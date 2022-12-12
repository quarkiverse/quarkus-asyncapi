package io.quarkiverse.asyncapi.generator;

import io.quarkiverse.asyncapi.config.AsyncAPISupplier;
import io.quarkus.builder.item.MultiBuildItem;

public final class AsyncAPIBuildItem extends MultiBuildItem {

    private final AsyncAPISupplier asyncAPI;

    public AsyncAPIBuildItem(AsyncAPISupplier asyncAPI) {
        this.asyncAPI = asyncAPI;
    }

    public AsyncAPISupplier getAsyncAPI() {
        return asyncAPI;
    }
}
