package io.quarkiverse.asyncapi.generator.input;

import io.quarkiverse.asyncapi.config.AsyncAPIExtension;

public class AsyncApiGeneratorYamlCodeGen extends AsyncApiPathGeneratorCodeGen {

    public AsyncApiGeneratorYamlCodeGen() {
        super(AsyncAPIExtension.yaml);
    }
}
