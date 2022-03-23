package com.ingka.tweets.pipeline;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import com.ingka.tweets.config.PostgresConstants;
import com.ingka.tweets.connector.TwitterReader;
import com.ingka.tweets.model.BeamTweet;
import com.ingka.tweets.option.TwitterOptions;
import com.ingka.tweets.sink.PostgresTweetSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import java.util.Arrays;

@Slf4j
public class TwitterPipeline {

    /**
     * The logger to output status messages to.
     */


    /**
     * The main entry-point for pipeline execution. This method will start the
     * pipeline but will not wait for it's execution to finish. If blocking
     * execution is required, use the {@link TwitterPipeline#run(Options)} method to
     * start the pipeline and invoke {@code result.waitUntilFinish()} on the
     * {@link PipelineResult}.
     *
     * @param args The command-line args passed by the executor.
     */
    public static void main(String[] args) throws ClassNotFoundException {


            Class.forName("org.postgresql.Driver");
            //on classpath

        TwitterOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(TwitterOptions.class);
        run(options);
    }

    public static ValueProvider<String> vp(String value) {
        return new ValueProvider<String>() {
            @Override
            public String get() {
                return value;
            }

            @Override
            public boolean isAccessible() {
                return false;
            }
        };
    }


    /**
     * Runs the pipeline to completion with the specified options. This method does
     * not wait until the pipeline is finished before returning. Invoke
     * {@code result.waitUntilFinish()} on the result object to block until the
     * pipeline is finished running if blocking programmatic execution is required.
     *
     * @param options The execution options.
     * @return The pipeline result.
     */
    public static PipelineResult run(TwitterOptions options) {


        // Schema for the output BigQuery table.
        final TableSchema outputSchema = new TableSchema().setFields(Arrays.asList(
                new TableFieldSchema().setName("id").setType("STRING"),
                new TableFieldSchema().setName("text").setType("STRING"),
                new TableFieldSchema().setName("createdAt").setType("DATETIME"),
                new TableFieldSchema().setName("language").setType("STRING")));


        // -- Create the pipeline
        Pipeline pipeline = Pipeline.create(options);

        // -- force pubsub emulator
        options.setPubsubRootUrl("http://localhost:8085");
        options.setJobName("twitter-dm-" + System.currentTimeMillis());

        PostgresTweetSink postgresTweetSink = new PostgresTweetSink(getDataSourceConfiguration(options));
        pipeline.apply("ReadTweets", TwitterReader.read(vp(options.getApiKey()),
                        vp(options.getApiSecret()),
                        vp(options.getAccessToken()),
                        vp(options.getAccessTokenSecret()),
                        vp(options.getTwitterQuery())))
                //write to output log for debugging purpose
                .apply("LoggingAll", ParDo.of(new OutputLines()))
                //sink the tweets
                .apply("SaveToSQL", postgresTweetSink.getUpsertSink());

        log.info("Building pipeline...");

        return pipeline.run();
    }

    /**
     * Get data source configuration
     *
     * @param options the options
     * @return data source configuration
     */
    public static JdbcIO.DataSourceConfiguration getDataSourceConfiguration(TwitterOptions options) {

        final JdbcIO.DataSourceConfiguration dataSourceConfiguration = JdbcIO.DataSourceConfiguration
                .create(PostgresConstants.ORG_POSTGRESQL_DRIVER, options.getJdbcHostNameURL());
        final JdbcIO.DataSourceConfiguration dataSource = dataSourceConfiguration
                .withUsername(options.getJdbcUsername()).withPassword(options.getJdbcPassword());

        return dataSource;
    }

    static class OutputLines extends DoFn<BeamTweet, BeamTweet> {

        @ProcessElement
        public void processElement(@Element BeamTweet message, OutputReceiver<BeamTweet> receiver) {
            log.info("-------------    id {} \t\t, message {}, \t\t createdAt {} , \t\t sold {}   -----------------", message.getId(), message.getText(), message.getCreatedAt());
            receiver.output(message);
        }
    }

}