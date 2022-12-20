package io.quarkiverse.asyncapi.generator;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import io.quarkiverse.asyncapi.config.AsyncAPISupplier;
import io.quarkiverse.asyncapi.config.JacksonAsyncAPISupplier;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;

public class AsyncAPIResourceGenerator {

    @BuildStep
    void asyncAPIs(CombinedIndexBuildItem index, BuildProducer<AsyncAPIBuildItem> resourceProducer) {
        IndexView indexView = index.getIndex();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (ClassInfo supplier : indexView.getAllKnownSubclasses(JacksonAsyncAPISupplier.class)) {
            try {
                resourceProducer
                        .produce(new AsyncAPIBuildItem(
                                (AsyncAPISupplier) cl.loadClass(supplier.name().toString()).getDeclaredConstructor()
                                        .newInstance()));
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
