package io.quarkiverse.asyncapi.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import io.quarkiverse.asyncapi.config.AsyncConfigSource;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;

public class AsyncAPIResourceGenerator {

    @BuildStep
    void serviceLoader(CombinedIndexBuildItem index, BuildProducer<GeneratedResourceBuildItem> resourceProducer)
            throws IOException {
        IndexView indexView = index.getIndex();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (OutputStreamWriter w = new OutputStreamWriter(os)) {
                for (ClassInfo configSource : indexView.getAllKnownSubclasses(AsyncConfigSource.class)) {
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
