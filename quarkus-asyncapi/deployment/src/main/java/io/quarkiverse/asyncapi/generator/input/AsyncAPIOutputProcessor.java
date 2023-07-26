package io.quarkiverse.asyncapi.generator.input;

import java.io.IOException;

import io.quarkiverse.asyncapi.config.AsyncAPIConfigBuilder;
import io.quarkiverse.asyncapi.config.AsyncAPISupplier;
import io.quarkiverse.asyncapi.config.AsyncAPISupplierFactory;
import io.quarkiverse.asyncapi.generator.AsyncAPIBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.StaticInitConfigBuilderBuildItem;

public class AsyncAPIOutputProcessor {

    @BuildStep
    StaticInitConfigBuilderBuildItem config() {
        return new StaticInitConfigBuilderBuildItem(AsyncAPIConfigBuilder.class.getCanonicalName());
    }

    @BuildStep
    void processResource(BuildProducer<AsyncAPIBuildItem> outputBI) throws IOException {
        for (AsyncAPISupplier supplier : AsyncAPISupplierFactory.get().getAsyncApiSuppliers()) {
            outputBI.produce(new AsyncAPIBuildItem(supplier));
        }
    }
}
