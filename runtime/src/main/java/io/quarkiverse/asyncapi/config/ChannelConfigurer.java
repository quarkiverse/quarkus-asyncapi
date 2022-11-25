package io.quarkiverse.asyncapi.config;

import java.util.Map;

import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.server.Server;

public interface ChannelConfigurer {
    void channelConfig(String channelName, ChannelItem item, Server server, Map<String, String> result);

    void commonConfig(Server server, Map<String, String> result);
}
