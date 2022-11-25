package io.quarkiverse.asyncapi.generator.input;

import io.quarkiverse.asyncapi.generator.AsyncApiCodeGenerator;
import io.quarkiverse.asyncapi.generator.Extension;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.CodeGenProvider;

public abstract class AsyncApiGeneratorCodeGenBase implements CodeGenProvider {

    protected final Extension extension;

    protected AsyncApiGeneratorCodeGenBase(Extension extension) {
        this.extension = extension;
    }

    private final static String ASYNC_API = "asyncapi";

    @Override
    public String providerId() {
        return ASYNC_API + "-" + inputExtension();
    }

    @Override
    public String inputDirectory() {
        return ASYNC_API;
    }

    @Override
    public String inputExtension() {
        return extension.toString();
    }

    @Override
    public boolean trigger(CodeGenContext context) throws CodeGenException {
        AsyncApiCodeGenerator generator = new AsyncApiCodeGenerator(context.outDir(), context.config());
        trigger(context, generator);
        return generator.done();
    }

    protected abstract void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws CodeGenException;
}
