package io.quarkiverse.ayncapi.generator.input;

import io.quarkiverse.ayncapi.generator.AsyncApiCodeGenerator;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public abstract class AsyncApiGeneratorCodeGenBase implements CodeGenProvider {

    public static final String YAML = "yaml";
    public static final String YML = "yml";
    public static final String JSON = "json";
    public static final String STREAM = "stream";

    private final String extension;

    protected AsyncApiGeneratorCodeGenBase(String extension) {
        this.extension = extension;
    }

    private final static String ASYNC_API = "asyncapi";

    @Override
    public String providerId() {
        return ASYNC_API;
    }

    @Override
    public String inputDirectory() {
        return ASYNC_API;
    }

    @Override
    public String inputExtension() {
        return extension;
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        AsyncApiCodeGenerator generator = new AsyncApiCodeGenerator(context.outDir(), context.config());
        trigger(context, generator);
        return generator.hasGenerated();
    }

    protected abstract void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws CodeGenException;
}
