package io.quarkiverse.asyncapi.config;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.asyncapi.v2.model.AsyncAPI;

public class JacksonAsyncAPISupplier implements AsyncAPISupplier {

    private final String id;
    private final String content;

    public JacksonAsyncAPISupplier(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public AsyncAPI asyncAPI() {
        try {
            return ObjectMapperFactory.yaml().readValue(content, AsyncAPI.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
