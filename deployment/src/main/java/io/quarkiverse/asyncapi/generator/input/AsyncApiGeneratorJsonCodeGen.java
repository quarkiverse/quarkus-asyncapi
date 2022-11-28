package io.quarkiverse.asyncapi.generator.input;

import io.quarkiverse.asyncapi.generator.AsyncAPIExtension;

public class AsyncApiGeneratorJsonCodeGen extends AsyncApiPathGeneratorCodeGen {

    public AsyncApiGeneratorJsonCodeGen() {
        super(AsyncAPIExtension.json);
    }
}
