package io.quarkiverse.asyncapi.annotation.scanner.kafka.binding;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * @since 02.03.2023
 */
public class KafkaResolver {

    private static final Logger LOGGER = Logger.getLogger(KafkaResolver.class.getName());

    private AdminClient adminClient;

    public KafkaChannelBinding getKafkaChannelBindings(String aTopic) {
        KafkaChannelBinding.KafkaChannelBindingBuilder builder = KafkaChannelBinding.builder()
                .topic(aTopic);
        Optional<String> bootStrapServers = ConfigProvider.getConfig()
                .getOptionalValue("kafka.bootstrap.servers", String.class);
        if (bootStrapServers.isPresent()) {
            Map<String, Object> properties = Map.of("bootstrap.servers", bootStrapServers.get());
            if (adminClient == null) {
                adminClient = AdminClient.create(properties);
            }
            try {
                if (isTopicExists(adminClient, aTopic)) {
                    DescribeTopicsResult topicDescription = adminClient.describeTopics(
                            Set.of(aTopic), new DescribeTopicsOptions().timeoutMs(100));
                    List<TopicPartitionInfo> partitionInfos = topicDescription.topicNameValues().get(aTopic).get().partitions();
                    builder.partitions(partitionInfos.size())
                            .replicas(partitionInfos.get(0).replicas().size())
                            .topicConfiguration(getTopicConfiguration(adminClient, aTopic));
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.log(Level.WARNING, "Unable to describe topic {0}", aTopic);
            }
        } else {
            LOGGER.log(Level.WARNING, "No kafka.bootstrap.server configured");
        }
        return builder.build();
    }

    TopicConfiguration getTopicConfiguration(AdminClient aClient, String aTopic) {
        Map<String, ConfigEntry> configMap = aClient
                .describeConfigs(List.of(new ConfigResource(ConfigResource.Type.TOPIC, aTopic)))
                .values().values().stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException interruptedException) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(Config::entries)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(ConfigEntry::name, Function.identity()));
        return TopicConfiguration.builder()
                .cleanupPolicy(configMap.get(TopicConfiguration.CLEANUP_POLICY).value())
                .retentionMs(Long.valueOf(configMap.get(TopicConfiguration.RETENTION_MS).value()))
                .retentionBytes(Long.valueOf(configMap.get(TopicConfiguration.RETENTION_BYTES).value()))
                .deleteRetentionMs(Long.valueOf(configMap.get(TopicConfiguration.DELETE_RETENTION_MS).value()))
                .maxMessageBytes(Long.valueOf(configMap.get(TopicConfiguration.MAX_MESSAGE_BYTES).value()))
                .build();
    }

    private boolean isTopicExists(AdminClient admin, String topicName) throws InterruptedException, ExecutionException {
        return admin.listTopics(new ListTopicsOptions().timeoutMs(100)).names().get().contains(topicName);
    }

    public void close() {
        if (adminClient != null) {
            adminClient.close();
        }
    }
}
