package io.quarkiverse.asyncapi.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.asyncapi.v2.model.AsyncAPI;

@ApplicationScoped
public class AsyncAPIRegistryProducer {

    @Inject
    Instance<AsyncAPISupplier> asyncAPISuppliers;

    private Map<String, AsyncAPI> asyncAPIs;

    @PostConstruct
    void init() {
        asyncAPIs = new HashMap<>();
        for (AsyncAPISupplier asyncAPISupplier : asyncAPISuppliers) {
            asyncAPIs.put(asyncAPISupplier.id(), asyncAPISupplier.asyncAPI());
        }
    }

    @Produces
    AsyncAPIRegistry getAPIRegistry() {
        return new MapAsyncAPIRegistry(asyncAPIs);
    }
}
