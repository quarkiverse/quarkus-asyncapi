package io.quarkiverse.asyncapi.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class AsyncAPIRegistryProducer {

    @Produces
    @ApplicationScoped
    AsyncAPIRegistry getAPIRegistry() {
        return new MapAsyncAPIRegistry(AsyncAPISupplierFactory.getAsyncApiSuppliers());
    }
}
