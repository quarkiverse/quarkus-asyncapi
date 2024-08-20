package io.quarkiverse.asyncapi.generator;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = AsyncApiConfigGroup.CONFIG_PREFIX)
public class AsyncApiConfigGroup {

    static final String CONFIG_PREFIX = "asyncapi-generator.codegen";

    private static final String EXCLUDE = "exclude";
    private static final String PACKAGE = "package";

    public static final String EXCLUDED_FILES_PROP = getPropName(EXCLUDE);
    public static final String PACKAGE_PROP = getPropName(PACKAGE);

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
}
