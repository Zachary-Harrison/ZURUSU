# Data Log

### Unlimited Requests

The following is a table which dictates start and end dates for which the data is collected, describing which service(s) (if any) were attacked and where to find said data. 

> **Note**: Daylight Savings ended on November 5th, which affects the below timestamps for MST time.

| Start Time (RFC 3339) | End Time (RFC 3339)  | Start Time (MST)    | End Time (MST)      | Services Attacked                                                                                | File name                    |
| --------------------- | -------------------- | ------------------- | ------------------- | ------------------------------------------------------------------------------------------------ | ---------------------------- |
| 2023-10-07T17:00:00Z  | 2023-10-13T19:00:00Z | 2023-10-07 11:00 AM | 2023-10-13 01:00 PM | None                                                                                             | CPU_Usage-NORMAL.csv         |
| 2023-11-12T03:00:00Z  | 2023-11-12T03:59:00Z | 2023-11-11 08:00 PM | 2023-11-11 08:59 PM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T04:00:00Z  | 2023-11-12T05:59:00Z | 2023-11-11 09:00 PM | 2023-11-11 10:59 PM | None                                                                                             | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T06:00:00Z  | 2023-11-12T06:59:00Z | 2023-11-11 11:00 PM | 2023-11-11 11:59 PM | cart, currency, frontend, productcatalog, recommendation, shipping                               | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T07:00:00Z  | 2023-11-12T08:59:00Z | 2023-11-12 12:00 AM | 2023-11-12 01:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T09:00:00Z  | 2023-11-12T09:59:00Z | 2023-11-12 02:00 AM | 2023-11-12 02:59 AM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T10:00:00Z  | 2023-11-12T11:59:00Z | 2023-11-12 03:00 AM | 2023-11-12 04:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T12:00:00Z  | 2023-11-12T12:59:00Z | 2023-11-12 05:00 AM | 2023-11-12 05:59 AM | ad, cart, currency, frontend, productcatalog, recommendation                                     | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T13:00:00Z  | 2023-11-12T14:59:00Z | 2023-11-12 06:00 AM | 2023-11-12 07:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T15:00:00Z  | 2023-11-12T15:59:00Z | 2023-11-12 08:00 AM | 2023-11-12 08:59 AM | ad, cart, currency, frontend, productcatalog, recommendation, shipping                           | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T16:00:00Z  | 2023-11-12T17:59:00Z | 2023-11-12 09:00 AM | 2023-11-12 10:59 AM | None                                                                                             | CPU_Usage-OVERALL_ATTACK.csv |
| 2023-11-12T18:00:00Z  | 2023-11-12T18:59:00Z | 2023-11-12 11:00 AM | 2023-11-12 11:59 AM | ad, cart, checkout, currency, email, frontend, payment, productcatalog, recommendation, shipping | CPU_Usage-OVERALL_ATTACK.csv |


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
| 2023-10-07T17:00:00Z  | 2023-10-13T19:00:00Z | 2023-10-07 11:00 AM | 2023-10-13 01:00 PM | None                                                                                             | CPU_Usage-NORMAL.csv         |
| 2023-12-06T04:00:00Z  | 2023-12-06T05:59:00Z | 2023-12-05 9:00 PM  | 2023-12-05 10:59 PM | None                                                                                             | CPU_Usage-OVERALL_LIMITED_ATTACK.csv         |
| 2023-12-06T06:00:00Z  | 2023-12-06T06:59:00Z | 2023-12-05 11:00 PM | 2023-12-05 11:59 PM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T07:00:00Z  | 2023-12-06T08:59:00Z | 2023-12-06 12:00 AM | 2023-12-06 01:59 AM | None                                                                                             | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T09:00:00Z  | 2023-12-06T09:59:00Z | 2023-12-06 02:00 AM | 2023-12-06 02:59 AM | cart, currency, frontend, productcatalog, recommendation, shipping                               | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T10:00:00Z  | 2023-12-06T11:59:00Z | 2023-12-06 03:00 AM | 2023-12-06 04:59 AM | None                                                                                             | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T12:00:00Z  | 2023-12-06T12:59:00Z | 2023-12-06 05:00 AM | 2023-12-06 05:59 AM | ad, cart, currency, frontend, productcatalog                                                     | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T13:00:00Z  | 2023-12-06T14:59:00Z | 2023-12-06 06:00 AM | 2023-12-06 07:59 AM | None                                                                                             | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T15:00:00Z  | 2023-12-06T15:59:00Z | 2023-12-06 08:00 AM | 2023-12-06 08:59 AM | ad, cart, currency, frontend, productcatalog, recommendation                                     | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T16:00:00Z  | 2023-12-06T17:59:00Z | 2023-12-06 09:00 AM | 2023-12-06 10:59 AM | None                                                                                             | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T18:00:00Z  | 2023-12-06T18:59:00Z | 2023-12-06 11:00 AM | 2023-12-06 11:59 AM | ad, cart, currency, frontend, productcatalog, recommendation, shipping                           | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T19:00:00Z  | 2023-12-06T20:59:00Z | 2023-12-06 12:00 PM | 2023-12-06 01:59 PM | None                                                                                             | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |
| 2023-12-06T21:00:00Z  | 2023-12-06T21:59:00Z | 2023-12-06 02:00 PM | 2023-12-06 02:59 PM | ad, cart, checkout, currency, email, frontend, payment, productcatalog, recommendation, shipping | CPU_Usage-OVERALL_LIMITED_ATTACK.csv |

And the alternative table:

| Start Time (RFC 3339) | End Time (RFC 3339)  | ad  | cart | checkout | currency | email | frontend | payment | productcatalog | recommendation | shipping |
| --------------------- | -------------------- | :-: | :--: | :------: | :------: | :---: | :------: | :-----: | :------------: | :------------: | :------: |
| 2023-12-06T06:00:00Z  | 2023-12-06T06:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |                |          |
| 2023-12-06T09:00:00Z  | 2023-12-06T09:59:00Z |     |  X   |          |    X     |       |    X     |         |       X        |       X        |    X     |
| 2023-12-06T12:00:00Z  | 2023-12-06T12:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |                |          |
| 2023-12-06T15:00:00Z  | 2023-12-06T15:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |       X        |          |
| 2023-12-06T18:00:00Z  | 2023-12-06T18:59:00Z |  X  |  X   |          |    X     |       |    X     |         |       X        |       X        |    X     |
| 2023-12-06T21:00:00Z  | 2023-12-06T21:59:00Z |  X  |  X   |    X     |    X     |   X   |    X     |    X    |       X        |       X        |    X     |

