package io.quarkiverse.asyncapi.config;

import io.quarkus.runtime.configuration.ConfigBuilder;
import io.smallrye.config.SmallRyeConfigBuilder;

public class AsyncAPIConfigBuilder implements ConfigBuilder {

    @Override
    public SmallRyeConfigBuilder configBuilder(SmallRyeConfigBuilder builder) {
        AsyncAPISupplierFactory.init(builder).getAsyncApiSuppliers()
                .forEach(a -> builder.getSources().add(new AsyncConfigSource(a)));
        return builder;
    }
}
