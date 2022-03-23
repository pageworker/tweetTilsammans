package com.ingka.tweets.sink;

import com.ingka.tweets.config.PostgresConstants;
import com.ingka.tweets.model.BeamTweet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.io.jdbc.JdbcIO.DataSourceConfiguration;
import org.apache.beam.sdk.io.jdbc.JdbcIO.RetryConfiguration;
import org.apache.beam.sdk.io.jdbc.JdbcIO.Write;
import org.joda.time.Duration;

import java.sql.PreparedStatement;


@RequiredArgsConstructor
public class PostgresTweetSink {

    private final static String SQL_STATEMENT = "INSERT INTO ikea_tweets (id, \"text\", created_at, \"language\")    VALUES(?, ?, ?, ?);";
    private final DataSourceConfiguration dataSource;


    @SneakyThrows
    private static void setPreparedStatement(final BeamTweet beamTweet, final PreparedStatement preparedStatement) {
        preparedStatement.setString(1, beamTweet.getId());
        preparedStatement.setString(2, beamTweet.getText());
        preparedStatement.setTimestamp(3, new java.sql.Timestamp(beamTweet.getCreatedAt().getTime()));
        preparedStatement.setString(4, beamTweet.getLanguage());
    }

    public Write<BeamTweet> getUpsertSink() {
        return JdbcIO.<BeamTweet>write().withDataSourceConfiguration(dataSource)
                .withStatement(SQL_STATEMENT)
                .withBatchSize(PostgresConstants.BATCH_SIZE_DEFAULT).withBatchSize(PostgresConstants.BATCH_SIZE_DEFAULT)
                .withRetryConfiguration(
                        RetryConfiguration.create(20, Duration.standardSeconds(60), Duration.standardSeconds(10)))
                .withPreparedStatementSetter(PostgresTweetSink::setPreparedStatement);
    }

}
