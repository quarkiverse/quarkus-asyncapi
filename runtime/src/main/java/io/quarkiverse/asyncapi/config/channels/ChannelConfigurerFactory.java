package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;
import java.util.Objects;

import com.asyncapi.v2.model.server.Server;

public class ChannelConfigurerFactory {

    private ChannelConfigurerFactory() {
    }

    // TODO make this extensible
    private static Map<String, ChannelConfigurer> configurers = Map.of("http", new HttpChannelConfigurer(), "kafka",
            new KafkaChannelConfigurer());

    public static ChannelConfigurer get(Server server) {
        return Objects.requireNonNull(configurers.get(server.getProtocol()));
    }
}
