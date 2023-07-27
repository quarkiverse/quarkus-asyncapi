package io.quarkiverse.asyncapi.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class AsyncAPIRegistryProducer {

    @Produces
    AsyncAPIRegistry getAPIRegistry() {
        return new MapAsyncAPIRegistry(AsyncAPISupplierFactory.get().getAsyncApiSuppliers());
    }
}
