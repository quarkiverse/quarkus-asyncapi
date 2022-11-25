package io.quarkiverse.asyncapi.generator.input;

import java.util.Map;

import io.quarkus.deployment.CodeGenContext;

/**
 * Provider interface for clients to dynamically provide their own AyncAPI specification files.
 */
public interface AsyncApiSpecInputProvider {

    Map<String, InputStreamSupplier> read(CodeGenContext context);
}
