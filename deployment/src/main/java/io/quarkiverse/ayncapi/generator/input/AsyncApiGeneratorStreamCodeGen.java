package io.quarkiverse.ayncapi.generator.input;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import io.quarkiverse.ayncapi.generator.AsyncApiCodeGenerator;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

public class AsyncApiGeneratorStreamCodeGen extends AsyncApiGeneratorCodeGenBase {

    public AsyncApiGeneratorStreamCodeGen() {
        super(AsyncApiGeneratorCodeGenBase.STREAM);
    }

    @Override
    public void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws CodeGenException {
        for (AsyncApiSpecInputProvider provider : ServiceLoader.load(AsyncApiSpecInputProvider.class)) {
            for (Entry<String, InputStream> entry : provider.read(context).entrySet()) {
                generator.generate(entry.getKey(), entry.getValue());
            }
        }
    }
}
