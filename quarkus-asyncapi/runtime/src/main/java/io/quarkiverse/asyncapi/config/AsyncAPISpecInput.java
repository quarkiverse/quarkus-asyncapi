package io.quarkiverse.asyncapi.config;

import java.util.Map;

public class AsyncAPISpecInput {
    private final Map<String, InputStreamSupplier> streams;

    public AsyncAPISpecInput(Map<String, InputStreamSupplier> streams) {
        this.streams = streams;
    }

    public Map<String, InputStreamSupplier> getStreams() {
        return streams;
    }

    @Override
    public String toString() {
        return "AsyncAPISpecInput [streams=" + streams + "]";
    }
}
