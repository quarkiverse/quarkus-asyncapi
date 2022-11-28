package io.quarkiverse.asyncapi.generator.input;

import java.io.IOException;
import java.util.Collection;

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
        boolean result = false;
        for (AsyncApiCodeGenerator generator : buildCodeGenerators(context))
            try {
                trigger(context, generator);
                result |= generator.done();
            } catch (IOException io) {
                throw new CodeGenException(io);
            }
        return result;
    }

    protected abstract Collection<AsyncApiCodeGenerator> buildCodeGenerators(CodeGenContext context);

    protected abstract void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws IOException;
}
