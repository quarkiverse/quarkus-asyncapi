package io.quarkiverse.asyncapi.annotation.scanner.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConvertWith;
import io.quarkus.runtime.configuration.TrimmedStringConverter;

@ConfigGroup
public class Server {

    /**
     * Url
     */
    @ConfigItem
    @ConvertWith(TrimmedStringConverter.class)
    public String url;

    /**
     * Protocol
     */
    @ConfigItem(defaultValue = "kafka")
    @ConvertWith(TrimmedStringConverter.class)
    public String protocol;
}
