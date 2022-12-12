package io.quarkiverse.asyncapi.config;

import java.util.List;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

public abstract class AsyncConfigSourceFactory implements ConfigSourceFactory {

    private final Iterable<ConfigSource> sources;

    protected AsyncConfigSourceFactory(AsyncConfigSource source) {
        this.sources = List.of(source);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        return sources;
    }
}
