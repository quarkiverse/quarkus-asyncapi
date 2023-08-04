package io.quarkiverse.asyncapi.generator.input;

import io.quarkus.deployment.CodeGenContext;

/**
 * Provider interface for clients to dynamically provide their own AsyncAPI specification files.
 */
public interface AsyncApiSpecInputProvider {

    AsyncAPISpecInput read(CodeGenContext context);
}
