package io.quarkiverse.asyncapi.generator.input;

/**
 * Provider interface for clients to dynamically provide their own AsyncAPI specification files.
 */
public interface AsyncApiSpecInputProvider {

    AsyncAPISpecInput read();
}
