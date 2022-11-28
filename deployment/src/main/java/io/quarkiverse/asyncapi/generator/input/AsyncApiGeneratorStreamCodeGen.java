package io.quarkiverse.asyncapi.generator.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import io.quarkiverse.asyncapi.generator.AsyncApiCodeGenerator;
import io.quarkiverse.asyncapi.generator.Extension;
import io.quarkiverse.asyncapi.generator.ObjectMapperFactory;
import io.quarkus.deployment.CodeGenContext;

public class AsyncApiGeneratorStreamCodeGen extends AsyncApiGeneratorCodeGenBase {

    public AsyncApiGeneratorStreamCodeGen() {
        super(Extension.unknown);
    }

    @Override
    public void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws IOException {
        for (AsyncApiSpecInputProvider provider : ServiceLoader.load(AsyncApiSpecInputProvider.class)) {
            for (Entry<String, InputStreamSupplier> entry : provider.read(context).entrySet()) {
                try (InputStream is = entry.getValue().get()) {
                    generator.generate(entry.getKey(), is, ObjectMapperFactory.get(entry.getValue().getExtension()),
                            entry.getValue().getPackage());
                }
            }
        }
    }
}
