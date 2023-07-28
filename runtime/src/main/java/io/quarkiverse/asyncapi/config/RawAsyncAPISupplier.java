package io.quarkiverse.asyncapi.config;

import java.util.Collection;

import com.asyncapi.v2.model.AsyncAPI;

public class RawAsyncAPISupplier implements AsyncAPISupplier {

    private Collection<AsyncAPI> asyncAPIs;
    private String name;

    public RawAsyncAPISupplier(Collection<AsyncAPI> asyncAPIs, String name) {
        this.asyncAPIs = asyncAPIs;
        this.name = name;
    }

    public RawAsyncAPISupplier(Collection<AsyncAPI> asyncAPIs) {
        this(asyncAPIs, "async-from-dir");
    }

    @Override
    public Collection<AsyncAPI> asyncApis() {
        return asyncAPIs;
    }

    @Override
    public String name() {
        return name;
    }

}
