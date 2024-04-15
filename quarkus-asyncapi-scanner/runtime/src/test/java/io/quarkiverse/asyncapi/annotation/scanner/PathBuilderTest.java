package io.quarkiverse.asyncapi.annotation.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PathBuilderTest {

    @Nested
    class appendPath {

        @ParameterizedTest
        @CsvSource({
                "http://localhost:8080/api, /asyncapi.yaml, http://localhost:8080/api/asyncapi.yaml",
                "http://localhost:8080/api, /foo.json,      http://localhost:8080/api/foo.json",
        })
        void appends_to_the_rootPath(String rootPath, String append, String expected) {
            String actual = new PathBuilder(rootPath)
                    .appendPath(append);

            assertEquals(expected, actual);
        }

        @ParameterizedTest
        @CsvSource({
                "'',                     /asyncapi.yaml",
                "/,                      /asyncapi.yaml",
                "////,                   /asyncapi.yaml",
                "/api,                   /api/asyncapi.yaml",
                "/api/,                  /api/asyncapi.yaml",
                "http://localhost:8080,  http://localhost:8080/asyncapi.yaml",
                "http://localhost:8080/, http://localhost:8080/asyncapi.yaml",
        })
        void normalizes_root_path_before_appending(String rootPath, String expected) {
            String actual = new PathBuilder(rootPath)
                    .appendPath("/asyncapi.yaml");

            assertEquals(expected, actual);
        }

    }

}
