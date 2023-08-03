package io.quarkiverse.asyncapi.config;

import java.util.Collections;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;

public class AsyncAPIConfigSourceFactory implements ConfigSourceFactory {

    private static final Logger logger = Logger.getLogger(AsyncAPIConfigSourceFactory.class);

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        try {
            return AsyncAPISupplierFactory.init(context).stream().map(AsyncAPIConfigSource::new)
                    .collect(Collectors.toList());
        } catch (Exception io) {
            logger.info(
                    "Problem initializing async api configuration. Unless there are follow up errors related with async api, you can quietly ignore exception {}",
                    io);
            return Collections.emptyList();
        }
    }
}
