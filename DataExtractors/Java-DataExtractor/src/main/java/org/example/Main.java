package org.example;

import com.google.monitoring.v3.*;
import com.google.protobuf.util.Timestamps;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        SimpleDateFormat rfc3339Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date startDate = rfc3339Formatter.parse("2023-10-07T17:00:00Z");
        Date endDate = rfc3339Formatter.parse("2023-10-13T19:00:00Z");
        List<String[]> attackPeriods = Arrays.asList(
                new String[]{"2023-12-10T03:00:00Z", "2023-12-10T03:59:00Z"},
                new String[]{"2023-12-10T06:00:00Z", "2023-12-10T06:59:00Z"},
                new String[]{"2023-12-10T09:00:00Z", "2023-12-10T09:59:00Z"},
                new String[]{"2023-12-10T12:00:00Z", "2023-12-10T12:59:00Z"},
                new String[]{"2023-12-10T15:00:00Z", "2023-12-10T15:59:00Z"},
                new String[]{"2023-12-10T18:00:00Z", "2023-12-10T18:59:00Z"}
        );

        TimeInterval interval =
                TimeInterval.newBuilder()
                        .setStartTime(Timestamps.fromMillis(startDate.getTime()))
                        .setEndTime(Timestamps.fromMillis(endDate.getTime()))
                        .build();

        Aggregation aggregation = Aggregation.newBuilder()
                .setAlignmentPeriod(com.google.protobuf.Duration.newBuilder().setSeconds(10).build())
                .setPerSeriesAligner(Aggregation.Aligner.ALIGN_RATE)
                .addGroupByFields("metadata.system_labels.\"service_name\"")
                .build();

        String filter = "metric.type=\"kubernetes.io/container/cpu/core_usage_time\" "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"default-http-backend\") "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"frontend-external\") "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"gmp-operator\") "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"kube-dns\") "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"metrics-server\") "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"opentelemetrycollector\") "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"alertmanager\") "
                + "metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"redis-cart\")";

        DataExtractor extractor = new DataExtractor(System.getenv("PROJECT_ID"), "pages", "output");
        try {
            extractor.createFiles(interval, aggregation, filter);
            extractor.mergeFiles();
//            extractor.convertToCSV("CPU_Usage-NORMAL.csv");
            extractor.convertToCSV("CPU_Usage-OVERALL_ATTACKv2.csv", attackPeriods);
        }
        catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}