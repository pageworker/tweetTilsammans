package com.ingka.tweets.option;

import com.ingka.tweets.pipeline.TwitterPipeline;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation;

/**
 * The {@link TwitterPipeline.Options} class provides the custom execution options passed by the
 * executor at the command-line.
 */
public interface TwitterOptions extends DataflowPipelineOptions {

    @Description("The API key to use with the Twitter API.")
    @Validation.Required
    String getApiKey();

    void setApiKey(String value);

    @Description("The Access token to use with the Twitter API.")
    @Validation.Required
    String getAccessToken();

    void setAccessToken(String value);

    @Description("The API Secret to use with the Twitter API.")
    @Validation.Required
    String getApiSecret();

    void setApiSecret(String value);

    @Description("The Access Token Secret to use with the Twitter API.")
    @Validation.Required
    String getAccessTokenSecret();

    void setAccessTokenSecret(String value);

    @Description("The handle of the Twitter user to pull Tweets for.")
    @Validation.Required
    String getTwitterQuery();

    void setTwitterQuery(String value);

    @Description("JDBC Hostanme url")
    @Default.String("jdbc:postgresql://localhost:5432/atp")
    String getJdbcHostNameURL();

    void setJdbcHostNameURL(String jdbcHostNameURL);

    @Description("JDBC username")
    @Default.String("atp")
    String getJdbcUsername();

    void setJdbcUsername(String jdbcUsername);

    @Description("JDBC password")
    @Default.String("atp")
    String getJdbcPassword();

    void setJdbcPassword(String jdbcPassword);
}
