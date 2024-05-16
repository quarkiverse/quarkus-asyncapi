package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;

import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.operation.Operation;
import com.asyncapi.v3._0_0.model.operation.OperationAction;
import com.asyncapi.v3._0_0.model.server.Server;

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
    public void channelConfig(Server server, Channel channel, Operation operation, Map<String, String> result) {
        if (operation.getAction() == OperationAction.RECEIVE) {
            String incomingChannel = channel.getAddress();
            result.put(incomingProperty(incomingChannel, CONNECTOR), connectorId);
            addIncomingChannel(incomingChannel, channel.getAddress(), operation, server, result);
        } else if (operation.getAction() == OperationAction.SEND) {
            String outgoingChannel = channel.getAddress() + "_out";
            result.put(outgoingProperty(outgoingChannel, CONNECTOR), connectorId);
            addOutgoingChannel(outgoingChannel, channel.getAddress(), operation, server, result);
        }
    }

    public void commonConfig(Server server, Map<String, String> result) {

    }

    protected abstract void addOutgoingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result);

    protected abstract void addIncomingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result);

}
