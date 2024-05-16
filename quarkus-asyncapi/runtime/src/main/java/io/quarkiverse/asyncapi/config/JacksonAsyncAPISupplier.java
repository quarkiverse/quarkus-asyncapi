package io.quarkiverse.asyncapi.config;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.asyncapi.v3._0_0.model.AsyncAPI;

public abstract class JacksonAsyncAPISupplier implements AsyncAPISupplier {

    private final String id;
    private final String content;

    protected JacksonAsyncAPISupplier(String id, String content) {
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
            return ObjectMapperFactory.json().readValue(content, AsyncAPI.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
