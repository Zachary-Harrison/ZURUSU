from google.cloud import monitoring_v3
from sys import maxsize
import pandas as pd
import json
import os
from csv import DictWriter
from dateutil import parser

class DataExtractor:
    def __init__(self, inputDirectory="pages", outputDirectory="output"):
        self.inputDirectory = inputDirectory
        self.outputDirectory = outputDirectory
        self.fieldnames = ["timestamp", "adservice","cartservice","checkoutservice","currencyservice","emailservice","frontend","paymentservice","productcatalogservice","recommendationservice","shippingservice", "label"]

    def createFiles(self, interval, aggregation, filter="", i=1, page_token=""):
        client = monitoring_v3.MetricServiceClient()
        # Keep calling API until we read all the pages
        while i < maxsize:
            results = client.list_time_series(
                request={
                    "name": "projects/zurusu-500420",
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
        print("Done creating page json files")
                
    def mergeFiles(self):
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
        print("Done merging page json files")

    def createMergedFile(self, interval, aggregation, filter=""):
        client = monitoring_v3.MetricServiceClient()
        page_token = ""
        # Keep calling API until we read all the pages
        i = 0
        while i < maxsize:
            results = client.list_time_series(
                request={
                    "name": "projects/zurusu-500420",
                    "filter": filter,
                    "interval": interval,
                    "view": monitoring_v3.ListTimeSeriesRequest.TimeSeriesView.FULL,
                    "aggregation": aggregation,
                    # "page_size": 25_000,
                    "page_token": page_token,
                },
                timeout=300
            )
            response = json.loads(monitoring_v3.ListTimeSeriesResponse.to_json(results._response))
            page_token = results._response.next_page_token
            if not page_token:
                break
            i += 1

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
        
