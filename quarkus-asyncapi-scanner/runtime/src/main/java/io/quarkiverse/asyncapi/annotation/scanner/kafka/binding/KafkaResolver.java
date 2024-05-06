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

import com.asyncapi.v3.binding.channel.kafka.KafkaChannelBinding;
import com.asyncapi.v3.binding.channel.kafka.KafkaChannelTopicCleanupPolicy;
import com.asyncapi.v3.binding.channel.kafka.KafkaChannelTopicConfiguration;

/**
 * @since 02.03.2023
 */
public class KafkaResolver {

    private static final Logger LOGGER = Logger.getLogger(KafkaResolver.class.getName());

    static final String CLEANUP_POLICY = "cleanup.policy";
    static final String RETENTION_MS = "retention.ms";
    static final String RETENTION_BYTES = "retention.bytes";
    static final String DELETE_RETENTION_MS = "delete.retention.ms";
    static final String MAX_MESSAGE_BYTES = "max.message.bytes";

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

    KafkaChannelTopicConfiguration getTopicConfiguration(AdminClient aClient, String aTopic) {
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
        KafkaChannelTopicCleanupPolicy cleanUpPolicy = KafkaChannelTopicCleanupPolicy
                .valueOf(configMap.get(CLEANUP_POLICY).value());
        return KafkaChannelTopicConfiguration.builder()
                .cleanupPolicy(List.of(cleanUpPolicy))
                .retentionMs(Integer.valueOf(configMap.get(RETENTION_MS).value()))
                .retentionBytes(Integer.valueOf(configMap.get(RETENTION_BYTES).value()))
                .deleteRetentionMs(Integer.valueOf(configMap.get(DELETE_RETENTION_MS).value()))
                .maxMessageBytes(Integer.valueOf(configMap.get(MAX_MESSAGE_BYTES).value()))
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
