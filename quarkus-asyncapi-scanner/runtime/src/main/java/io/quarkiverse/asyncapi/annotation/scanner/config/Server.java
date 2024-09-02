package io.quarkiverse.asyncapi.annotation.scanner.config;

import java.util.Optional;

import io.quarkus.runtime.configuration.TrimmedStringConverter;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;

@ConfigMapping
public interface Server {

    /**
     * Host
     */
    @WithConverter(TrimmedStringConverter.class)
    public String host();

    /**
     * Pathname
     */
    @WithConverter(TrimmedStringConverter.class)
    public Optional<String> pathname();

    /**
     * Protocol
     */
    @WithDefault("kafka")
    @WithConverter(TrimmedStringConverter.class)
    public String protocol();
}
