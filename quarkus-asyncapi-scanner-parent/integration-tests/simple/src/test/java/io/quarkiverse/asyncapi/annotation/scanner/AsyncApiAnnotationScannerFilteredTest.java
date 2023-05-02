package io.quarkiverse.asyncapi.annotation.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AsyncApiAnnotationScannerFilteredTest {

    @Test
    void shouldScanAndFilterEmitterAnnotations_CheckTransferChannel1() throws Exception {
        //given
        String yaml = Files.readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.yaml")).stream()
                .collect(Collectors.joining("\n"));
        assertThat(yaml).isNotNull();
        System.out.println(yaml);
        JsonNode asyncAPI = ObjectMapperFactory.yaml().readTree(yaml);
        //when

        assertThat(asyncAPI.at("/channels")).isInstanceOf(ObjectNode.class);
        assertThat(asyncAPI.at("/channels")).hasSizeGreaterThanOrEqualTo(5);
        assertThat(asyncAPI.at("/channels/prices").isMissingNode()).isFalse();
        assertThat(asyncAPI.at("/channels/prices-intern").isMissingNode()).as("intern channels should be ignored").isTrue();
        //TransferChannel1
        assertThat(asyncAPI.at("/channels/transfer-channel1/description").asText())
                .isEqualTo("description of the transfer-channel1 from application.properties");
        assertThat(asyncAPI.at("/channels/transfer-channel1/bindings/kafka/topic").asText())
                .isEqualTo("transfer-topic");
        //TransferChannel1 Publish
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/description").asText())
                .isEqualTo("transferEmitter description1");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/operationId").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1");
        //TransferMessage
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/contentType").asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/name").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.TransferMessage");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload")).hasSize(3);
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/description").asText())
                .isEqualTo("TransferMessage description");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/type").asText()).isEqualTo("object");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties")).hasSize(10);
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/action/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/description/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/kafkaKey/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/trigger/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/type/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/user/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/maxBookDate/$ref").asText())
                .isEqualTo("#/components/schemas/OffsetDateTime");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/minBookDate/$ref").asText())
                .isEqualTo("#/components/schemas/OffsetDateTime");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/plantId/$ref").asText())
                .isEqualTo("#/components/schemas/UUID");
        //
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/type").asText())
                .isEqualTo("object");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/properties")).hasSize(3);
        //BookDate Only Ref available
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/bookDate/$ref")
                .asText())
                .isEqualTo("#/components/schemas/OffsetDateTime");
        //Part
        assertThat(
                asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/description")
                        .asText())
                .isEqualTo("Part definition. Part may be a product, a raw material, an equipment or any other part");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/type")
                .asText())
                .isEqualTo("object");
        assertThat(
                asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/properties"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/properties/partTypeId/$ref")
                .asText())
                .isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/properties/partTypeId/description")
                .asText())
                .isEqualTo("reference to parttype of the part");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/properties/partGroupId/$ref")
                .asText())
                .isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/properties/partGroupId/description")
                .asText())
                .isEqualTo("reference to partgroup of the part");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/properties/externalId/type")
                .asText())
                .isEqualTo("string");
        //Company
        assertThat(asyncAPI
                .at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/description")
                .asText())
                .isEqualTo("Company");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/type")
                .asText())
                .isEqualTo("object");
        assertThat(asyncAPI
                .at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties"))
                .hasSize(7);
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties/payload/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties/name/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties/tenantId/description")
                .asText())
                .isEqualTo("Id references the Tenant");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties/description/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties/info1/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties/info2/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties/info3/type")
                .asText())
                .isEqualTo("string");
        //Servers
        assertThat(asyncAPI.at("/servers/testServer/protocol").asText())
                .isEqualTo("kafka");
        //MyKafkaChannelBinding
        assertThat(asyncAPI.at("/channels/transfer-channel1/description").asText())
                .isEqualTo("description of the transfer-channel1 from application.properties");
        assertThat(asyncAPI.at("/channels/transfer-channel2/description").asText())
                .isEqualTo("description of the transfer-channel2 from application.properties");
    }

    @Test
    void shouldScanAndFilterEmitterAnnotations_CheckIncomingChannelPart() throws Exception {
        //given
        String yaml = Files.readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.yaml")).stream()
                .collect(Collectors.joining("\n"));
        assertThat(yaml).isNotNull();
        System.out.println(yaml);
        JsonNode asyncAPI = ObjectMapperFactory.yaml().readTree(yaml);
        //when
        assertThat(asyncAPI.at("/channels")).isInstanceOf(ObjectNode.class);
        assertThat(asyncAPI.at("/channels")).hasSizeGreaterThanOrEqualTo(5);

        //IncomingChannelPart
        assertThat(asyncAPI.at("/channels/incoming-channel-part/bindings/kafka/topic").asText())
                .isEqualTo("incoming-channel-part-topic");
        //IncomingChannelPart Subscribe
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/operationId").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart");
        //GecMessage
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/contentType").asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/name").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.GecMessage");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload")).hasSize(3);
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/description").asText())
                .isEqualTo("GecMessage description");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/type").asText()).isEqualTo("object");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties")).hasSize(5);
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties/action/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties/kafkaKey/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties/user/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties/plantId/$ref").asText())
                .isEqualTo("#/components/schemas/UUID");
        //Part
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties/value/type").asText())
                .isEqualTo("object");
        assertThat(asyncAPI
                .at("/channels/incoming-channel-part/subscribe/message/payload/properties/value/description")
                .asText()).isEqualTo("Part definition. Part may be a product, a raw material, an equipment or any other part");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties"))
                .hasSize(10);
        //
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/availabilityState/enum"))
                .hasSize(4);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/dimension/description")
                .asText()).isEqualTo("Dimension of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/dimension/type")
                .asText()).isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/externalId/type")
                .asText()).isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/materialType/description")
                .asText()).isEqualTo("Material Type of the part: MATERIAL, PRODUCT");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/materialType/enum"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/partGroupId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/partGroupId/description")
                .asText()).isEqualTo("reference to partgroup of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/partTypeId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/partTypeId/description")
                .asText()).isEqualTo("reference to parttype of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/procurementType/description")
                .asText()).isEqualTo("PROCUREMENT TYPE of the part: INHOUSE, EXTERNAL");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/procurementType/enum"))
                .hasSize(2);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/workorderFinishMode/enum"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/workorderQuantityAdjustmentMode/enum"))
                .hasSize(2);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/workorderQuantityOverbookPercentage/description")
                .asText())
                .isEqualTo(
                        "Percentage value that the workorder quantity could be overbooked. In combination with finishMode the workorder quantity could be overbooked up to this value before the workorder state will be set automatically to finished");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties/workorderQuantityOverbookPercentage/type")
                .asText())
                .isEqualTo("integer");
    }

    @Test
    void shouldScanAndFilterEmitterAnnotations_CheckIncomingChannelString() throws Exception {
        //given
        String yaml = Files.readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.yaml")).stream()
                .collect(Collectors.joining("\n"));
        assertThat(yaml).isNotNull();
        System.out.println(yaml);
        JsonNode asyncAPI = ObjectMapperFactory.yaml().readTree(yaml);
        //when
        assertThat(asyncAPI.at("/channels")).isInstanceOf(ObjectNode.class);
        assertThat(asyncAPI.at("/channels")).hasSizeGreaterThanOrEqualTo(5);

        //IncomingChannelPart
        assertThat(asyncAPI.at("/channels/incoming-channel-string/bindings/kafka/topic").asText())
                .isEqualTo("incoming-channel-string-topic");
        //IncomingChannelPart Subscribe
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/operationId").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString");
        //GecMessage
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/contentType").asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/name").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.GecMessage");
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload")).hasSize(3);
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/description").asText())
                .isEqualTo("GecMessage description");
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/type").asText())
                .isEqualTo("object");
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/properties")).hasSize(5);
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/properties/action/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/properties/kafkaKey/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/properties/user/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/properties/plantId/$ref").asText())
                .isEqualTo("#/components/schemas/UUID");
        //Part
        assertThat(asyncAPI.at("/channels/incoming-channel-string/subscribe/message/payload/properties/value/type").asText())
                .isEqualTo("object");
        assertThat(asyncAPI
                .at("/channels/incoming-channel-string/subscribe/message/payload/properties/value/description")
                .asText()).isEqualTo("Part definition. Part may be a product, a raw material, an equipment or any other part");
        assertThat(asyncAPI.at("/channels/incoming-channel-part/subscribe/message/payload/properties/value/properties"))
                .hasSize(10);
        //
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/availabilityState/enum"))
                .hasSize(4);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/dimension/description")
                .asText()).isEqualTo("Dimension of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/dimension/type")
                .asText()).isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/externalId/type")
                .asText()).isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/materialType/description")
                .asText()).isEqualTo("Material Type of the part: MATERIAL, PRODUCT");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/materialType/enum"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/partGroupId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/partGroupId/description")
                .asText()).isEqualTo("reference to partgroup of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/partTypeId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/partTypeId/description")
                .asText()).isEqualTo("reference to parttype of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/procurementType/description")
                .asText()).isEqualTo("PROCUREMENT TYPE of the part: INHOUSE, EXTERNAL");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/procurementType/enum"))
                .hasSize(2);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/workorderFinishMode/enum"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/workorderQuantityAdjustmentMode/enum"))
                .hasSize(2);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/workorderQuantityOverbookPercentage/description")
                .asText())
                .isEqualTo(
                        "Percentage value that the workorder quantity could be overbooked. In combination with finishMode the workorder quantity could be overbooked up to this value before the workorder state will be set automatically to finished");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/subscribe/message/payload/properties/value/properties/workorderQuantityOverbookPercentage/type")
                .asText())
                .isEqualTo("integer");
    }

    @Test
    void shouldScanAndFilterEmitterAnnotations_CheckOutgoingChannelString() throws Exception {
        //given
        String yaml = Files.readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.yaml")).stream()
                .collect(Collectors.joining("\n"));
        assertThat(yaml).isNotNull();
        System.out.println(yaml);
        JsonNode asyncAPI = ObjectMapperFactory.yaml().readTree(yaml);
        //when
        assertThat(asyncAPI.at("/channels")).isInstanceOf(ObjectNode.class);
        assertThat(asyncAPI.at("/channels")).hasSizeGreaterThanOrEqualTo(5);

        //IncomingChannelPart
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/bindings/kafka/topic").asText())
                .isEqualTo("outgoing-channel-string-topic");
        //IncomingChannelPart Subscribe
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/operationId").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString");
        //GecMessage
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/contentType").asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/name").asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.GecMessage");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload")).hasSize(3);
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/description").asText())
                .isEqualTo("GecMessage description");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/type").asText())
                .isEqualTo("object");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/properties")).hasSize(5);
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/properties/action/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/properties/kafkaKey/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/properties/user/type").asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/properties/plantId/$ref").asText())
                .isEqualTo("#/components/schemas/UUID");
        //Part
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/properties/value/type").asText())
                .isEqualTo("array");
        assertThat(asyncAPI
                .at("/channels/outgoing-channel-string/publish/message/payload/properties/value/items/description")
                .asText()).isEqualTo("Part definition. Part may be a product, a raw material, an equipment or any other part");
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties"))
                .hasSize(10);
        //
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/availabilityState/enum"))
                .hasSize(4);
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/dimension/description")
                .asText()).isEqualTo("Dimension of the part");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/dimension/type")
                .asText()).isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/externalId/type")
                .asText()).isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/materialType/description")
                .asText()).isEqualTo("Material Type of the part: MATERIAL, PRODUCT");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/materialType/enum"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/partGroupId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/partGroupId/description")
                .asText()).isEqualTo("reference to partgroup of the part");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/partTypeId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/partTypeId/description")
                .asText()).isEqualTo("reference to parttype of the part");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/procurementType/description")
                .asText()).isEqualTo("PROCUREMENT TYPE of the part: INHOUSE, EXTERNAL");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/procurementType/enum"))
                .hasSize(2);
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/workorderFinishMode/enum"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/workorderQuantityAdjustmentMode/enum"))
                .hasSize(2);
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/workorderQuantityOverbookPercentage/description")
                .asText())
                .isEqualTo(
                        "Percentage value that the workorder quantity could be overbooked. In combination with finishMode the workorder quantity could be overbooked up to this value before the workorder state will be set automatically to finished");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/publish/message/payload/properties/value/items/properties/workorderQuantityOverbookPercentage/type")
                .asText())
                .isEqualTo("integer");
    }
}
