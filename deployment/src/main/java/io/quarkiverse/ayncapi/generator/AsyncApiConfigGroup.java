package io.quarkiverse.ayncapi.generator;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = AsyncApiConfigGroup.CONFIG_PREFIX, phase = ConfigPhase.BUILD_TIME)
public class AsyncApiConfigGroup {

    static final String CONFIG_PREFIX = "ayncapi-generator.codegen";

    public static final String EXCLUDED_FILES_PROP_FORMAT = "quarkus." + CONFIG_PREFIX + ".exclude";

    /**
     * List of files to be excluded
     */
    @ConfigItem(name = "exclude")
    public Optional<List<String>> excluded;

}
