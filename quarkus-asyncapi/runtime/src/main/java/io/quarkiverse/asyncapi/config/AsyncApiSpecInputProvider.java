package io.quarkiverse.asyncapi.config;

import io.smallrye.config.ConfigSourceContext;

/**
 * Provider interface for clients to dynamically provide their own AsyncAPI specification files.
 */
public interface AsyncApiSpecInputProvider {

    AsyncAPISpecInput read(ConfigSourceContext context);
}
