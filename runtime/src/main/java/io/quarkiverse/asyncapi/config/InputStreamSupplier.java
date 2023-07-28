package io.quarkiverse.asyncapi.config;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamSupplier {
    InputStream get() throws IOException;
}
