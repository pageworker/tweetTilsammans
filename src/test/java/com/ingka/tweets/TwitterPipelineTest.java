package com.ingka.tweets;

import com.google.common.collect.ImmutableList;
import com.ingka.tweets.model.BeamTweet;
import com.ingka.tweets.pipeline.TwitterPipeline;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class TwitterPipelineTest {

    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();

    @Before
    public void setUp() {


    }

    /**
     * A very silly test, to test the logging DoFN
     */
    @Test
    public void aVerySillyTest() {

        Pipeline p = Pipeline.create();


        BeamTweet tweet = BeamTweet.builder().id("1").text("hello world").createdAt(new Date()).language("en").build();
        ImmutableList<BeamTweet> input = ImmutableList.of(tweet);

        PCollection<BeamTweet> out = testPipeline.apply("Create input", Create.of(input))
                .apply("Parse pipeline",
                        ParDo.of(new TwitterPipeline.OutputLines()));

        assertNotNull(out);
        PAssert.that(out).containsInAnyOrder(input);

        testPipeline.run().waitUntilFinish();

    }

}
