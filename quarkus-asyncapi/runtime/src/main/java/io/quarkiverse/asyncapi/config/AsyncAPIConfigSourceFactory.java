package io.quarkiverse.asyncapi.config;

import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

public class AsyncAPIConfigSourceFactory implements ConfigSourceFactory {

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        return AsyncAPISupplierFactory.init(context).getAsyncApiSuppliers().stream().map(AsyncAPIConfigSource::new)
                .collect(Collectors.toList());
    }
}
