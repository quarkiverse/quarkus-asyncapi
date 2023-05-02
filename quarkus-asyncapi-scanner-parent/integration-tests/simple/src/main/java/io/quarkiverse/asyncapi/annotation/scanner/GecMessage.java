package io.quarkiverse.asyncapi.annotation.scanner;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Container for KafkaMessages with additional Infos
 *
 * @author kai
 * @param <T>
 */
@Schema(description = "GecMessage description")
@JsonView(TransferRelevant.class)
public class GecMessage<T> implements Serializable {

    public enum Action {
        CREATED,
        UPDATED,
        DELETED;
    }

    /**
     * Used in Kafka to set a Key for the Topic
     */
    private String kafkaKey;

    /**
     * Holds the User Information given from JWT/Keycloak
     */
    private String user;
    private UUID plantId;

    /**
     * Holds the action information
     */
    private String action;

    /**
     * Value --> KafkaMessage.....
     */
    private T value;

    public static <U> GecMessage<U> of(U aValue, Action aAction) {
        return new GecMessage<>(aValue, aAction, null, null);
    }

    public static <U> GecMessage<U> of(U aValue, Action aAction, String aKafkaKey) {
        return new GecMessage<>(aValue, aAction, aKafkaKey, null);
    }

    public static <U> GecMessage<U> of(U aValue, Action aAction, String aKafkaKey, UUID aPlantId) {
        return new GecMessage<>(aValue, aAction, aKafkaKey, aPlantId);
    }

    protected GecMessage() {
    }

    protected GecMessage(T aValue, Action aAction, String aKafkaKey, UUID aPlantId) {
        if (aPlantId != null) {
            plantId = aPlantId;
        }
        value = aValue;
        action = aAction.name();
        kafkaKey = aKafkaKey;
    }

    public Action getAction() {
        return Action.valueOf(action);
    }

    public String getUser() {
        return user;
    }

    public UUID getPlantId() {
        return plantId;
    }

    public T getValue() {
        return value;
    }

    protected void setValue(T aValue) {
        value = aValue;
    }

    public String getKafkaKey() {
        return kafkaKey;
    }

    @Override
    public String toString() {
        return "GecMessage " + action + " " + value.toString();
    }
}
