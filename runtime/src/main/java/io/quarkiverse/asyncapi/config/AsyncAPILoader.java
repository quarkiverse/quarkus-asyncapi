package io.quarkiverse.asyncapi.config;

import java.util.Map;

import com.asyncapi.v2.model.AsyncAPI;

public interface AsyncAPILoader {
    void load(Map<String, AsyncAPI> map);
}
