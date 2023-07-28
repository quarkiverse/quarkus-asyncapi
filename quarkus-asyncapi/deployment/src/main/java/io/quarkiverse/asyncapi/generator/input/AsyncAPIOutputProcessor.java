package io.quarkiverse.asyncapi.generator.input;

import io.quarkiverse.asyncapi.config.AsyncAPISupplier;
import io.quarkiverse.asyncapi.config.AsyncAPISupplierFactory;
import io.quarkiverse.asyncapi.generator.AsyncAPIBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;

public class AsyncAPIOutputProcessor {
    @BuildStep
    void processResource(BuildProducer<AsyncAPIBuildItem> outputBI) {
        for (AsyncAPISupplier supplier : AsyncAPISupplierFactory.getAsyncApiSuppliers()) {
            outputBI.produce(new AsyncAPIBuildItem(supplier));
        }
    }
}
