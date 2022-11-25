package io.quarkiverse.asyncapi.config;

import com.asyncapi.v2.model.AsyncAPI;

public interface AsyncAPIRecorder {
    void addAsyncAPI(String id, AsyncAPI asyncAPI);
}
