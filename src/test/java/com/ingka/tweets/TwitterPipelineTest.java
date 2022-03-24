package com.ingka.tweets;

import org.apache.beam.sdk.testing.TestPipeline;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class TwitterPipelineTest {

    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();


    /**
     * Simple test to test the parsing of regiondetails

     */
    @Test
    public void testParseJsonDoFn()  {
        //todo
        testPipeline.run().waitUntilFinish();

    }

}
