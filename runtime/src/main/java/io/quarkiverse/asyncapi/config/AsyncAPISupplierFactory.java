package io.quarkiverse.asyncapi.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.smallrye.config.ConfigSourceContext;

public class AsyncAPISupplierFactory {

    private final static Set<String> EXTENSIONS = Set.of(".yml", ".yaml", ".json");
    private static Collection<AsyncAPISupplier> asyncAPISuppliers = new ArrayList<>();

    public static Collection<AsyncAPISupplier> init(ConfigSourceContext context) throws IOException {
        asyncAPISuppliers.clear();
        List<String> specDirs = AsyncAPIUtils.getValues(context, AsyncAPIConfigGroup.SOURCES_PROP,
                Arrays.asList("src/main/asyncapi", "src/test/asyncapi"));
        final Collection<String> ignoredFiles = excludedFiles(context);
        for (String dir : specDirs) {
            Path specDir = Path.of(dir);
            if (Files.isDirectory(specDir)) {
                try (Stream<Path> specFilePaths = Files.walk(specDir)) {
                    Collection<Path> files = specFilePaths
                            .filter(path -> isCandidateFile(path, ignoredFiles))
                            .collect(Collectors.toList());
                    for (Path file : files) {
                        asyncAPISuppliers.add(new JacksonAsyncAPISupplier(
                                file.getFileName().toString(), Files.readString(file)));
                    }
                }
            }
        }
        ServiceLoader<AsyncAPISpecInputProvider> providers = ServiceLoader.load(AsyncAPISpecInputProvider.class);
        for (AsyncAPISpecInputProvider provider : providers) {
            AsyncAPISpecInput specInput = provider.read(context);
            for (Map.Entry<String, InputStreamSupplier> entry : specInput.getStreams().entrySet()) {
                try (InputStream stream = entry.getValue().get()) {
                    asyncAPISuppliers.add(new JacksonAsyncAPISupplier(entry.getKey(),
                            new String(stream.readAllBytes())));
                }
            }
        }
        return asyncAPISuppliers;
    }

    public static Collection<AsyncAPISupplier> getAsyncApiSuppliers() {
        return asyncAPISuppliers;
    }

    private static Collection<String> excludedFiles(ConfigSourceContext context) {
        return AsyncAPIUtils.getValues(context, AsyncAPIConfigGroup.EXCLUDED_FILES_PROP, Collections.emptyList());
    }

    private static boolean isCandidateFile(Path path, Collection<String> ignoredFiles) {
        String fileName = path.getFileName().toString();
        return Files.isRegularFile(path) && !ignoredFiles.contains(fileName) && isExtension(fileName);
    }

    private static boolean isExtension(String fileName) {
        return EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    private AsyncAPISupplierFactory() {
    }
}
