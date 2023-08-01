package io.quarkiverse.asyncapi.config;

import java.io.IOException;

import io.smallrye.config.ConfigSourceContext;

/**
 * Provider interface for clients to dynamically provide their own AsyncAPI specification files.
 */
public interface AsyncAPISpecInputProvider {

    AsyncAPISpecInput read(ConfigSourceContext context) throws IOException;
}
