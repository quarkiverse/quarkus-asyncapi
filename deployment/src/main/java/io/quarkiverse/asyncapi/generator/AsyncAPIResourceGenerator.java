package io.quarkiverse.asyncapi.generator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.OptionalInt;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import io.quarkiverse.asyncapi.config.AsyncAPISupplier;
import io.quarkiverse.asyncapi.config.AsyncConfigSource;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationSourceBuildItem;

public class AsyncAPIResourceGenerator {

    @BuildStep
    void configSources(CombinedIndexBuildItem index, BuildProducer<RunTimeConfigurationSourceBuildItem> resourceProducer)
            throws IOException {
        IndexView indexView = index.getIndex();
        for (ClassInfo configSource : indexView.getAllKnownSubclasses(AsyncConfigSource.class)) {
            resourceProducer
                    .produce(new RunTimeConfigurationSourceBuildItem(configSource.name().toString(), OptionalInt.empty()));
        }
    }

    @BuildStep
    void asyncAPIs(CombinedIndexBuildItem index, BuildProducer<AsyncAPIBuildItem> resourceProducer)
            throws IOException {
        IndexView indexView = index.getIndex();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (ClassInfo supplier : indexView.getAllKnownSubclasses(AsyncAPISupplier.class)) {
            try {
				resourceProducer
				        .produce(new AsyncAPIBuildItem((AsyncAPISupplier)cl.loadClass(supplier.name().toString()).getConstructor().newInstance()));
			} catch (ReflectiveOperationException ex) {
				throw new IllegalStateException(ex);
			}
        }
    }
}
