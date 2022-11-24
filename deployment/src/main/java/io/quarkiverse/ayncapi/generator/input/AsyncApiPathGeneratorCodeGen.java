package io.quarkiverse.ayncapi.generator.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;

import io.quarkiverse.ayncapi.generator.AsyncApiCodeGenerator;
import io.quarkiverse.ayncapi.generator.AsyncApiConfigGroup;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;

public class AsyncApiPathGeneratorCodeGen extends AsyncApiGeneratorCodeGenBase {

    protected AsyncApiPathGeneratorCodeGen(String extension) {
        super(extension);
    }

    protected Collection<String> excludedFiles(final Config config) {
        return config.getOptionalValues(AsyncApiConfigGroup.EXCLUDED_FILES_PROP_FORMAT, String.class)
                .orElse(Collections.emptyList());
    }

    @Override
    protected void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws CodeGenException {
        final Path specDir = context.inputDir();
        final Collection<String> ignoredFiles = excludedFiles(context.config());

        Collection<Path> files = Collections.emptyList();
        if (Files.isDirectory(specDir)) {
            try (Stream<Path> specFilePaths = Files.walk(specDir)) {
                files = specFilePaths
                        .filter(path -> isCandidateFile(path, ignoredFiles))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new CodeGenException("Failed to generate java files from directory: " + specDir.toAbsolutePath(),
                        e);
            }
        }
        for (Path file : files) {
            generator.generate(file);
        }
    }

    protected boolean isCandidateFile(Path path, Collection<String> ignoredFiles) {
        String fileName = path.getFileName().toString();
        return Files.isRegularFile(path) && fileName.endsWith(inputExtension()) && !ignoredFiles.contains(fileName);
    }
}
