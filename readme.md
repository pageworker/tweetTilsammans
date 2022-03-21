####

# Beam DM POC project

Welcome to the beam twitter etl project. 
The objective of this project is to read all tweets about IKEA and 
store them in a BigQuery to be used for analytical purposes 

-----------

# Prerequisite

* JAVA 11
* Python 3.6


# Step 1

##Docker compose 
Before you can run this project localy you need to know that this project uses a few resources like pubsub and big table. 
In order to start, simple run the from the root of this project the following steps.
docker compose file.

Start the pubsub emulator:
```sh
docker compose up -d
```

it also uses the BigTable emulator:
```sh
gcloud components update beta
gcloud beta emulators bigtable start
```

after the emulator has started run the command below to init your environment
before you start the pipeline locally
```shell
$(gcloud beta emulators bigtable env-init)
```



# Step 2

 # Big Qeuery 
## start the emulat or
 gcloud beta emulators bigtable start

 ## Init the env where you run the pipe
 $(gcloud beta emulators bigtable env-init)

 ## install cbt
 gcloud components install cbt 

 ### set config 

echo project = $PROJECT_ID > ~/.cbtrc
echo instance = test-instance >> ~/.cbtrc

### verify content of file
cat ~/.cbtrc

### Create tables
cbt createtable inventory-item
### list tables 
cbt ls


To run and publish events
```
export MAIN_CLASS_NAME=org.ikea.nl.dm.helper.PublisherExample
mvn compile exec:java \
-Dexec.mainClass=${MAIN_CLASS_NAME}  -Dexec.args="-name inventory_alerts"
```


# Create BiqQuery Dataset
bq mk --location=europe-west4 logs



# Step 2

## Run the pipeline 

To run execute after starting local infra defined above

## Env vars
```sh
export PROJECT_ID=twitter-dm
export RUNNER=DirectRunner
export MAIN_CLASS_NAME=com.ingka.tweets.pipeline.TwitterPipeline
export JAVA_HOME=`/usr/libexec/java_home -v 1.8.0_322`
export MAIN_CLASS_NAME=com.ingka.tweets.pipeline.TwitterPipeline
export api_key=
export api_secret=
export access_token=
export access_token_secret=
export twitter_query=IKEA
```

## execute

```sh
 mvn compile -X exec:java \
-Dexec.mainClass=${MAIN_CLASS_NAME} \
-Dexec.cleanupDaemonThreads=false \
-Dexec.args=" --apiKey=${api_key} \
 --apiSecret=${api_secret} \
 --accessToken=${access_token} \
 --accessTokenSecret=${access_token_secret} \
 --twitterQuery=${twitter_query} \
 --outputBigQueryTable=twitter-dm:ikea.tweets \
 --project=twitter-dm --runner=${RUNNER}"
```