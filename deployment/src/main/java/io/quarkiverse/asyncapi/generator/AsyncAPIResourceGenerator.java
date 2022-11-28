package io.quarkiverse.asyncapi.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;

public class AsyncAPIResourceGenerator {

    @BuildStep
    void scanForBeans(BeanArchiveIndexBuildItem beanArchiveIndex, BuildProducer<GeneratedResourceBuildItem> resourceProducer)
            throws IOException {
        IndexView indexView = beanArchiveIndex.getIndex();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (OutputStreamWriter w = new OutputStreamWriter(os)) {
                for (ClassInfo configSource : indexView.getAllKnownSubclasses(ConfigSource.class)) {
                    w.write(configSource.name().toString());
                    w.write(System.lineSeparator());
                }
                w.flush();
                resourceProducer.produce(
                        new GeneratedResourceBuildItem(
                                "META-INF/services/org.eclipse.microprofile.config.spi.ConfigSource",
                                os.toByteArray()));
            }
        }
    }
}
