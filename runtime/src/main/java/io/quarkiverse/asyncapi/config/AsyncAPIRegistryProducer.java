package io.quarkiverse.asyncapi.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class AsyncAPIRegistryProducer {

    @Inject
    Instance<AsyncAPISupplier> asyncAPISuppliers;

    @Produces
    AsyncAPIRegistry getAPIRegistry() {
        return new MapAsyncAPIRegistry(asyncAPISuppliers);
    }
}
