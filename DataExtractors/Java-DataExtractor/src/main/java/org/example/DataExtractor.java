package org.example;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.monitoring.v3.Aggregation;
import com.google.monitoring.v3.ListTimeSeriesRequest;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.*;

public class DataExtractor {
    private final String projectID;
    private final String inputDirectory;
    private final String outputDirectory;
    private static final String MERGED_FILENAME = "mergedResponse.json";
    private final String[] fieldnames = {"timestamp", "adservice","cartservice","checkoutservice","currencyservice","emailservice","frontend","paymentservice","productcatalogservice","recommendationservice","shippingservice", "label"};

    public DataExtractor(String project_id, String inputDirectory, String outputDirectory) {
        this.projectID = project_id;
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
    }

    public void createFiles(TimeInterval interval, Aggregation aggregation, String filter, int i, String pageToken) throws IOException, ApiException {
        System.out.printf("%-30s", "Creating page json files... ");
        MetricServiceClient client = MetricServiceClient.create();
        Path inputDirPath = Paths.get(this.inputDirectory);
        Files.createDirectories(inputDirPath);  // create the directory if it does not exist

        Gson gson = new Gson();
        while (i < Integer.MAX_VALUE) {
            ListTimeSeriesRequest.Builder requestBuilder = ListTimeSeriesRequest.newBuilder()
                    .setName("projects/" + this.projectID)
                    .setInterval(interval)
                    .setAggregation(aggregation)
                    .setFilter(filter)
                    .setPageSize(0)
                    .setPageToken(pageToken)
                    .setView(ListTimeSeriesRequest.TimeSeriesView.FULL);

            MetricServiceClient.ListTimeSeriesPagedResponse response = client.listTimeSeries(requestBuilder.build());
            // process each TimeSeries object in the response
            for (TimeSeries ts : response.iterateAll()) {
                try (Writer writer = Files.newBufferedWriter(inputDirPath.resolve("page" + i + ".json"))) {
                    gson.toJson(ts, writer);
                }
                i++;
            }
            pageToken = response.getNextPageToken();
            if (pageToken == null || pageToken.isEmpty()) {
                break;
            }else {
                System.out.println("\tpageToken(" + i + ") = " + pageToken);
            }
        }
        client.close();
        System.out.printf("%s\n", "Done.");
    }
    public void createFiles(TimeInterval interval, Aggregation aggregation, String filter) throws IOException, ApiException {
        createFiles(interval, aggregation, filter, 0, "");
    }

    public void mergeFiles() throws IOException {
        System.out.printf("%-30s", "Merging page json files... ");
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
        System.out.printf("%s\n", "Done.");
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

    public void convertToCSV(String outputFileName, List<String[]> attackPeriods) throws IOException {
        System.out.printf("%-30s", "Converting to CSV file... ");
        if (!outputFileName.endsWith(".csv")) {
            outputFileName += ".csv";
        }

        Path outputDirPath = Paths.get(this.outputDirectory);
        Files.createDirectories(outputDirPath);  // create the directory if it does not exist

        // Reading the time-series data
        Map<Long, Map<String, Double>> dataDict = new TreeMap<>();
        try (JsonReader reader = new JsonReader(new FileReader(Paths.get(this.inputDirectory, MERGED_FILENAME).toFile()))) {
            Gson gson = new Gson();

            reader.beginArray();
            while (reader.hasNext()) {
                JsonObject map = gson.fromJson(reader, JsonObject.class);

                String pod_name = map
                        .getAsJsonObject("resource_")
                        .getAsJsonObject("labels_")
                        .getAsJsonObject("mapData")
                        .get("pod_name").getAsString();
                String field = this.getField(pod_name);

                JsonArray pointsArray = map.getAsJsonArray("points_");
                for (JsonElement pointElement : pointsArray) {
                    JsonObject pointObject = pointElement.getAsJsonObject();

                    String seconds = pointObject
                            .getAsJsonObject("interval_")
                            .getAsJsonObject("endTime_")
                            .get("seconds_")
                            .getAsString();

                    Double value = pointObject
                            .getAsJsonObject("value_")
                            .get("value_")
                            .getAsDouble();

                    // Add the extracted data to dataDict
                    Long key = Long.parseLong(seconds);
                    Map<String, Double> innerMap = dataDict.computeIfAbsent(key, k -> new HashMap<>());
                    innerMap.put(field, value);
                }
            }
            reader.endArray();
        }

        // Generating the CSV from time-series data
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
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
                        if (value != null) {
                            record.add(df.format(value));
                        }
                    }
                }
                record.add(String.valueOf(this.getLabel(attackPeriods, entry.getKey())));
                printer.printRecord(record);
            }
        }
        System.out.printf("%s\n", "Done.");
    }

    public void convertToCSV(String outputFileName) throws IOException {
        convertToCSV(outputFileName, null);
    }
}