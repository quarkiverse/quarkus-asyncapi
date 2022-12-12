package io.quarkiverse.asyncapi.generator.input;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamSupplier {
    InputStream get() throws IOException;
}
