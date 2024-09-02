package io.quarkiverse.asyncapi.annotation.scanner.config;

import java.util.Optional;

import io.quarkus.runtime.configuration.TrimmedStringConverter;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;

@ConfigMapping
public interface Channel {

    /**
     * Description
     */
    @WithConverter(TrimmedStringConverter.class)
    public Optional<String> description();
}
