package io.quarkiverse.ayncapi.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;

import io.quarkus.bootstrap.prebuild.CodeGenException;

public class AsyncApiCodeGenerator {

    private boolean hasGenerated;
    private final Path outPath;
    private final Config config;

    public AsyncApiCodeGenerator(Path outPath, Config config) {
        this.outPath = outPath;
        this.config = config;
    }

    public void generate(Path path) throws CodeGenException {
        try (InputStream is = Files.newInputStream(path)) {
            generate(path.getFileName().toString(), is);
        } catch (IOException e) {
            throw new CodeGenException(e);
        }
    }

    public void generate(String id, InputStream stream) throws CodeGenException {

        hasGenerated = true;
    }

    public boolean hasGenerated() {
        return hasGenerated;
    }
}
