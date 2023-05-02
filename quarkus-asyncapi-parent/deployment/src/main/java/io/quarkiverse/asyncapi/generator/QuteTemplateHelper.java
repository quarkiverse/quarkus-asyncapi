package io.quarkiverse.asyncapi.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import org.eclipse.microprofile.config.Config;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;

public class QuteTemplateHelper {

    private static Engine engine = Engine.builder().addDefaults().build();

    private QuteTemplateHelper() {
    }

    private static final String DEFAULT_TEMPLATE_DIR = "templates/";

    private static String getTemplateCP(Config config, String templateName) {
        return DEFAULT_TEMPLATE_DIR + templateName + ".qute";
    }

    public static Template getTemplate(Config config, String templateName) throws IOException {
        Template template = engine.getTemplate(templateName);

        if (template == null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(
                            getTemplateCP(config, templateName)), templateName + " not found in classpath")))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                template = engine.parse(sb.toString());
                engine.putTemplate(templateName, template);
            }
        }
        return template;
    }
}
