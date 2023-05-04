package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Company (Buchkreis) definition a Tenant may have multiple companies, but at
 * least one. A company may have one or more plants.
 *
 */
@Schema(description = "Company")
@JsonView(TransferRelevant.class)
public class Company {

    //    @JsonView(TransferRelevant.class)
    private String name;
    private String description;
    private String info1;
    private String info2;
    private String info3;
    private String payload;
    @Schema(description = "Id references the Tenant")
    private UUID tenantId;

    //<editor-fold defaultstate="collapsed" desc="getter & setter">
    @Schema
    public String getName() {
        return name;
    }

    @Schema
    public Company setName(String name) {
        this.name = name;
        return this;
    }

    @Schema
    public String getDescription() {
        return description;
    }

    @Schema
    public Company setDescription(String description) {
        this.description = description;
        return this;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public Company setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Schema
    public String getInfo1() {
        return info1;
    }

    @Schema
    public Company setInfo1(String info1) {
        this.info1 = info1;
        return this;
    }

    @Schema
    public String getInfo2() {
        return info2;
    }

    @Schema
    public Company setInfo2(String info2) {
        this.info2 = info2;
        return this;
    }

    @Schema
    public String getInfo3() {
        return info3;
    }

    @Schema
    public Company setInfo3(String info3) {
        this.info3 = info3;
        return this;
    }

    @Schema
    public String getPayload() {
        return payload;
    }

    @Schema
    public Company setPayload(String payload) {
        this.payload = payload;
        return this;
    }
    //</editor-fold>

    @Schema
    public String toString() {
        return "Company{"
                + ", name=" + name
                + ", description=" + description
                + ", tenantId=" + tenantId + '}';
    }

}
