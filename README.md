# ZURUSU

## Results

In this project, I built two different Proof-Of-Concept (POC) models, both with ROC AUC scores greater than 0.99. 

See [dataLog.md](dataLog.md) for more information regarding when the datasets were created and when different services were affected.

### Model version 1: Unlimited Requests

|                                    Learning Curve                                    |     |                      ROC Curve                       |
| :----------------------------------------------------------------------------------: | :-: | :--------------------------------------------------: |
|                  ![LearningCurve_1.png](images/LearningCurve_1.png)                  |     |            ![ROC_1.png](images/ROC_1.png)            |


### Model version 2: Limited Requests

In this model, I limit the overall CPU usage during attacks to 1.5x the usage during normal periods.

|                                    Learning Curve                                    |     |                      ROC Curve                       |
| :----------------------------------------------------------------------------------: | :-: | :--------------------------------------------------: |
|                  ![LearningCurve_1.png](images/LearningCurve_2.png)                  |     |            ![ROC_1.png](images/ROC_2.png)            |


> **Note**: Model version 2 uses a different Normal dataset than Model version 1. I'm not sure if this is due to changes in the [microservices-demo](https://github.com/GoogleCloudPlatform/microservices-demo) codebase, or if GKE clusters have unique server behavior. 

## Jupyter Notebooks

You can download and run either of these Jupyter notebooks without any additional configuration:
- [model version 1](USADv1.ipynb)
- [model version 2](USADv2.ipynb)

