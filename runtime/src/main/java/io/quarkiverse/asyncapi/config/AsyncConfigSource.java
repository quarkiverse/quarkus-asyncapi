package io.quarkiverse.asyncapi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.asyncapi.v2.model.AsyncAPI;
import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.server.Server;

import io.quarkiverse.asyncapi.config.channels.ChannelConfigurer;
import io.quarkiverse.asyncapi.config.channels.ChannelConfigurerFactory;
import io.smallrye.config.common.MapBackedConfigSource;

public class AsyncConfigSource extends MapBackedConfigSource {

    private static final long serialVersionUID = 1L;

    public AsyncConfigSource(AsyncAPISupplier asyncAPISupplier) {
        super(asyncAPISupplier.id(), getMapFromAsyncApi(asyncAPISupplier.asyncAPI()));
    }

    private static Map<String, String> getMapFromAsyncApi(AsyncAPI asyncAPI) {
        Map<String, String> result = new HashMap<>();
        for (Server server : asyncAPI.getServers().values()) {
            ChannelConfigurer configurer = ChannelConfigurerFactory.get(server);
            configurer.commonConfig(server, result);
            for (Entry<String, ChannelItem> entry : asyncAPI.getChannels().entrySet()) {
                configurer.channelConfig(entry.getKey(), entry.getValue(), server, result);
            }
        }
        return result;
    }
}
