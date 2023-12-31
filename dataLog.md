# Data Log

### Unlimited Requests

The following is a table which dictates start and end dates for which the data is collected, describing which service(s) (if any) were attacked and where to find said data. 

> **Note**: Daylight Savings ended on November 5th, which affects the below timestamps for MST time.

| Start Time (RFC 3339) | End Time (RFC 3339)  | Start Time (MST)    | End Time (MST)      | Services Attacked                                                                                | File name                    |
| --------------------- | -------------------- | ------------------- | ------------------- | ------------------------------------------------------------------------------------------------ | ---------------------------- |
| 2023-10-07T17:00:00Z  | 2023-10-13T19:00:00Z | 2023-10-07 11:00 AM | 2023-10-13 01:00 PM | None                                                                                             | CPU_Usage-NORMALv1.csv         |
| 2023-11-12T03:00:00Z  | 2023-11-12T03:59:00Z | 2023-11-11 08:00 PM | 2023-11-11 08:59 PM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T04:00:00Z  | 2023-11-12T05:59:00Z | 2023-11-11 09:00 PM | 2023-11-11 10:59 PM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T06:00:00Z  | 2023-11-12T06:59:00Z | 2023-11-11 11:00 PM | 2023-11-11 11:59 PM | cart, currency, frontend, productcatalog, recommendation, shipping                               | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T07:00:00Z  | 2023-11-12T08:59:00Z | 2023-11-12 12:00 AM | 2023-11-12 01:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T09:00:00Z  | 2023-11-12T09:59:00Z | 2023-11-12 02:00 AM | 2023-11-12 02:59 AM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T10:00:00Z  | 2023-11-12T11:59:00Z | 2023-11-12 03:00 AM | 2023-11-12 04:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T12:00:00Z  | 2023-11-12T12:59:00Z | 2023-11-12 05:00 AM | 2023-11-12 05:59 AM | ad, cart, currency, frontend, productcatalog, recommendation                                     | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T13:00:00Z  | 2023-11-12T14:59:00Z | 2023-11-12 06:00 AM | 2023-11-12 07:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T15:00:00Z  | 2023-11-12T15:59:00Z | 2023-11-12 08:00 AM | 2023-11-12 08:59 AM | ad, cart, currency, frontend, productcatalog, recommendation, shipping                           | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T16:00:00Z  | 2023-11-12T17:59:00Z | 2023-11-12 09:00 AM | 2023-11-12 10:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv1.csv |
| 2023-11-12T18:00:00Z  | 2023-11-12T18:59:00Z | 2023-11-12 11:00 AM | 2023-11-12 11:59 AM | ad, cart, checkout, currency, email, frontend, payment, productcatalog, recommendation, shipping | CPU_Usage-OVERALL_ATTACKv1.csv |


Here is an alternative table that documents when each service is attacked, where an "X" indicates the service is being attacked:

| Start Time (RFC 3339) | End Time (RFC 3339)  | ad  | cart | checkout | currency | email | frontend | payment | productcatalog | recommendation | shipping |
| --------------------- | -------------------- | :-: | :--: | :------: | :------: | :---: | :------: | :-----: | :------------: | :------------: | :------: |
| 2023-11-12T03:00:00Z  | 2023-11-12T03:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |                |          |
| 2023-11-12T06:00:00Z  | 2023-11-12T06:59:00Z |     |  X   |          |    X     |       |    X     |         |       X        |       X        |    X     |
| 2023-11-12T09:00:00Z  | 2023-11-12T09:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |                |          |
| 2023-11-12T12:00:00Z  | 2023-11-12T12:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |       X        |          |
| 2023-11-12T15:00:00Z  | 2023-11-12T15:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |       X        |    X     |
| 2023-11-12T18:00:00Z  | 2023-11-12T18:59:00Z |  X  |  X   |    X     |    X     |   X   |    X     |    X    |       X        |       X        |    X     |


### Limited Requests

In this time series data, we try to limit anomalous CPU usage to 1.5x that of the normal CPU usage. 

| Start Time (RFC 3339) | End Time (RFC 3339)  | Start Time (MST)    | End Time (MST)      | Services Attacked                                                                                | File name                    |
| --------------------- | -------------------- | ------------------- | ------------------- | ------------------------------------------------------------------------------------------------ | ---------------------------- |
| 2023-11-15T19:00:00Z  | 2023-11-26T19:00:00Z | 2023-11-15 12:00 PM | 2023-11-26 12:00 PM | None                                                                                             | CPU_Usage-NORMALv2.csv         |
| 2023-12-10T03:00:00Z  | 2023-12-10T03:59:00Z | 2023-12-09 08:00 PM | 2023-12-09 08:59 PM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T04:00:00Z  | 2023-12-10T05:59:00Z | 2023-12-09 09:00 PM | 2023-12-09 10:59 PM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T06:00:00Z  | 2023-12-10T06:59:00Z | 2023-12-09 11:00 PM | 2023-12-09 11:59 PM | cart, currency, frontend, productcatalog, recommendation, shipping                               | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T07:00:00Z  | 2023-12-10T08:59:00Z | 2023-12-10 12:00 AM | 2023-12-10 01:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T09:00:00Z  | 2023-12-10T09:59:00Z | 2023-12-10 02:00 AM | 2023-12-10 02:59 AM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T10:00:00Z  | 2023-12-10T11:59:00Z | 2023-12-10 03:00 AM | 2023-12-10 04:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T12:00:00Z  | 2023-12-10T12:59:00Z | 2023-12-10 05:00 AM | 2023-12-10 05:59 AM | ad, cart, currency, frontend, productcatalog, recommendation                                     | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T13:00:00Z  | 2023-12-10T14:59:00Z | 2023-12-10 06:00 AM | 2023-12-10 07:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T15:00:00Z  | 2023-12-10T15:59:00Z | 2023-12-10 08:00 AM | 2023-12-10 08:59 AM | ad, cart, currency, frontend, productcatalog, recommendation, shipping                           | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T16:00:00Z  | 2023-12-10T17:59:00Z | 2023-12-10 09:00 AM | 2023-12-10 10:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACKv2.csv |
| 2023-12-10T18:00:00Z  | 2023-12-10T18:59:00Z | 2023-12-10 11:00 AM | 2023-12-10 11:59 AM | ad, cart, checkout, currency, email, frontend, payment, productcatalog, recommendation, shipping | CPU_Usage-OVERALL_ATTACKv2.csv |

And the alternative table:

| Start Time (RFC 3339) | End Time (RFC 3339)  | ad  | cart | checkout | currency | email | frontend | payment | productcatalog | recommendation | shipping |
| --------------------- | -------------------- | :-: | :--: | :------: | :------: | :---: | :------: | :-----: | :------------: | :------------: | :------: |
| 2023-12-10T03:00:00Z  | 2023-12-10T03:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |                |          |
| 2023-12-10T06:00:00Z  | 2023-12-10T06:59:00Z |     |  X   |          |    X     |       |    X     |         |       X        |       X        |    X     |
| 2023-12-10T09:00:00Z  | 2023-12-10T09:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |                |          |
| 2023-12-10T12:00:00Z  | 2023-12-10T12:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |       X        |          |
| 2023-12-10T15:00:00Z  | 2023-12-10T15:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |       X        |    X     |
| 2023-12-10T18:00:00Z  | 2023-12-10T18:59:00Z |  X  |  X   |    X     |    X     |   X   |    X     |    X    |       X        |       X        |    X     |
