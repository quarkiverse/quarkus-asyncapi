package io.quarkiverse.asyncapi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.asyncapi.v3._0_0.model.AsyncAPI;

public class MapAsyncAPIRegistry implements AsyncAPIRegistry {
    private final Map<String, AsyncAPI> map;

    public MapAsyncAPIRegistry(Iterable<AsyncAPISupplier> suppliers) {
        this.map = new HashMap<>();
        suppliers.forEach(supplier -> map.put(supplier.id(), supplier.asyncAPI()));
    }

    public MapAsyncAPIRegistry(Map<String, AsyncAPI> map) {
        this.map = map;
    }

    @Override
    public Optional<AsyncAPI> getAsyncAPI(String id) {
        return Optional.ofNullable(map.get(AsyncAPIUtils.getJavaClassName(id)));
    }
}
