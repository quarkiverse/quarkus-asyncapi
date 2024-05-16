package io.quarkiverse.asyncapi.annotation.scanner.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConvertWith;
import io.quarkus.runtime.configuration.TrimmedStringConverter;

@ConfigGroup
public class Server {

    /**
     * Host
     */
    @ConfigItem
    @ConvertWith(TrimmedStringConverter.class)
    public String host;

    /**
     * Pathname
     */
    @ConfigItem
    @ConvertWith(TrimmedStringConverter.class)
    public Optional<String> pathname;

    /**
     * Protocol
     */
    @ConfigItem(defaultValue = "kafka")
    @ConvertWith(TrimmedStringConverter.class)
    public String protocol;
}
