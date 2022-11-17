package io.quarkiverse.asyncapi.meta;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.asyncapi.v2.model.AsyncAPI;

@ApplicationScoped
public class InMemoryAsyncAPIRegister implements AsyncAPIRegistry, AsyncAPIRecorder {

    private final Map<String, AsyncAPI> asyncAPIs = new HashMap<>();

    @Override
    public AsyncAPI getAsyncAPI(String id) {
        return asyncAPIs.get(id);
    }

    public void addAsyncAPI(String id, AsyncAPI asyncAPI) {
        asyncAPIs.put(id, asyncAPI);
    }
}
