package io.quarkiverse.asyncapi.config;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = AsyncApiConfigGroup.CONFIG_PREFIX, phase = ConfigPhase.BUILD_TIME)
public class AsyncApiConfigGroup {

    static final String CONFIG_PREFIX = "asyncapi-generator.codegen";

    private static final String SOURCES = "sources";
    private static final String EXCLUDE = "exclude";
    private static final String PACKAGE = "package";

    public static final String EXCLUDED_FILES_PROP = getPropName(EXCLUDE);
    public static final String PACKAGE_PROP = getPropName(PACKAGE);
    public static final String SOURCES_PROP = getPropName(SOURCES);

    private static final String getPropName(String suffix) {
        return "quarkus." + CONFIG_PREFIX + "." + suffix;
    }

    /**
     * List of files to be excluded
     */
    @ConfigItem(name = EXCLUDE)
    public Optional<List<String>> excluded;

    /**
     * Package name for generated classes
     */
    @ConfigItem(name = PACKAGE)
    public Optional<String> basePackage;

    /**
     * Source directories
     */
    @ConfigItem(name = SOURCES)
    public Optional<List<String>> sourceDirs;
}
