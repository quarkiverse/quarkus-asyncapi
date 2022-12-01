package io.quarkiverse.asyncapi.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.Test;

import com.asyncapi.v2.model.AsyncAPI;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AsyncAPIResourceGeneratorTest {

    @Inject
    AsyncAPIRegistry registry;

    @ConfigProperty(name = "mp.messaging.incoming./message.connector")
    Optional<String> incomingConnector;

    @ConfigProperty(name = "mp.messaging.outgoing./message.connector")
    Optional<String> outgoingConnector;

    @Test
    void testGenerator() {
        Iterable<ConfigSource> sources = ConfigProvider.getConfig().getConfigSources();
        Optional<AsyncAPI> asyncAPI = registry.getAsyncAPI("Asyncapi_yml");
        assertThat(asyncAPI.isPresent()).isTrue();
        assertThat(asyncAPI.get().getId()).isEqualTo("urn:com:kafka:server");
        assertThat(incomingConnector.isPresent()).isTrue();
        assertThat(incomingConnector.get()).isEqualTo("smallrye-kafka");
        assertThat(outgoingConnector.isPresent()).isTrue();
        assertThat(outgoingConnector.get()).isEqualTo("smallrye-kafka");
    }
}
