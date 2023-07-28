package io.quarkiverse.asyncapi.config;

import java.util.Collection;

import com.asyncapi.v2.model.AsyncAPI;

public interface AsyncAPISupplier {
    Collection<AsyncAPI> asyncApis();

    String name();
}
