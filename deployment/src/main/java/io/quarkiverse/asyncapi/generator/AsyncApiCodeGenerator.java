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

import io.quarkiverse.asyncapi.config.AsyncAPIExtension;
import io.quarkiverse.asyncapi.config.ObjectMapperFactory;
import io.smallrye.config.common.utils.StringUtil;

public class AsyncApiCodeGenerator {

    private final Path outPath;
    private final Config config;
    private final String basePackage;
    private boolean generated;

    private final static String DEFAULT_PACKAGE = "io.quarkiverse.asyncapi";

    private final static String CONFIG_SOURCE = "ConfigSource";
    private final static String PRODUCER_NAME = "AsyncAPISupplier";
    private final static String JAVA_SUFFIX = ".java";

    public AsyncApiCodeGenerator(Path outPath, Config config, Optional<String> packageName) {
        this.outPath = outPath;
        this.config = config;
        this.basePackage = packageName
                .orElse(config.getOptionalValue(AsyncApiConfigGroup.PACKAGE_PROP, String.class).orElse(DEFAULT_PACKAGE));
    }

    public void generate(Path path, ObjectMapper objectMapper) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            generate(getJavaClassName(path.getFileName().toString()), is, objectMapper);
        }
    }

    public void generate(String id, InputStream stream, ObjectMapper objectMapper)
            throws IOException {
        AsyncAPI asyncAPI = objectMapper.readValue(stream, AsyncAPI.class);
        Map<String, Object> data = Map.of("id", id, "packageName", basePackage, "asyncAPI",
                escape(ObjectMapperFactory.get(AsyncAPIExtension.json).writeValueAsString(asyncAPI)));
        writeTemplate(id + CONFIG_SOURCE, CONFIG_SOURCE, data);
        writeTemplate(id + PRODUCER_NAME, PRODUCER_NAME, data);
        generated = true;
    }

    private String getJavaClassName(String name) {
        return capitalizeFirst(StringUtil.replaceNonAlphanumericByUnderscores(name));
    }

    private String capitalizeFirst(String name) {
        char ch = name.charAt(0);
        return Character.isUpperCase(ch) ? name : Character.toUpperCase(ch) + name.substring(1);
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

    private void writeTemplate(String className, String templateName, Map<String, Object> map) throws IOException {
        Files.writeString(outPath.resolve(Path.of(className + JAVA_SUFFIX)),
                QuteTemplateHelper.getTemplate(config, templateName).data(map).render());
    }

    public boolean done() throws IOException {
        return generated;
    }
}
