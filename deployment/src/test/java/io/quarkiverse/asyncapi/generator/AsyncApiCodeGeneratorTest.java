package io.quarkiverse.asyncapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

public class AsyncApiCodeGeneratorTest {

    private Path outPath;

    @Test
    void testGenerator(@TempDir Path outPath) throws IOException {
        Path genPath = outPath.resolve("src").resolve("yml");
        AsyncApiCodeGenerator generator = new AsyncApiCodeGenerator(genPath, Mockito.mock(Config.class), Optional.empty());
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("asyncapi.yml")) {
            generator.generate("Test", is);
            generator.done(false);
        }
        Collection<Path> generatedFiles = Files.walk(genPath).filter(Files::isRegularFile).collect(Collectors.toList());
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, null);
        assertThat(
                javac.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjectsFromPaths(generatedFiles))
                        .call())
                .isTrue();

        assertThat(Files.walk(outPath.resolve("classes")).filter(Files::isRegularFile)
                .anyMatch(p -> p.getFileName().toString().equals(AsyncApiCodeGenerator.SERVICE_LOADER))).isTrue();
    }
}
