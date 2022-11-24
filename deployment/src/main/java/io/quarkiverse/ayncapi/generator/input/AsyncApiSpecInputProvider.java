package io.quarkiverse.ayncapi.generator.input;

import java.io.InputStream;
import java.util.Map;

import io.quarkus.deployment.CodeGenContext;

/**
 * Provider interface for clients to dynamically provide their own AyncAPI specification files.
 */
public interface AsyncApiSpecInputProvider {

    Map<String, InputStream> read(CodeGenContext context);
}
