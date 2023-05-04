package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;

import com.asyncapi.v2._6_0.model.channel.ChannelItem;
import com.asyncapi.v2._6_0.model.server.Server;

public interface ChannelConfigurer {

    String protocol();

    void channelConfig(String channelName, ChannelItem item, Server server, Map<String, String> result);

    void commonConfig(Server server, Map<String, String> result);
}
