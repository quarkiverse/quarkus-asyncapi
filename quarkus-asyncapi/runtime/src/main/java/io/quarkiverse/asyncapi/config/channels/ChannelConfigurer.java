package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;

import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.operation.Operation;
import com.asyncapi.v3._0_0.model.server.Server;

public interface ChannelConfigurer {

    String protocol();

    void channelConfig(Server server, Channel channel, Operation operation, Map<String, String> result);

    void commonConfig(Server server, Map<String, String> result);
}
