package io.quarkiverse.asyncapi.meta;

import com.asyncapi.v2.model.AsyncAPI;

/** Holder of Async API instances */
public interface AsyncAPIRegistry {

    /**
     * Retrieve an AsyncAPI object given the id provided for that file
     *
     * @param id
     * @return
     */
    AsyncAPI getAsyncAPI(String id);
}
