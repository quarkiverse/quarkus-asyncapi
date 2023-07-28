package io.quarkiverse.asyncapi.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.asyncapi.v2.model.AsyncAPI;

import io.smallrye.config.ConfigSourceContext;

public class AsyncAPISupplierFactory {

    private final static Set<String> EXTENSIONS = Set.of(".yml", ".yaml", ".json");
    private static Collection<AsyncAPISupplier> asyncAPISuppliers = new ArrayList<>();

    public static Collection<AsyncAPISupplier> init(ConfigSourceContext context) {
        asyncAPISuppliers.clear();
        List<String> specDirs = getValues(context, AsyncAPIConfigGroup.SOURCES_PROP,
                Arrays.asList("src/main/asyncapi", "src/test/asyncapi"));
        final Collection<String> ignoredFiles = excludedFiles(context);
        Collection<AsyncAPI> asyncAPIs = new ArrayList<>();
        for (String dir : specDirs) {
            Path specDir = Path.of(dir);
            if (Files.isDirectory(specDir)) {
                try (Stream<Path> specFilePaths = Files.walk(specDir)) {
                    Collection<Path> files = specFilePaths
                            .filter(path -> isCandidateFile(path, ignoredFiles))
                            .collect(Collectors.toList());
                    for (Path file : files) {
                        asyncAPIs.add(AsyncAPIUtils.fromString(Files.readString(file)));
                    }

                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        asyncAPISuppliers.add(new RawAsyncAPISupplier(asyncAPIs));
        ServiceLoader<AsyncAPISpecInputProvider> providers = ServiceLoader.load(AsyncAPISpecInputProvider.class);
        for (AsyncAPISpecInputProvider provider : providers) {
            asyncAPISuppliers.add(provider.read(context));
        }
        return asyncAPISuppliers;
    }

    public static Collection<AsyncAPISupplier> getAsyncApiSuppliers() {
        return asyncAPISuppliers;
    }

    private static Collection<String> excludedFiles(ConfigSourceContext context) {
        return getValues(context, AsyncAPIConfigGroup.EXCLUDED_FILES_PROP, Collections.emptyList());
    }

    private static boolean isCandidateFile(Path path, Collection<String> ignoredFiles) {
        String fileName = path.getFileName().toString();
        return Files.isRegularFile(path) && !ignoredFiles.contains(fileName) && isExtension(fileName);
    }

    private static List<String> getValues(ConfigSourceContext context, String propertyName, List<String> defaultValue) {
        String propValue = context.getValue(propertyName).getValue();
        return propValue == null ? defaultValue : Arrays.asList(propValue.split(",; "));
    }

    private static boolean isExtension(String fileName) {
        return EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    private AsyncAPISupplierFactory() {
    }
}
