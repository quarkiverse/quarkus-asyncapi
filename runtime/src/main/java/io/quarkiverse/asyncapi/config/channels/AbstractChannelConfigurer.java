package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;

import com.asyncapi.v2._6_0.model.channel.ChannelItem;
import com.asyncapi.v2._6_0.model.channel.operation.Operation;
import com.asyncapi.v2._6_0.model.server.Server;

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

    private final String protocol;
    private final String connectorId;

    protected AbstractChannelConfigurer(String protocol, String connectorId) {
        this.protocol = protocol;
        this.connectorId = connectorId;
    }

    public String protocol() {
        return protocol;
    }

    @Override
    public void channelConfig(String channelName, ChannelItem item, Server server, Map<String, String> result) {

        if (item.getSubscribe() != null) {
            String incomingChannel = channelName;
            result.put(incomingProperty(incomingChannel, CONNECTOR), connectorId);
            addIncomingChannel(incomingChannel, channelName, item.getPublish(), server, result);
        }
        if (item.getPublish() != null) {
            String outgoingChannel = channelName + "_out";
            result.put(outgoingProperty(outgoingChannel, CONNECTOR), connectorId);
            addOutgoingChannel(outgoingChannel, channelName, item.getPublish(), server, result);
        }

    }

    public void commonConfig(Server server, Map<String, String> result) {

    }

    protected abstract void addOutgoingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result);

    protected abstract void addIncomingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result);

}
