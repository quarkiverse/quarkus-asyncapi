package io.quarkiverse.asyncapi.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.asyncapi.v2.model.AsyncAPI;
import com.fasterxml.jackson.core.JsonProcessingException;

public final class AsyncAPIUtils {

    public static AsyncAPI fromString(String content) {
        try {
            return ObjectMapperFactory.yaml().readValue(content, AsyncAPI.class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static AsyncAPI fromStream(InputStream content) {
        try {
            return ObjectMapperFactory.yaml().readValue(content, AsyncAPI.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private AsyncAPIUtils() {
    }
}
