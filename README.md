# ZURUSU

In this project, I built a Proof-Of-Concept (POC) model capable of classifying microservice server attacks with 99.5% accuracy. As proof, here are the results:

|                                    Learning Curve                                    |     |                      ROC Curve                       |
| :----------------------------------------------------------------------------------: | :-: | :--------------------------------------------------: |
|                  ![LearningCurve_1.png](images/LearningCurve_1.png)                  |     |            ![ROC_1.png](images/ROC_1.png)            |


## Reproducing

If all you want to do is reproduce the same results I got, you can download and run the [usad notebook](USAD.ipynb)

However, if you want to expand on this project by generating your own dataset(s), follow the below [Build Instructions](#build-instructions).

## Build Instructions

### 1. Clone this repository

```bash
git clone https://github.com/Zachary-Harrison/ZURUSU.git
```

### 2. Set up microservices-demo

Follow the [Quickstart (GKE) instructions](https://github.com/GoogleCloudPlatform/microservices-demo/tree/main#quickstart-gke) provided by microservices-demo. 

### 3. (Optional) Modify app behavior

The creators of microservices-demo made an amazing [development guide](https://github.com/GoogleCloudPlatform/microservices-demo/blob/main/docs/development-guide.md) for how to run and develop their app locally. This is how you can change the behavior for `loadgenerator` or other microservices.

As a brief summary, you need to create your own Docker images (I recommend using [IntelliJ](https://www.jetbrains.com/idea/download/?section=windows) to help streamline this process). Then you use `skaffold` and `kubectl` to modify the default configuration files to use your image repository instead of the static one provided.

### 4. Simulating attack behavior

In the [JavaScript](JavaScript/) directory, I've provided a few files:
1. [immediateAttacker.js](JavaScript/immediateAttacker.js): Makes requests while running. Mainly used during development to see if you've got things working properly. Use it like this, replacing [http://EXTERNAL_IP] with the frontend's external IP.
   ```bash
    node immediateAttacker.js [http://EXTERNAL_IP]
   ```
2. [timedAttacker.js](JavaScript/timedAttacker.js): Makes requests during specific time intervals. Use it like this, replacing [http://EXTERNAL_IP] with the frontend's external IP.
   ```bash
    node timedAttacker.js [http://EXTERNAL_IP]
   ```
> **Tip:** Be sure to run this script on a computer that you don't use regularly, because you will need to keep it running during all your attacks.

### 5. Retrieving Data

Depending on how large your dataset is, you might run into various problems regarding heap or stack space (I know I did). For this reason, I have provided two separate implementations to retrieve your dataset:
- [DataExtractor (Java)](DataExtractors/Java-DataExtractor/): To use this project, I recommend opening it in IntelliJ and updating the [Main.java](DataExtractors/Java-DataExtractor/src/main/java/org/example/Main.java) and [DataExtractor.java](DataExtractors/Java-DataExtractor/src/main/java/org/example/DataExtractor.java) files to point to your app.
  - The CSV converting is much more robust in this version.
- [DataExtractor (Python)](DataExtractors/Python-DataExtractor/)
  - This version frequently encounters segmentation faults, but it allows you to start at a specific page by grabbing its nextPageToken attribute (at the bottom of the page.json response).

### 6. (Optional) EDA

If you want to see what your data looks like, use the [ATTACK_EDA.ipynb](ATTACK_EDA.ipynb) notebook that I've provided! You can use [Google Collab](https://colab.google/) or use Visual Studio Code (follow the instructions [here](https://code.visualstudio.com/docs/datascience/jupyter-notebooks)).

### 7. Create Your Model

Finally, you can create your model by running the [USAD.ipynb](USAD.ipynb) notebook, substituting your data for the default data. Be sure to modify the notebook so that it's reading your data from the correct spot.


# Future Works

While this is only a POC model, it shows some real potential for using Machine Learning in preventing hackers from attacking various components of microservices! 
- **Idea 1**: Document the project on GitHub so that anyone can reproduce the same results.
- **Idea 2**: Re-produce these results using the other models that Xiao provided.
- **Idea 3**: Make more realistic, covert attacks.
- **Idea 4**: Implement a real-time defense strategy using our model.
- **Idea 5**: Repeat the experiment, but try to limit the CPU usage during attacks to 1.5x the normal value(s).

# Citations

If you liked this repository, please consider checking these ones out, for they made this project possible:
- [microservices-demo](https://github.com/GoogleCloudPlatform/microservices-demo/tree/main)
- [usad](https://github.com/manigalati/usad)
    ```
    Audibert, J., Michiardi, P., Guyard, F., Marti, S., Zuluaga, M. A. (2020).
    USAD : UnSupervised Anomaly Detection on multivariate time series.
    Proceedings of the 26th ACM SIGKDD International Conference on Knowledge Discovery & Data Mining, August 23-27, 2020
    ```

