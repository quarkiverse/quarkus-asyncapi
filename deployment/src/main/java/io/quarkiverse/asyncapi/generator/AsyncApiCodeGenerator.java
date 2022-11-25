package io.quarkiverse.asyncapi.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;

import com.asyncapi.v2.model.AsyncAPI;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncApiCodeGenerator {

    private final Path outPath;
    private final Config config;
    private final String defaultPackage;
    private boolean generated;

    private final static String PROPS_TEMPLATE = "properties";
    private final static String DEFAULT_PACKAGE = "io.quarkiverse.asyncapi";

    private final static String CONFIG_SUFFIX = "ConfigSource";
    private final static String JAVA_SUFFIX = ".java";

    public AsyncApiCodeGenerator(Path outPath, Config config) {
        this.outPath = outPath;
        this.config = config;
        this.defaultPackage = config.getOptionalValue(AsyncApiConfigGroup.PACKAGE_PROP, String.class).orElse(DEFAULT_PACKAGE);
    }

    public void generate(Path path, ObjectMapper objectMapper) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            generate(path.getFileName().toString(), is, objectMapper, Optional.empty());
        }
    }

    public void generate(String id, InputStream stream, ObjectMapper objectMapper, Optional<String> basePackage)
            throws IOException {
        String packageName = basePackage.orElse(defaultPackage);
        AsyncAPI asyncAPI = objectMapper.readValue(stream, AsyncAPI.class);
        generateConfigFile(id, asyncAPI, packageName);

    }

    private void generateConfigFile(String id, AsyncAPI asyncAPI, String packageName) throws IOException {
        packageName += ".config";
        String simpleClassName = id + CONFIG_SUFFIX;
        Path configFile = outPath.resolve(Path.of(simpleClassName + JAVA_SUFFIX));
        Files.writeString(configFile, QuteTemplateHelper.getTemplate(config, PROPS_TEMPLATE)
                .data(Map.of("id", id, "packageName", packageName, "className", simpleClassName, "asyncAPI",
                        escape(ObjectMapperFactory.get(Extension.json).writeValueAsString(asyncAPI))))
                .render());
        generated = true;
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

    public boolean done() {
        return generated;
    }
}
