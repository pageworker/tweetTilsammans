package com.ingka.tweets.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;

import java.util.Date;
import java.util.Objects;

@Data
@DefaultSchema(JavaFieldSchema.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class BeamTweet {
    public String id;
    public String text;
    public Date createdAt;
    public String language;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeamTweet beamTweet = (BeamTweet) o;
        return Objects.equals(id, beamTweet.id) && Objects.equals(text, beamTweet.text)
               //beam tampers with the time so there is a difference even if the objects is one and the same
//                        && Objects.equals(createdAt.getTime(), beamTweet.createdAt.getTime())
               && Objects.equals(language, beamTweet.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createdAt);
    }
}
