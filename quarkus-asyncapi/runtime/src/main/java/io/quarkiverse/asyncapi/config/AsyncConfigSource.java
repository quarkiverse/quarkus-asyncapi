package io.quarkiverse.asyncapi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.operation.Operation;
import com.asyncapi.v3._0_0.model.server.Server;

import io.quarkiverse.asyncapi.config.channels.ChannelConfigurer;
import io.quarkiverse.asyncapi.config.channels.ChannelConfigurerFactory;
import io.smallrye.config.common.MapBackedConfigSource;

public class AsyncConfigSource extends MapBackedConfigSource {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfigSource.class);
    private static final String CHANNEL_PREFIX = "#/channels/";

    public AsyncConfigSource(AsyncAPISupplier asyncAPISupplier) {
        super(asyncAPISupplier.id(), getMapFromAsyncApi(asyncAPISupplier.asyncAPI()));
    }

    private static Map<String, String> getMapFromAsyncApi(AsyncAPI asyncAPI) {
        Map<String, String> result = new HashMap<>();
        for (Object serverObject : asyncAPI.getServers().values()) {
            Server server = (Server) serverObject;
            ChannelConfigurer configurer = ChannelConfigurerFactory.get(server);
            configurer.commonConfig(server, result);
            for (Entry<String, Object> entry : asyncAPI.getOperations().entrySet()) {
                Operation operation = (Operation) entry.getValue();
                getChannelFromOperation(asyncAPI.getChannels(), operation)
                        .ifPresent(channel -> configurer.channelConfig(server, channel, operation, result));
            }
        }
        logger.debug("Produced config source {}", result);
        return result;
    }

    private static Optional<Channel> getChannelFromOperation(Map<String, Object> channels, Operation oper) {
        String channelRef = oper.getChannel().getRef();
        if (channelRef.startsWith(CHANNEL_PREFIX)) {
            String channelName = channelRef.substring(CHANNEL_PREFIX.length());
            logger.debug("Checking channel name {}", channelName);
            return Optional.ofNullable((Channel) channels.get(channelName));
        }
        return Optional.empty();
    }
}
