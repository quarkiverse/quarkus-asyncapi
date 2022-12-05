package io.quarkiverse.asyncapi.generator.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import io.quarkiverse.asyncapi.config.AsyncAPIExtension;

public interface InputStreamSupplier {
    InputStream get() throws IOException;

    AsyncAPIExtension getExtension();

    default Optional<String> getPackage() {
        return Optional.empty();
    }
}
