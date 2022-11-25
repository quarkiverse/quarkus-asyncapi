package io.quarkiverse.asyncapi.config;

import java.util.Map;

import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.channel.operation.Operation;
import com.asyncapi.v2.model.server.Server;

public abstract class AbstractChannelConfigurer implements ChannelConfigurer {

    private static final String INCOMING_CHANNEL_PATTERN = "mp.messaging.incoming.%s.%s";
    private static final String OUTGOING_CHANNEL_PATTERN = "mp.messaging.outgoing.%s.%s";

    protected final String outgoingProperty(String channel, String property) {
        return String.format(OUTGOING_CHANNEL_PATTERN, channel, property);
    }

    protected final String incomingProperty(String channel, String property) {
        return String.format(INCOMING_CHANNEL_PATTERN, channel, property);
    }

    public static final String CONNECTOR = "connector";

    private final String connectorId;

    protected AbstractChannelConfigurer(String connectorId) {
        this.connectorId = connectorId;
    }

    @Override
    public void channelConfig(String channelName, ChannelItem item, Server server, Map<String, String> result) {
        if (item.getPublish() != null) {
            result.put(outgoingProperty(channelName, CONNECTOR), connectorId);
            addOutgoingChannel(channelName, item.getPublish(), server, result);
        }
        if (item.getSubscribe() != null) {
            result.put(incomingProperty(channelName, CONNECTOR), connectorId);
            addIncomingChannel(channelName, item.getPublish(), server, result);
        }
    }

    public void commonConfig(Server server, Map<String, String> result) {

    }

    protected abstract void addOutgoingChannel(String channelName, Operation operation, Server server,
            Map<String, String> result);

    protected abstract void addIncomingChannel(String channelName, Operation operation, Server server,
            Map<String, String> result);

}
