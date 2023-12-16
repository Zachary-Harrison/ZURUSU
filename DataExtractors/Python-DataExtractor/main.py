from dataExtractor import DataExtractor
from google.cloud import monitoring_v3
import sys
from dotenv import load_dotenv
import os
from datetime import datetime, timezone

def main():
    interval = monitoring_v3.TimeInterval(
            {
                "start_time": "2023-11-14T19:00:00Z",
                "end_time": "2023-11-27T19:00:00Z",
            }
        )
    
    aggregation = monitoring_v3.Aggregation(
            {
                "alignment_period": {"seconds": 10},
                "per_series_aligner": monitoring_v3.Aggregation.Aligner.ALIGN_RATE,
                "group_by_fields": [
                    "metadata.system_labels.\"service_name\""
                ]
            }
        )
    
    attack_periods = [
        ("2023-12-10T03:00:00Z", "2023-12-10T03:59:00Z"),
        ("2023-12-10T06:00:00Z", "2023-12-10T06:59:00Z"),
        ("2023-12-10T09:00:00Z", "2023-12-10T09:59:00Z"),
        ("2023-12-10T12:00:00Z", "2023-12-10T12:59:00Z"),
        ("2023-12-10T15:00:00Z", "2023-12-10T15:59:00Z"),
        ("2023-12-10T18:00:00Z", "2023-12-10T18:59:00Z"),
    ]

    # converting attack_periods into posixTimes
    attack_periods = [(datetime.strptime(start, "%Y-%m-%dT%H:%M:%SZ"), datetime.strptime(end, "%Y-%m-%dT%H:%M:%SZ")) for start, end in attack_periods]
    attack_periods = [(start.replace(tzinfo=timezone.utc).timestamp(), end.replace(tzinfo=timezone.utc).timestamp()) for start, end in attack_periods]
    
    filter = "metric.type=\"kubernetes.io/container/cpu/core_usage_time\" \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"default-http-backend\") \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"frontend-external\") \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"gmp-operator\") \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"kube-dns\") \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"metrics-server\") \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"opentelemetrycollector\") \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"alertmanager\") \
                        metadata.system_labels.\"service_name\"!=monitoring.regex.full_match(\"redis-cart\")" 
    load_dotenv()
    extractor = DataExtractor(os.getenv("PROJECT_ID"))
    extractor.createFiles(interval, aggregation, filter)
    extractor.mergeFiles()
    # extractor.convertToCSV(outputFileName="CPU_Usage-NORMALv2.csv")
    extractor.convertToCSV(outputFileName="CPU_Usage-OVERAL_ATTACKv2.csv", attackPeriods=attack_periods)

main()