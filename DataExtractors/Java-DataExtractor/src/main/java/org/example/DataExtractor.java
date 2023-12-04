package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.monitoring.v3.Aggregation;
import com.google.monitoring.v3.ListTimeSeriesRequest;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

public class DataExtractor {
   private static final String PROJECT_ID = "[PROJECT_ID]";
    private static final String MERGED_FILENAME = "mergedResponse.json";
    private final String[] fieldnames = {"timestamp", "adservice","cartservice","checkoutservice","currencyservice","emailservice","frontend","paymentservice","productcatalogservice","recommendationservice","shippingservice", "label"};
    private String inputDirectory;
    private String outputDirectory;


    public DataExtractor(String inputDirectory, String outputDirectory) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public void createFiles(TimeInterval interval, Aggregation aggregation, String filter) throws IOException, ApiException {
        System.out.println("creating files...");
        MetricServiceClient client = MetricServiceClient.create();
        Path inputDirPath = Paths.get(this.inputDirectory);
        Files.createDirectories(inputDirPath);  // create the directory if it does not exist

        Gson gson = new Gson();
        String pageToken = "";
        int i = 0;
        while (i < Integer.MAX_VALUE) {
            ListTimeSeriesRequest.Builder requestBuilder = ListTimeSeriesRequest.newBuilder()
                    .setName("projects/" + PROJECT_ID)
                    .setFilter(filter)
                    .setInterval(interval)
                    .setAggregation(aggregation)
                    .setPageSize(0)
                    .setPageToken(pageToken)
                    .setView(ListTimeSeriesRequest.TimeSeriesView.FULL);
            MetricServiceClient.ListTimeSeriesPagedResponse response = client.listTimeSeries(requestBuilder.build());
            for (TimeSeries ts : response.iterateAll()) {
                // process each TimeSeries object
                try (Writer writer = Files.newBufferedWriter(inputDirPath.resolve("page" + i + ".json"))) {
                    gson.toJson(ts, writer);
                }
                i++;
            }
            pageToken = response.getNextPageToken();
            if (pageToken == null || pageToken.isEmpty()) {
                break;
            }
        }
        client.close();
    }

    private String getField(String rawField) {
        String field = "";
        for (String fieldname : this.fieldnames) {
            if (rawField.startsWith(fieldname)) {
                field = fieldname;
            }
        }
        return field;
    }

    private int getLabel(List<String[]> attackPeriods, long timestamp) {
        if (attackPeriods == null) {
            return 0;
        }
        for (String[] periods : attackPeriods) {
            long startTime = Instant.parse(periods[0]).getEpochSecond();
            long endTime = Instant.parse(periods[1]).getEpochSecond();
            if (timestamp >= startTime && timestamp <= endTime) {
                return 1;
            }
        }
        return 0;
    }

    public void mergeFiles() throws IOException {
        System.out.println("merging files...");
        Gson gson = new Gson();

        try (Writer writer = Files.newBufferedWriter(Paths.get(this.inputDirectory, MERGED_FILENAME))) {
            writer.write("[");  // Start of JSON array

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(this.inputDirectory))) {
                boolean firstFile = true;
                for (Path p : directoryStream) {
                    if (!p.getFileName().toString().equals(MERGED_FILENAME)) {  // Skip the mergedResponse.json file
                        if (!firstFile) {
                            writer.write(",");  // Separate JSON objects with commas
                        }
                        try (Reader reader = Files.newBufferedReader(p)) {
                            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                            gson.toJson(jsonObject, writer);
                            firstFile = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            writer.write("]");  // End of JSON array
        }
    }

    private String extractPodName(JsonParser parser) throws IOException {
        String pod_name = null;
        parser.nextToken(); // move to the start of the object
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if ("labels_".equals(parser.getCurrentName())) {
                parser.nextToken(); // move to the start of the object
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    if ("mapData".equals(parser.getCurrentName())) {
                        parser.nextToken(); // move to the start of the object
                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            if ("pod_name".equals(parser.getCurrentName())) {
                                parser.nextToken(); // move to the value
                                pod_name = parser.getText();
                            }
                        }
                    }
                }
            }
        }
        return pod_name;
    }

    private Map.Entry<String, Double> extractPoint(JsonParser parser) throws IOException {
        String seconds = null;
        double value = 0;
        parser.nextToken(); // move to the start of the array
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if ("interval_".equals(parser.getCurrentName())) {
                    parser.nextToken(); // move to the start of the object
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        if ("startTime_".equals(parser.getCurrentName())) {
                            parser.nextToken(); // move to the start of the object
                            while (parser.nextToken() != JsonToken.END_OBJECT) {
                                if ("seconds_".equals(parser.getCurrentName())) {
                                    parser.nextToken(); // move to the value
                                    seconds = parser.getText();
                                }
                            }
                        }
                    }
                } else if ("value_".equals(parser.getCurrentName())) {
                    parser.nextToken(); // move to the start of the object
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        if ("value_".equals(parser.getCurrentName())) {
                            parser.nextToken(); // move to the value
                            value = parser.getDoubleValue();
                        }
                    }
                }
            }
        }
        return seconds != null ? new AbstractMap.SimpleEntry<>(seconds, value) : null;
    }

    public void convertToCSV(String outputFileName, List<String[]> attackPeriods) throws IOException {
        System.out.println("converting files...");
        if (!outputFileName.endsWith(".csv")) {
            outputFileName += ".csv";
        }

        Path outputDirPath = Paths.get(this.outputDirectory);
        Files.createDirectories(outputDirPath);  // create the directory if it does not exist


        JsonFactory factory = new JsonFactory();
        Map<Long, Map<String, Double>> dataDict = new TreeMap<>();
        try (Reader fileReader = Files.newBufferedReader(Paths.get(this.inputDirectory, MERGED_FILENAME));
             JsonParser parser = factory.createParser(fileReader)) {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                String field = null;
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    String name = parser.getCurrentName();
                    if ("resource_".equals(name)) {
                        String pod_name = extractPodName(parser);
                        field = this.getField(pod_name);
                    } else if ("points_".equals(name)) {
                        Map.Entry<String, Double> point = extractPoint(parser);
                        if (point != null && field != null) {
                            long posixTime = Instant.ofEpochSecond(Long.parseLong(point.getKey())).getEpochSecond();
                            dataDict.computeIfAbsent(posixTime, k -> new HashMap<>()).put(field, point.getValue());
                        }
                    }
                }
            }
        }
                            

        try (CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(
                Paths.get(this.outputDirectory, outputFileName)),
                CSVFormat.DEFAULT.withHeader(this.fieldnames)) // Add this line to print the headers
        ) {
            for (Map.Entry<Long, Map<String, Double>> entry : dataDict.entrySet()) {
                List<String> record = new ArrayList<>();
                for (String fieldname : this.fieldnames) {
                    if (fieldname.equals("timestamp")) {
                        record.add(String.valueOf(entry.getKey()));
                    } else {
                        Double value = entry.getValue().get(fieldname);
                        record.add(value != null ? String.valueOf(value) : "");
                    }
                }
                record.add(String.valueOf(this.getLabel(attackPeriods, entry.getKey())));
                printer.printRecord(record);
            }
        }
    }

    public void convertToCSV(String outputFileName) throws IOException {
        convertToCSV(outputFileName, null);
    }
}