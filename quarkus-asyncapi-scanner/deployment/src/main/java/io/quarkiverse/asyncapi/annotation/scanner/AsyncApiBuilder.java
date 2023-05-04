package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.Map;

import org.jboss.jandex.IndexView;

import com.asyncapi.v2._0_0.model.AsyncAPI;
import com.asyncapi.v2._0_0.model.server.Server;

import io.quarkiverse.asyncapi.annotation.scanner.config.AsyncApiRuntimeConfig;

/**
 * @since 02.03.2023
 */
public class AsyncApiBuilder {

    AsyncAPI build(IndexView aIndex, AsyncApiRuntimeConfig aConfig) {
        AsyncApiConfigResolver configResolver = new AsyncApiConfigResolver(aConfig);
        AsyncApiAnnotationScanner scanner = new AsyncApiAnnotationScanner(aIndex, configResolver);
        AsyncAPI.AsyncAPIBuilder builder = AsyncAPI.builder()
                .asyncapi(aConfig.version)
                //                id: 'https://github.com/smartylighting/streetlights-server'
                .id(configResolver.getConfiguredKafkaBootstrapServer())
                .info(configResolver.getInfo())
                .defaultContentType(aConfig.defaultContentType)
                .channels(scanner.getChannels())
                .components(scanner.getGlobalComponents());
        Map<String, Server> servers = configResolver.getServers();
        if (servers != null) {
            builder.servers(servers);
        }
        return builder
                .build();
    }
}
