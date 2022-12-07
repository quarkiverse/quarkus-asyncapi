package io.quarkiverse.asyncapi.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;

import com.asyncapi.v2.model.AsyncAPI;

import io.quarkiverse.asyncapi.config.AsyncAPIUtils;
import io.quarkiverse.asyncapi.config.ObjectMapperFactory;

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

    public void generate(Path path) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            generate(path.getFileName().toString(), is);
        }
    }

    public void generate(String id, InputStream stream)
            throws IOException {
        id = AsyncAPIUtils.getJavaClassName(id);
        AsyncAPI asyncAPI = ObjectMapperFactory.reader().readValue(stream, AsyncAPI.class);
        Map<String, Object> data = Map.of("id", id, "packageName", basePackage, "asyncAPI",
                escape(ObjectMapperFactory.writer().writeValueAsString(asyncAPI)));
        writeTemplate(id + CONFIG_SOURCE, CONFIG_SOURCE, data);
        writeTemplate(id + PRODUCER_NAME, PRODUCER_NAME, data);
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

    private void writeTemplate(String className, String templateName, Map<String, Object> map) throws IOException {
        Files.writeString(
                Files.createDirectories(outPath.resolve(basePackage.replace('.', '/'))).resolve(className + JAVA_SUFFIX),
                QuteTemplateHelper.getTemplate(config, templateName).data(map).render());
    }

    public boolean done() throws IOException {
        return generated;
    }
}
