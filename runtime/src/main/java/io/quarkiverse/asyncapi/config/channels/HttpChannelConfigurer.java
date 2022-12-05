package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;

import com.asyncapi.v2.model.channel.operation.Operation;
import com.asyncapi.v2.model.server.Server;

public class HttpChannelConfigurer extends AbstractChannelConfigurer {

    public HttpChannelConfigurer() {
        super("http", "quarkus-http");
    }

    @Override
    protected void addOutgoingChannel(String channelName, Operation operation, Server server,
            Map<String, String> result) {

    }

    @Override
    protected void addIncomingChannel(String channelName, Operation operation, Server server,
            Map<String, String> result) {

    }

}
