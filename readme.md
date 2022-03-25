####

# Beam DM POC project

Welcome to the beam twitter etl project. 
The objective of this project is to read all tweets about IKEA and 
store them in a BigQuery to be used for analytical purposes 

-----------

# Prerequisite

* JAVA 11

# Step 1

##Docker compose 
Before you can run this project locally you need to know that this project uses a few resources like pubsub and big table. 
In order to start, simple run the docker compose found at the root of this project the following steps.

Starting dev resources
```sh
docker compose up -d
```

after booting the postgres database use the schema.sql to create the table
using your favorite sql editor.
    



## Run the pipeline 

To run execute after starting local infra defined above

set the environment variables

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

The the last step : execute the pipeline

```sh
 mvn compile -X exec:java \
-Dexec.mainClass=${MAIN_CLASS_NAME} \
-Dexec.cleanupDaemonThreads=false \
-Dexec.args=" --apiKey=${api_key} \
 --apiSecret=${api_secret} \
 --accessToken=${access_token} \
 --accessTokenSecret=${access_token_secret} \
 --twitterQuery=${twitter_query} \
 --jdbcHostNameURL=jdbc:postgresql://localhost:5432/atp \
 --jdbcUsername=atp \
 --jdbcPassword=atp \
 --project=twitter-dm --runner=${RUNNER}"
```