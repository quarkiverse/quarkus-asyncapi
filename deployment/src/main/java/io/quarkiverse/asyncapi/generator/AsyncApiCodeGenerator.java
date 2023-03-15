package io.quarkiverse.asyncapi.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;

import com.asyncapi.v2._6_0.model.AsyncAPI;

import io.quarkiverse.asyncapi.config.AsyncAPIUtils;
import io.quarkiverse.asyncapi.config.ObjectMapperFactory;

public class AsyncApiCodeGenerator {

    private final Path outPath;
    private final Config config;
    private final String basePackage;

    private static final String DEFAULT_PACKAGE = "io.quarkiverse.asyncapi";
    static final String SERVICE_LOADER = "org.eclipse.microprofile.config.spi.ConfigSource";

    private static final String CONFIG_SOURCE = "ConfigSource";
    private static final String PRODUCER_NAME = "AsyncAPISupplier";
    private static final String JAVA_SUFFIX = ".java";

    private final Collection<String> configClassNames = new HashSet<>();

    public AsyncApiCodeGenerator(Path outPath, Config config, Optional<String> packageName) {
        this.outPath = outPath;
        this.config = config;
        this.basePackage = packageName
                .orElse(config.getOptionalValue(AsyncApiConfigGroup.PACKAGE_PROP, String.class).orElse(DEFAULT_PACKAGE));
    }

    public void generate(Path path) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            generate(path.getFileName().toString(), is);
        }
    }

    public void generate(String id, InputStream stream)
            throws IOException {
        id = AsyncAPIUtils.getJavaClassName(id);
        AsyncAPI asyncAPI = ObjectMapperFactory.yaml().readValue(stream, AsyncAPI.class);
        Map<String, Object> data = Map.of("id", id, "packageName", basePackage, "asyncAPI",
                escape(ObjectMapperFactory.json().writeValueAsString(asyncAPI)));
        configClassNames.add(writeTemplate(id, CONFIG_SOURCE, data));
        writeTemplate(id, PRODUCER_NAME, data);
    }

    private String escape(String raw) {
        String escaped = raw;
        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\"", "\\\"");
        escaped = escaped.replace("\b", "\\b");
        escaped = escaped.replace("\f", "\\f");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\r", "\\r");
        escaped = escaped.replace("\t", "\\t");
        return escaped;
    }

    private String writeTemplate(String id, String templateName, Map<String, Object> map) throws IOException {
        Files.writeString(
                Files.createDirectories(outPath.resolve(basePackage.replace('.', '/')).resolve(id))
                        .resolve(templateName + JAVA_SUFFIX),
                QuteTemplateHelper.getTemplate(config, templateName).data(map).render());
        return basePackage + "." + id + "." + templateName;
    }

    public boolean done(boolean test) throws IOException {
        if (!configClassNames.isEmpty()) {
            writeServiceLoader(test);
            return true;
        }
        return false;
    }

    private void writeServiceLoader(boolean test) throws IOException {
        Path serviceLoader = Files
                .createDirectories(outPath.getParent().getParent().resolve(test ? "test-classes" : "classes")
                        .resolve("META-INF").resolve("services"))
                .resolve(SERVICE_LOADER);
        try (BufferedWriter w = Files.newBufferedWriter(serviceLoader)) {
            for (String implName : configClassNames) {
                w.write(implName);
                w.write(System.lineSeparator());
            }
        }
    }

}
