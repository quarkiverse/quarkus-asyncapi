package io.quarkiverse.asyncapi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asyncapi.v2.model.AsyncAPI;
import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.server.Server;

import io.quarkiverse.asyncapi.config.channels.ChannelConfigurer;
import io.quarkiverse.asyncapi.config.channels.ChannelConfigurerFactory;

public class AsyncAPIConfigSource implements ConfigSource {

    private static final Logger logger = LoggerFactory.getLogger(AsyncAPIConfigSource.class);
    private AsyncAPISupplier asyncAPISupplier;
    private Map<String, String> map;
    private String name;

    public AsyncAPIConfigSource(AsyncAPISupplier asyncAPISupplier) {
        this.name = "config-" + asyncAPISupplier.name();
        this.asyncAPISupplier = asyncAPISupplier;
    }

    private static void fillMapFromAsyncApi(AsyncAPI asyncAPI, Map<String, String> result) {
        for (Server server : asyncAPI.getServers().values()) {
            ChannelConfigurer configurer = ChannelConfigurerFactory.get(server);
            configurer.commonConfig(server, result);
            for (Entry<String, ChannelItem> entry : asyncAPI.getChannels().entrySet()) {
                configurer.channelConfig(entry.getKey(), entry.getValue(), server, result);
            }
        }
        logger.debug("Produced map {}", result);
    }

    @Override
    public Map<String, String> getProperties() {
        if (map == null) {
            synchronized (this) {
                if (map == null) {
                    map = new HashMap<>();
                    asyncAPISupplier.asyncApis().forEach(a -> fillMapFromAsyncApi(a, map));
                }
            }
        }
        return map;
    }

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return getProperties().get(propertyName);
    }

    @Override
    public String getName() {
        return name;
    }
}
