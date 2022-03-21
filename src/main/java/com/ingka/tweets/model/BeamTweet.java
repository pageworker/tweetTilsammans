package com.ingka.tweets.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
@DefaultSchema(JavaFieldSchema.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeamTweet implements Serializable {
    public String id;
    public String text;
    public Date createdAt;
    public String language;
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeamTweet beamTweet = (BeamTweet) o;
        return Objects.equals(id, beamTweet.id) && Objects.equals(text, beamTweet.text) && Objects.equals(createdAt, beamTweet.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createdAt);
    }
}