However, if you want to expand on this project by generating your own dataset(s), follow the [Build Instructions](#build-instructions) below.

## Build Instructions

### 1. Clone this repository

```bash
git clone https://github.com/Zachary-Harrison/ZURUSU.git
```

### 2. Set up microservices-demo

1. Follow the [Quickstart (GKE) instructions](https://github.com/GoogleCloudPlatform/microservices-demo/tree/main#quickstart-gke) provided by microservices-demo. 
   - Be sure to clone the microservices-demo in a folder that is not already a repository. 
2. Create a service account [here](https://console.cloud.google.com/iam-admin/serviceaccounts/) for your project
3. [Deploy Online Boutique variations with Kustomize](https://github.com/GoogleCloudPlatform/microservices-demo/tree/main/kustomize#deploy-online-boutique-with-kustomize)
   > Note: This documents additional ways to modify app behavior, along with the [development guide](https://github.com/GoogleCloudPlatform/microservices-demo/blob/main/docs/development-guide.md).

### 3. Simulating attack behavior

In the [JavaScript](JavaScript/) directory, I've provided a few files:
- [immediateAttacker.js](JavaScript/immediateAttacker.js): Makes requests while running. Mainly used during development to see if you've got things working properly. Use it like this, replacing `EXTERNAL_IP` with the frontend's external IP.
   ```bash
    node immediateAttacker.js [http://EXTERNAL_IP]
   ```
- [timedAttacker.js](JavaScript/timedAttacker.js): Makes requests during specific time intervals. Use it like this, replacing `EXTERNAL_IP` with the frontend's external IP.
  1. Update the `BASE_URL` variable to equal the URL of your webpage.
  2. Update the `ATTACKS` variable.
  3. Run the following in your terminal:
   ```bash
    node timedAttacker.js
   ```
- [timedAttackerLimited.js](JavaScript/timedAttackerLimited.js): Basically the same as the timedAttacker, but this one limits the number of requests. Use it like this, replacing `EXTERNAL_IP` with the frontend's external IP.
  1. Update the `BASE_URL` variable to equal the URL of your webpage.
  2. Update the `ATTACKS` variable.
  3. Run the following in your terminal:
   ```bash
    node timedAttackerLimited.js
   ```
> **Tip:** Be sure to run this script on a computer that you don't use regularly, because you will need to keep it running during all your attacks.

### 4. Retrieving Data

I have provided two different implementations to retrieve data from this project. The Java version is recommended, as Java more easily supports the reading and creation of large files. 

#### DataExtractor (Java)

1. Open the [DataExtractor (Java)](DataExtractors/Java-DataExtractor/) folder in [IntelliJ](https://www.jetbrains.com/idea/download/?section=windows).
2. Add Run/Debug Configuration. Click [here](https://www.jetbrains.com/help/idea/run-debug-configuration.html) for more information.
   - Main class: Main.java
   - Program arguments: -Xmx2048m 
   - Environment variables: PROJECT_ID=REPLACE_ME
     - Replace REPLACE_ME with your project id, which you can find on your [GCP Dashboard](https://console.cloud.google.com/home/dashboard?).
3. Update the `startDate` and `endDate` variables in `Main.java`. These dates must be in the UTC timezone. 
4. Update the `attackPeriods` variable in `Main.java`. These dates must be in the UTC timezone.
5. Run your Run/Debug configuration!


#### DataExtractor (Python)

1. Install the [`google-cloud-monitoring`](https://pypi.org/project/google-cloud-monitoring/) python package:
   ```bash
   pip install google-cloud-monitoring
   ```
2. Open the [DataExtractor (Python)](DataExtractors/Python-DataExtractor/) in your favorite IDE.
3. Use [`dotenv`](https://pypi.org/project/python-dotenv/) to set up the PROJECT_ID environment variable. To do so, execute the following command in a terminal, replacing `REPLACE_ME` with your project id (found on your [GCP Dashboard](https://console.cloud.google.com/home/dashboard?)):
   ```bash
   echo "PROJECT_ID=REPLACE_ME" > .env
   ```
4. Update the `interval` variable according to the start and end time for the data you'd like to collect. These dates must be in the UTC timezone. 
5. Modify the `attack_periods` variable in `main.py`. These dates must be in the UTC timezone. 
6. Execute your program:
   ```bash
   python main.py
   ```

#### Relevant Resources

- [Retrieving time-series data](https://cloud.google.com/monitoring/custom-metrics/reading-metrics) from a Google Cloud Server.
- Converting between [local time and UTC time](https://www.worldtimebuddy.com/).
- What is [RFFC 3339 format](https://www.rfc-editor.org/rfc/rfc3339#:~:text=Abstract%20This%20document%20defines%20a,times%20using%20the%20Gregorian%20calendar.).

### 5. (Optional) EDA

If you want to see what your data looks like, use the [ATTACK_EDA.ipynb](ATTACK_EDA.ipynb) notebook that I've provided! You can use [Google Collab](https://colab.google/) or use Visual Studio Code (follow the instructions [here](https://code.visualstudio.com/docs/datascience/jupyter-notebooks)).

### 6. Create Your Model

Finally, you can create your model by running the [USAD.ipynb](USAD.ipynb) notebook, substituting your data for the default data. You only need to modify the `Environment` section; leave everything else the same unless you change the structure for the CSV files. 

## Future Works

While this is only a POC model, it shows some real potential for using Machine Learning in preventing hackers from attacking various components of microservices! Here are some potential ideas to expand on this work:
- **Idea 1**: Document the project on GitHub so that anyone can reproduce the same results.
- **Idea 2**: Re-produce these results using the other models.
- **Idea 3**: Make more realistic, covert attacks.
- **Idea 4**: Implement a real-time defense strategy using our model.
- **Idea 5**: Make a more generalizable normal dataset (this is related to the need for two different Normal datasets in Model 1 vs Model 2).

## Citations

If you liked this repository, please consider checking these ones out. This project would've been impossible without them:
- [microservices-demo](https://github.com/GoogleCloudPlatform/microservices-demo/tree/main)
- [usad](https://github.com/manigalati/usad)
    ```
    Audibert, J., Michiardi, P., Guyard, F., Marti, S., Zuluaga, M. A. (2020).
    USAD : UnSupervised Anomaly Detection on multivariate time series.
    Proceedings of the 26th ACM SIGKDD International Conference on Knowledge Discovery & Data Mining, August 23-27, 2020
    ```

