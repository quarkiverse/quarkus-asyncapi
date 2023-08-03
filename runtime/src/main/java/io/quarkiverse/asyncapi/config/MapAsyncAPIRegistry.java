package io.quarkiverse.asyncapi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asyncapi.v2.model.AsyncAPI;

public class MapAsyncAPIRegistry implements AsyncAPIRegistry {
    private final Map<String, AsyncAPI> map;
    private static final Logger logger = LoggerFactory.getLogger(MapAsyncAPIRegistry.class);

    public MapAsyncAPIRegistry(Iterable<AsyncAPISupplier> suppliers) {
        this.map = new HashMap<>();
        suppliers.forEach(supplier -> map.put(supplier.id(), supplier.asyncAPI()));
        logger.debug("AsyncAPI registry map {}", map);
    }

    public MapAsyncAPIRegistry(Map<String, AsyncAPI> map) {
        this.map = map;
    }

    @Override
    public Optional<AsyncAPI> getAsyncAPI(String id) {
        return Optional.ofNullable(map.get(AsyncAPIUtils.getJavaClassName(id)));
    }
}
