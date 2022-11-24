package io.quarkiverse.ayncapi.generator.input;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamSupplier {
    InputStream get() throws IOException;
}
