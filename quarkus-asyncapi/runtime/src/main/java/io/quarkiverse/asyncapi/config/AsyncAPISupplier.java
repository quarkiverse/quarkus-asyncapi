package io.quarkiverse.asyncapi.config;

import com.asyncapi.v2._6_0.model.AsyncAPI;

public interface AsyncAPISupplier {
    String id();

    AsyncAPI asyncAPI();
}
