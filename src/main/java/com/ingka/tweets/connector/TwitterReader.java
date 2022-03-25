package com.ingka.tweets.connector;


import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.ingka.tweets.model.BeamTweet;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.time.ZoneOffset.UTC;

/**
 * TwitterReader is an I/O connector that fetches the last 1 days worth of Tweets for a given Twitter Query
 */
public class TwitterReader {


    public static ListTweets read(ValueProvider<String> apiKey,
                                  ValueProvider<String> apiSecretKey,
                                  ValueProvider<String> accessToken,
                                  ValueProvider<String> accessTokenSecret,
                                  ValueProvider<String> twitterUser) {
        //create and return the custom connector
        return new ListTweets(
                twitterUser,
                apiKey,
                apiSecretKey,
                accessToken,
                accessTokenSecret
        );

    }

    public static class ListTweets extends PTransform<PBegin, PCollection<BeamTweet>> {

        private final ValueProvider<String> twitterHandle;
        private final ValueProvider<String> apiKey;
        private final ValueProvider<String> apiSecretKey;
        private final ValueProvider<String> accessToken;
        private final ValueProvider<String> accessTokenSecret;

        ListTweets(ValueProvider<String> twitterHandle,
                   ValueProvider<String> apiKey,
                   ValueProvider<String> apiSecretKey,
                   ValueProvider<String> accessToken,
                   ValueProvider<String> accessTokenSecret
        ) {
            this.twitterHandle = twitterHandle;
            this.apiKey = apiKey;
            this.apiSecretKey = apiSecretKey;
            this.accessToken = accessToken;
            this.accessTokenSecret = accessTokenSecret;
        }

        public PCollection<BeamTweet> expand(PBegin input) {
            return input
                    .apply(Create.ofProvider(this.twitterHandle, StringUtf8Coder.of()))
                    .apply(ParDo.of(new ListTweetsFn(this.apiKey, this.apiSecretKey, this.accessToken, this.accessTokenSecret)));
        }
    }

    @Slf4j
    static class ListTweetsFn extends DoFn<String, BeamTweet> {

        private final ValueProvider<String> apiKey;
        private final ValueProvider<String> apiSecretKey;
        private final ValueProvider<String> accessToken;
        private final ValueProvider<String> accessTokenSecret;
        private transient TwitterClient client;

        ListTweetsFn(
                ValueProvider<String> apiKey,
                ValueProvider<String> apiSecretKey,
                ValueProvider<String> accessToken,
                ValueProvider<String> accessTokenSecret
        ) {
            this.apiKey = apiKey;
            this.apiSecretKey = apiSecretKey;
            this.accessToken = accessToken;
            this.accessTokenSecret = accessTokenSecret;
        }


        /**
         * Initialising the twitter client
         */
        @Setup
        public void initClient() {

            TwitterCredentials creds = TwitterCredentials.builder()
                    .apiKey(this.apiKey.get())
                    .apiSecretKey(this.apiSecretKey.get())
                    .accessToken(this.accessToken.get())
                    .accessTokenSecret(this.accessTokenSecret.get())
                    .build();

            this.client = new TwitterClient(creds);
        }

        /**
         * Fetches Tweets for the last day for the given twitterQuery
         *
         * @param twitterQuery   The handle of the user you wish to download Tweets for.
         * @param outputReceiver The output receiver that we emit our results to.
         */
        @ProcessElement
        public void listTweets(
                @Element String twitterQuery,
                OutputReceiver<BeamTweet> outputReceiver) {

            //create the query and select the last 24h.
            LocalDateTime dateFrom = LocalDateTime.now(UTC).plusDays(-1L);
            LocalDateTime dateTo = LocalDateTime.now(UTC).plusSeconds(-60L);
            List<Tweet> tweets = this.client.searchForTweetsWithin7days(twitterQuery, dateFrom, dateTo);

            for (Tweet t : tweets) {
                log.debug(String.format("Received tweet: %s", t.getText()));
                outputReceiver.output(
                        BeamTweet.builder()
                                .id(t.getId())
                                .text(new String(t.getText().getBytes(), StandardCharsets.UTF_8))
                                .createdAt(Date.from(t.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()))
                                .language(t.getLang())
                                .build());
            }
        }

    }
}
