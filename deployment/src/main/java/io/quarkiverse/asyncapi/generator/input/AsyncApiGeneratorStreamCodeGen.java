package io.quarkiverse.asyncapi.generator.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ServiceLoader;

import io.quarkiverse.asyncapi.generator.AsyncApiCodeGenerator;
import io.quarkiverse.asyncapi.generator.Extension;
import io.quarkiverse.asyncapi.generator.ObjectMapperFactory;
import io.quarkus.deployment.CodeGenContext;

public class AsyncApiGeneratorStreamCodeGen extends AsyncApiGeneratorCodeGenBase {

    private Map<AsyncApiCodeGenerator, Map<String, InputStreamSupplier>> generators;

    public AsyncApiGeneratorStreamCodeGen() {
        super(Extension.stream);
    }

    @Override
    protected Collection<AsyncApiCodeGenerator> buildCodeGenerators(CodeGenContext context) {
        generators = new HashMap<>();
        for (AsyncApiSpecInputProvider provider : ServiceLoader.load(AsyncApiSpecInputProvider.class)) {
            AsyncAPISpecInput specInput = provider.read(context);
            generators.put(new AsyncApiCodeGenerator(context.outDir(), context.config(),
                    Optional.ofNullable(specInput.getBasePackage())), specInput.getStreams());
        }
        return generators.keySet();
    }

    @Override
    public void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws IOException {
        for (Entry<String, InputStreamSupplier> entry : generators.get(generator).entrySet()) {
            try (InputStream is = entry.getValue().get()) {
                generator.generate(entry.getKey(), is, ObjectMapperFactory.get(entry.getValue().getExtension()));
            }
        }
    }
}
