package io.quarkiverse.asyncapi.annotation.scanner;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

@Schema(description = "TransferMessage description")
@JsonView(TransferRelevant.class)
public class TransferMessage<T> extends GecMessage<T> {

    private String type;
    private String description;
    private OffsetDateTime minBookDate;
    private OffsetDateTime maxBookDate;
    private String trigger;

    public static <T> TransferMessage of(T aValue, Action aAction, String aKafkaKey, UUID aPlantId,
            String aType, String aDescription, OffsetDateTime aMinBookdate, OffsetDateTime aMaxBookdate, String aTrigger) {
        return new TransferMessage(aValue, aAction, aKafkaKey, aPlantId, aType, aDescription, aMinBookdate, aMaxBookdate,
                aTrigger);
    }

    protected TransferMessage() {
    }

    protected TransferMessage(T aValue, Action aAction, String aKafkaKey, UUID aPlantId,
            String aType, String aDescription, OffsetDateTime aMinBookDate, OffsetDateTime aMaxBookDate, String aTrigger) {
        super(aValue, aAction, aKafkaKey, aPlantId);
        this.type = aType;
        this.description = aDescription;
        this.minBookDate = aMinBookDate;
        this.maxBookDate = aMaxBookDate;
        this.trigger = aTrigger;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getMinBookDate() {
        return minBookDate;
    }

    public OffsetDateTime getMaxBookDate() {
        return maxBookDate;
    }

    public String getTrigger() {
        return trigger;
    }

    @Override
    public String toString() {
        return "TransferMessage{" + "type=" + type + ", description=" + description + ", minBookDate=" + minBookDate
                + ", maxBookDate=" + maxBookDate + ", trigger=" + trigger + '}';
    }
}
