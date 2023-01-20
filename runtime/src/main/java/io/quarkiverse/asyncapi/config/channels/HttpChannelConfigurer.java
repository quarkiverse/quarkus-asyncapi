package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;

import com.asyncapi.v2.model.channel.operation.Operation;
import com.asyncapi.v2.model.server.Server;

public class HttpChannelConfigurer extends AbstractChannelConfigurer {

    private static final String PATH = "path";

    public HttpChannelConfigurer() {
        super("http", "quarkus-http");
    }

    @Override
    protected void addOutgoingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result) {
        result.put(outgoingProperty(smallryeChannel, PATH), channelName);
    }

    @Override
    protected void addIncomingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result) {
        result.put(incomingProperty(smallryeChannel, PATH), channelName);
    }
}
