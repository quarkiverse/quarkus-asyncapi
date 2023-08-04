package io.quarkiverse.asyncapi.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class AsyncAPIRegistryProducer {

    @Inject
    Instance<AsyncAPISupplier> asyncAPISuppliers;

    @Produces
    AsyncAPIRegistry getAPIRegistry() {
        return new MapAsyncAPIRegistry(asyncAPISuppliers);
    }
}
