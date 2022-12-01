package io.quarkiverse.asyncapi.generator;

import java.io.IOException;
import java.util.OptionalInt;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import io.quarkiverse.asyncapi.config.AsyncConfigSource;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationSourceBuildItem;

public class AsyncAPIResourceGenerator {

    @BuildStep
    void serviceLoader(CombinedIndexBuildItem index, BuildProducer<RunTimeConfigurationSourceBuildItem> resourceProducer)
            throws IOException {
        IndexView indexView = index.getIndex();
        for (ClassInfo configSource : indexView.getAllKnownSubclasses(AsyncConfigSource.class)) {
            resourceProducer
                    .produce(new RunTimeConfigurationSourceBuildItem(configSource.name().toString(), OptionalInt.empty()));
        }

    }
}
