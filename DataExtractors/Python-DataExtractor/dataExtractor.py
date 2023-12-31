from google.cloud import monitoring_v3
from sys import maxsize
import pandas as pd
import json
import os
from csv import DictWriter
from dateutil import parser

class DataExtractor:
    def __init__(self, project_id, inputDirectory="pages", outputDirectory="output"):
        self.PROJECT_ID = project_id
        self.inputDirectory = inputDirectory
        self.outputDirectory = outputDirectory
        self.fieldnames = ["timestamp", "adservice","cartservice","checkoutservice","currencyservice",
                           "emailservice","frontend","paymentservice","productcatalogservice",
                           "recommendationservice","shippingservice", "label"]

        # Create the input and output directories if they don't exist
        if not os.path.exists(self.inputDirectory):
            os.makedirs(self.inputDirectory)
        if not os.path.exists(self.outputDirectory):
            os.makedirs(self.outputDirectory)

    def createFiles(self, interval, aggregation, filter="", i=1, page_token=""):
        print("{:<40}".format("Creating page json files... "), end="", flush=True)
        client = monitoring_v3.MetricServiceClient()
        # Keep calling API until we read all the pages
        while i < maxsize:
            results = client.list_time_series(
                request={
                    "name": f"projects/{self.PROJECT_ID}",
                    "filter": filter,
                    "interval": interval,
                    "view": monitoring_v3.ListTimeSeriesRequest.TimeSeriesView.FULL,
                    "aggregation": aggregation,
                    # "page_size": 25_000,
                    "page_token": page_token,
                },
                timeout=300
            )
            response = monitoring_v3.ListTimeSeriesResponse.to_json(results._response)
            fileName = f"{self.inputDirectory}/page{i}.json"
            with open(fileName, "w") as outfile:
                outfile.write(response)
            page_token = results._response.next_page_token
            if not page_token:
                break
            i += 1
        print("Done.")
                
    def mergeFiles(self):
        print("{:<40}".format("Merging page json files... "), end="", flush=True)
        output_list = []
        obj = os.scandir(self.inputDirectory)
        for entry in obj:
            if ("mergedResponse" in entry.name):
                continue
            with open(f"{self.inputDirectory}/{entry.name}", "rb") as infile:
                output_list.append(json.load(infile))

        all_time_series = []
        for json_file in output_list:
            all_time_series += json_file['timeSeries']

        textfile_merged = open(f'{self.inputDirectory}/mergedResponse.json', 'w')
        json.dump({ "timeSeries": all_time_series }, textfile_merged, indent=2)
        textfile_merged.close()
        print("Done.")

    def _getField(self, rawField):
        field = ""
        for fieldname in self.fieldnames:
            if rawField.startswith(fieldname):
                field = fieldname
        return field
    
    def _getLabel(self, attackPeriods, timestamp):
        # no need to iterate if it's clearly outside the range
        if timestamp < attackPeriods[0][0] or timestamp > attackPeriods[-1][-1]:
            return 0

        for start_time, end_time in attackPeriods:
            if timestamp >= start_time and timestamp <= end_time:
                return 1
        return 0

    def convertToCSV(self, outputFileName="output.csv", attackPeriods=None):
        print("{:<40}".format("Converting to CSV file... "), end="", flush=True)
        if not outputFileName.endswith(".csv"):
            outputFileName += ".csv"

        temp = None
        with open(f"{self.inputDirectory}/mergedResponse.json", "rb") as file:
            temp = json.load(file)
        timeSeries = temp['timeSeries']

        # first making dictionary for quick search/add 
        dataDict = dict()
        for series in timeSeries:
            field = self._getField(series['resource']['labels']['pod_name'])
            if not field:
                continue
            
            for point in series['points']:
                posixTime = parser.isoparse(point['interval']['startTime']).timestamp()
                if posixTime in dataDict.keys():
                    dataDict[posixTime][field] = point['value']['doubleValue']
                else:
                    dataDict[posixTime] = { field: point['value']['doubleValue']}

        # converting to DataFrame
        sortedKeys = list(dataDict.keys())
        sortedKeys.sort()
        dataDict = {i: dataDict[i] for i in sortedKeys}
        df = pd.DataFrame.from_dict(dataDict, orient='index')
        df = df.reindex(sorted(df.columns), axis=1)     # alphabetizing columns
        df.sort_index(axis=0)                           # sorting timestamps chronologically
        df[self.fieldnames[-1]] = df.index
        df[self.fieldnames[-1]] = df[self.fieldnames[-1]].apply(lambda x: self._getLabel(attackPeriods, x))
        df.to_csv(f"{self.outputDirectory}/{outputFileName}", index_label=self.fieldnames[0])
        print("Done.")
        
