package io.quarkiverse.asyncapi.meta;

import com.asyncapi.v2.model.AsyncAPI;

interface AsyncAPIRecorder {
    void addAsyncAPI(String id, AsyncAPI asyncAPI);
}
