package io.quarkiverse.asyncapi.generator;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = AsyncApiConfig.CONFIG_PREFIX)
public interface AsyncApiConfig {

    static final String CONFIG_PREFIX = "quarkus.asyncapi-generator.codegen";

    static final String EXCLUDE = "exclude";
    static final String PACKAGE = "package";

    public static final String EXCLUDED_FILES_PROP = CONFIG_PREFIX + "." + EXCLUDE;
    public static final String PACKAGE_PROP = CONFIG_PREFIX + "." + PACKAGE;

    /**
     * List of files to be excluded
     */
    @WithName(EXCLUDE)
    Optional<List<String>> excluded();

    /**
     * Package name for generated classes
     */
    @WithName(PACKAGE)
    Optional<String> basePackage();
}
