package io.quarkiverse.asyncapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AsyncApiCodeGeneratorTest {

    private Path outPath;

    @BeforeEach
    void setup() throws IOException {
        outPath = Path.of("temp");
        Files.createDirectories(outPath);
    }

    @Test
    void testGenerator() throws IOException {
        AsyncApiCodeGenerator generator = new AsyncApiCodeGenerator(outPath, Mockito.mock(Config.class));
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("asyncapi.yml")) {
            generator.generate("Test", is, ObjectMapperFactory.get(Extension.yml), Optional.empty());
        }
        Optional<Path> generatedFile = Files.walk(outPath)
                .filter(path -> path.getFileName().toString().equals("TestConfigSource.java")).findAny();
        assertThat(generatedFile.isPresent()).isTrue();
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, null);
        assertThat(
                javac.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjects(generatedFile.orElseThrow()))
                        .call())
                .isTrue();
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.walk(outPath).map(Path::toFile).forEach(File::delete);
    }
}
