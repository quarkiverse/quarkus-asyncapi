package io.quarkiverse.asyncapi.generator.input;

import java.util.Map;

public class AsyncAPISpecInput {
    private final Map<String, InputStreamSupplier> streams;
    private final String basePackage;

    public AsyncAPISpecInput(Map<String, InputStreamSupplier> streams) {
        this(streams, null);
    }

    public AsyncAPISpecInput(Map<String, InputStreamSupplier> streams, String basePackage) {
        this.streams = streams;
        this.basePackage = basePackage;
    }

    public Map<String, InputStreamSupplier> getStreams() {
        return streams;
    }

    public String getBasePackage() {
        return basePackage;
    }

    @Override
    public String toString() {
        return "AsyncAPISpecInput [streams=" + streams + ", basePackage=" + basePackage + "]";
    }
}
