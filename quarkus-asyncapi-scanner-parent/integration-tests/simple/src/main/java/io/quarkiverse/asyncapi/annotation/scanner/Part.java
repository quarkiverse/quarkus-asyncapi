package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.wildfly.common.annotation.NotNull;

import com.fasterxml.jackson.annotation.JsonView;

@Schema(description = "Part definition. Part may be a product, a raw material, an equipment or any other part")
public class Part {

    @Schema(description = "Dimension of the part")
    @NotNull
    private Class<? extends List<?>> dimension;

    @Schema(description = "reference to partgroup of the part")
    @JsonView(TransferRelevant.class)
    private UUID partGroupId;

    @Schema(description = "reference to parttype of the part")
    @JsonView(TransferRelevant.class)
    private UUID partTypeId;

    @JsonView(TransferRelevant.class)
    private String externalId;

    @NotNull
    private AvailabilityState availabilityState = AvailabilityState.AVAILABLE;

    @Schema(description = "PROCUREMENT TYPE of the part: INHOUSE, EXTERNAL")
    @NotNull
    ProcurementType procurementType = ProcurementType.EXTERNAL;

    @Schema(description = "Material Type of the part: MATERIAL, PRODUCT")
    @NotNull
    MaterialType materialType = MaterialType.MATERIAL;

    //    @Schema(description = "expiration time of the part in seconds")
    //    private GecDuration expirationTime;
    private WorkorderQuantityAdjustmentMode workorderQuantityAdjustmentMode;

    private WorkorderFinishMode workorderFinishMode;

    @Schema(description = "Percentage value that the workorder quantity could be overbooked. In combination with finishMode the workorder quantity could be overbooked up to this value before the workorder state will be set automatically to finished")
    private Integer workorderQuantityOverbookPercentage;

    /**
     * Availability-State of Part Components Values: AVAILABLE, DISCHARGED, BLOCKED, DELETED
     */
    @Schema(description = "Availability State of the part")
    public enum AvailabilityState {
        AVAILABLE,
        BLOCKED,
        DISCHARGED,
        DELETED;
    }

    /**
     * ProcurementType of a part/material, defines id the part is produced Inhouse or is delivered from external
     * Values:INHOUSE, EXTERNAL
     */
    @Schema(description = "Procurement Type of a part")
    public enum ProcurementType {
        INHOUSE,
        EXTERNAL;
    }

    @Schema(description = "Material Type of a part: PRODUCT, MATERIAL, etc. A Product is based on a BOM, Material is not based on a BOM.")
    public enum MaterialType {
        PRODUCT(true),
        MATERIAL(true),
        EQUIPMENT(false);

        final boolean bomItemApplicable;

        MaterialType(boolean aBomItemApplicable) {
            bomItemApplicable = aBomItemApplicable;
        }

        public boolean isBomItemApplicable() {
            return bomItemApplicable;
        }
    }

    @Schema(description = "Modes to set the workorder state automatically to finish")
    public enum WorkorderFinishMode {
        @Schema(description = "No automated finish of a workorder. Workorder has to be closed manually")
        NONE,
        @Schema(description = "Set the Workorder to finish, when the quantityStarted reaches the workorder quantity")
        FINISH_ON_QUANTITY_STARTED,
        @Schema(description = "Set the Workorder to finish, when the quantityFinished reaches the workorder quantity")
        FINISH_ON_QUANTITY_FINISHED,
    }

    @Schema(description = "QuantityAjustment for the workorder quantity. Defines whether the workorder quantity should be increased in special cases (e.g. increase by Scrap-Booking)")
    public enum WorkorderQuantityAdjustmentMode {
        @Schema(description = "No automatic increasing of the workorder quantity")
        NONE,
        @Schema(description = "Increasing the workorder quantity for each scrap booking")
        ADJUST_SCRAP
    }

    //<editor-fold defaultstate="collapsed" desc="getter & setter">
    public UUID getPartGroupId() {
        return partGroupId;
    }

    public Part setPartGroupId(UUID partGroupId) {
        this.partGroupId = partGroupId;
        return this;
    }

    public UUID getPartTypeId() {
        return partTypeId;
    }

    public Part setPartTypeId(UUID partTypeId) {
        this.partTypeId = partTypeId;
        return this;
    }

    public AvailabilityState getAvailabilityState() {
        return availabilityState;
    }

    public Part setAvailabilityState(AvailabilityState availabilityState) {
        this.availabilityState = availabilityState;
        return this;
    }

    public ProcurementType getProcurementType() {
        return procurementType;
    }

    public Part setProcurementType(ProcurementType procurementType) {
        this.procurementType = procurementType;
        return this;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public Part setMaterialType(MaterialType aMaterialype) {
        this.materialType = aMaterialype;
        return this;
    }

    public WorkorderQuantityAdjustmentMode getWorkorderQuantityAdjustmentMode() {
        return workorderQuantityAdjustmentMode;
    }

    public Part setWorkorderQuantityAdjustmentMode(WorkorderQuantityAdjustmentMode aWorkorderQuantityAdjustmentMode) {
        this.workorderQuantityAdjustmentMode = aWorkorderQuantityAdjustmentMode;
        return this;
    }

    public WorkorderFinishMode getWorkorderFinishMode() {
        return workorderFinishMode;
    }

    public Part setWorkorderFinishMode(WorkorderFinishMode aWorkorderFinishMode) {
        this.workorderFinishMode = aWorkorderFinishMode;
        return this;
    }

    public Integer getWorkorderQuantityOverbookPercentage() {
        return workorderQuantityOverbookPercentage;
    }

    public Part setWorkorderQuantityOverbookPercentage(Integer aWorkorderQuantityOverbookPercentage) {
        this.workorderQuantityOverbookPercentage = aWorkorderQuantityOverbookPercentage;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public Part setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="toString">
    @Override
    public String toString() {
        return "Part{"
                + ", dimension=" + dimension
                + ", partGroupId=" + partGroupId
                + ", partTypeId=" + partTypeId
                + ", availabilityState=" + availabilityState
                + ", procurementType=" + procurementType
                + ", materialype=" + materialType
                + ", externalId=" + externalId
                + '}';
    }
    //</editor-fold>
}
