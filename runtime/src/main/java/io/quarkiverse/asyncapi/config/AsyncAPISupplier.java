package io.quarkiverse.asyncapi.config;

import com.asyncapi.v2.model.AsyncAPI;

public interface AsyncAPISupplier {
    String id();

    AsyncAPI asyncAPI();
}
