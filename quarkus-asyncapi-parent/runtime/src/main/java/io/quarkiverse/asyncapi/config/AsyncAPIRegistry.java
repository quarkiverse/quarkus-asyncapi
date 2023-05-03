package io.quarkiverse.asyncapi.config;

import java.util.Optional;

import com.asyncapi.v2._6_0.model.AsyncAPI;

/** Holder of Async API instances */
public interface AsyncAPIRegistry {

    /**
     * Retrieve an AsyncAPI object given the id provided for that file
     *
     * @param id
     * @return
     */
    Optional<AsyncAPI> getAsyncAPI(String id);
}
