package io.quarkiverse.asyncapi.config;

import com.asyncapi.v3._0_0.model.AsyncAPI;

public interface AsyncAPISupplier {
    String id();

    AsyncAPI asyncAPI();
}
