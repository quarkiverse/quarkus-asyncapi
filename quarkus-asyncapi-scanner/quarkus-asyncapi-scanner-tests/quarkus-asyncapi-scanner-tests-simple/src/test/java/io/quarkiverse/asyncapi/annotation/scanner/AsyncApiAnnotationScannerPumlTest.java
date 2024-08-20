package io.quarkiverse.asyncapi.annotation.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AsyncApiAnnotationScannerPumlTest {

    @Test
    void shouldCreateValidPuml() throws IOException, JsonProcessingException {
        List<String> puml = Files
                .readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.puml")).stream()
                .map(s -> s.replace("\\n", " ")) //assertj has problems comparing strings with newlines
                .collect(Collectors.toList());
        assertThat(puml).isNotNull();
        assertThat(puml).isNotEmpty();
        assertThat(puml).first().isEqualTo("@startuml");
        assertThat(puml).last().isEqualTo("@enduml");
        assertThat(puml).contains(
                "[Test Service API (test) 1.2.3] -[#red,bold]-> (topic-x)",
                "[Test Service API (test) 1.2.3] -[#red,bold]-> (inOutTopic)",
                "[Test Service API (test) 1.2.3] <-[#green,bold]- (prices)",
                "[Test Service API (test) 1.2.3] <-[#green,bold]- (topic-y)",
                "[Test Service API (test) 1.2.3] <-[#green,bold]- (incoming-channel-part-topic)",
                "[Test Service API (test) 1.2.3] <-[#green,bold]- (incoming-channel-string-topic)",
                "[Test Service API (test) 1.2.3] <-[#green,bold]- (inOutTopic)",
                "[Test Service API (test) 1.2.3] -[#red,bold]-> (outgoing-channel-string-topic)",
                "[Test Service API (test) 1.2.3] -[#red,bold]-> (outgoing-channel-part-topic)",
                "[Test Service API (test) 1.2.3] -[#red,bold]-> (outgoing-channel-reactive-part-topic)",
                "[Test Service API (test) 1.2.3] -[#red,bold]-> (transfer-topic)");
    }
}
