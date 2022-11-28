package io.quarkiverse.asyncapi.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.asyncapi.v2.model.AsyncAPI;

import io.quarkus.runtime.Startup;

@Startup
public class AsyncAPIRegistryProducer {

    @Inject
    Instance<AsyncAPILoader> asyncAPISuppliers;

    private Map<String, AsyncAPI> asyncAPIs;

    @PostConstruct
    void init() {
        asyncAPIs = new HashMap<>();
        for (AsyncAPILoader asyncAPISupplier : asyncAPISuppliers) {
            asyncAPISupplier.load(asyncAPIs);
        }
    }

    @Produces
    AsyncAPIRegistry getAPIRegistry() {
        return new MapAsyncAPIRegistry(asyncAPIs);
    }
}
