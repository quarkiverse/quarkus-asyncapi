package io.quarkiverse.asyncapi.generator.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.Config;

import io.quarkiverse.asyncapi.config.AsyncAPIExtension;
import io.quarkiverse.asyncapi.generator.AsyncApiCodeGenerator;
import io.quarkiverse.asyncapi.generator.AsyncApiConfigGroup;
import io.quarkus.deployment.CodeGenContext;

public class AsyncApiPathGeneratorCodeGen extends AsyncApiGeneratorCodeGenBase {

    protected AsyncApiPathGeneratorCodeGen(AsyncAPIExtension extension) {
        super(extension);
    }

    protected Collection<String> excludedFiles(final Config config) {
        return config.getOptionalValues(AsyncApiConfigGroup.EXCLUDED_FILES_PROP, String.class)
                .orElse(Collections.emptyList());
    }

    @Override
    protected void trigger(CodeGenContext context, AsyncApiCodeGenerator generator) throws IOException {
        final Path specDir = context.inputDir();
        final Collection<String> ignoredFiles = excludedFiles(context.config());
        if (Files.isDirectory(specDir)) {
            try (Stream<Path> specFilePaths = Files.walk(specDir)) {
                Collection<Path> files = specFilePaths
                        .filter(path -> isCandidateFile(path, ignoredFiles))
                        .collect(Collectors.toList());
                for (Path file : files) {
                    generator.generate(file);
                }
            }
        }
    }

    protected boolean isCandidateFile(Path path, Collection<String> ignoredFiles) {
        String fileName = path.getFileName().toString();
        return Files.isRegularFile(path) && fileName.endsWith(inputExtension()) && !ignoredFiles.contains(fileName);
    }

    @Override
    protected Collection<AsyncApiCodeGenerator> buildCodeGenerators(CodeGenContext context) {
        return List.of(new AsyncApiCodeGenerator(context.outDir(), context.config(), Optional.empty()));
    }
}
