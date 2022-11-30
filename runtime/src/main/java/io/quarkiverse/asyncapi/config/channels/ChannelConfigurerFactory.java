package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.asyncapi.v2.model.server.Server;

public class ChannelConfigurerFactory {

    private ChannelConfigurerFactory() {
    }

    private static Map<String, ChannelConfigurer> configurers = ServiceLoader.load(ChannelConfigurer.class).stream()
            .map(provider -> provider.get()).collect(Collectors.toMap(ChannelConfigurer::protocol, Function.identity()));

    public static ChannelConfigurer get(Server server) {
        return Objects.requireNonNull(configurers.get(server.getProtocol()), "Unsupported protocol " + server.getProtocol());
    }
}
