package io.quarkiverse.asyncapi.annotation.scanner.kafka.binding;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 06.03.2023
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicConfiguration {

    static final String CLEANUP_POLICY = "cleanup.policy";
    static final String RETENTION_MS = "retention.ms";
    static final String RETENTION_BYTES = "retention.bytes";
    static final String DELETE_RETENTION_MS = "delete.retention.ms";
    static final String MAX_MESSAGE_BYTES = "max.message.bytes";

    /**
     * ["delete", "compact"]
     */
    @JsonProperty(CLEANUP_POLICY)
    private String cleanupPolicy;
    @JsonProperty(RETENTION_MS)
    private Long retentionMs;
    @JsonProperty(RETENTION_BYTES)
    private Long retentionBytes;
    @JsonProperty(DELETE_RETENTION_MS)
    private Long deleteRetentionMs;
    @JsonProperty(MAX_MESSAGE_BYTES)
    private Long maxMessageBytes;
}
