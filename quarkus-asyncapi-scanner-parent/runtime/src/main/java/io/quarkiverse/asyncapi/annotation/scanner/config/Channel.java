package io.quarkiverse.asyncapi.annotation.scanner.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConvertWith;
import io.quarkus.runtime.configuration.TrimmedStringConverter;

@ConfigGroup
public class Channel {

    /**
     * Description
     */
    @ConfigItem
    @ConvertWith(TrimmedStringConverter.class)
    public Optional<String> description;
}
