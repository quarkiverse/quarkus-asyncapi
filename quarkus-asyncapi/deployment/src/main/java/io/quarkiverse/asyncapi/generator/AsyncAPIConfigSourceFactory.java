package io.quarkiverse.asyncapi.generator;

import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.quarkiverse.asyncapi.config.AsyncConfigSource;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

@StaticInitSafe
public class AsyncAPIConfigSourceFactory implements ConfigSourceFactory {

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        return AsyncAPISupplierFactory.init(context).getAsyncApiSuppliers().stream().map(AsyncConfigSource::new)
                .collect(Collectors.toList());
    }
}
