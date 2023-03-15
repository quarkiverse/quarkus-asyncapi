package io.quarkiverse.asyncapi.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import com.asyncapi.v2._6_0.model.AsyncAPI;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AsyncAPIResourceGeneratorTest {

    @Inject
    AsyncAPIRegistry registry;

    @ConfigProperty(name = "mp.messaging.incoming./message.connector")
    Optional<String> kafkaIncomingConnector;

    @ConfigProperty(name = "mp.messaging.outgoing./message_out.connector")
    Optional<String> kafkaOutgoingConnector;

    @ConfigProperty(name = "mp.messaging.incoming.first.connector")
    Optional<String> httpIncomingConnector;

    @ConfigProperty(name = "mp.messaging.outgoing.first_out.connector")
    Optional<String> httpOutgoingConnector;

    @ConfigProperty(name = "mp.messaging.incoming./message.topic")
    Optional<String> incomingTopic;

    @ConfigProperty(name = "mp.messaging.outgoing./message_out.topic")
    Optional<String> outgoingTopic;

    @ConfigProperty(name = "mp.messaging.incoming.first.path")
    Optional<String> incomingPath;

    @ConfigProperty(name = "mp.messaging.outgoing.first_out.path")
    Optional<String> outgoingPath;

    @Test
    void testKafkaGenerator() {
        Optional<AsyncAPI> asyncAPI = registry.getAsyncAPI("Asyncapikafka_yml");
        assertThat(asyncAPI.isPresent()).isTrue();
        assertThat(asyncAPI.get().getId()).isEqualTo("urn:com:kafka:server");
        assertThat(kafkaIncomingConnector.get()).isEqualTo("smallrye-kafka");
        assertThat(kafkaOutgoingConnector.get()).isEqualTo("smallrye-kafka");
        assertThat(incomingTopic.get()).isEqualTo("/message");
        assertThat(outgoingTopic.get()).isEqualTo("/message");
    }

    @Test
    void testHttpGenerator() {
        Optional<AsyncAPI> asyncAPI = registry.getAsyncAPI("Asyncapihttp_yml");
        assertThat(asyncAPI.isPresent()).isTrue();
        assertThat(asyncAPI.get().getId()).isEqualTo("urn:com:http:server");
        assertThat(httpIncomingConnector.get()).isEqualTo("quarkus-http");
        assertThat(httpOutgoingConnector.get()).isEqualTo("quarkus-http");
        assertThat(incomingPath.get()).isEqualTo("first");
        assertThat(outgoingPath.get()).isEqualTo("first");
    }
}
