package io.quarkiverse.asyncapi.annotation.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AsyncApiAnnotationScannerFilteredTest {

    JsonNode asyncAPI;

    @BeforeEach
    void readAsynApiYaml() throws IOException, JsonProcessingException {
        String yaml = Files
                .readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.yaml")).stream()
                .collect(Collectors.joining("\n"));
        assertThat(yaml).isNotNull();
        //        System.out.println(yaml);
        asyncAPI = ObjectMapperFactory.yaml().readTree(yaml);
    }

    //TODO check for channel with both incomming & outgoing
    @Test
    void shouldScanAndFilterEmitterAnnotations_CheckTransferChannel1() throws Exception {
        //given
        assertThat(asyncAPI.at("/channels/prices").isMissingNode()).isFalse();
        assertThat(asyncAPI.at("/channels/prices-intern").isMissingNode()).as("intern channels should be ignored").isTrue();
        //TransferChannel1 channel
        assertThat(asyncAPI.at("/channels/transfer-channel1/bindings/kafka/topic").asText())
                .isEqualTo("transfer-topic");
        assertThat(asyncAPI.at("/channels/transfer-channel1/address").asText())
                .isEqualTo("transfer-channel1");
        assertThat(asyncAPI.at("/channels/transfer-channel1/description").asText())
                .isEqualTo("description of the transfer-channel1 from application.properties");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/contentType")
                .asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/name")
                .asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.TransferMessage");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/description")
                .asText())
                .isEqualTo("TransferMessage description");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/type")
                .asText()).isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties"))
                .hasSize(10);
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/action/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/description/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/kafkaKey/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/trigger/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/type/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/user/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/maxBookDate/$ref")
                .asText())
                .isEqualTo("#/components/schemas/OffsetDateTime");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/minBookDate/$ref")
                .asText())
                .isEqualTo("#/components/schemas/OffsetDateTime");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/plantId/$ref")
                .asText())
                .isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/transfer-channel1/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/payload/properties/value/$ref")
                .asText())
                .isEqualTo("#/components/schemas/TransferWorkorderMessage");
        //TransferChannel1 operation
        assertThat(asyncAPI
                .at("/operations/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/action")
                .asText())
                .isEqualTo("send");
        assertThat(asyncAPI
                .at("/operations/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/channel/$ref")
                .asText())
                .isEqualTo("#/channels/transfer-channel1");
        assertThat(asyncAPI
                .at("/operations/io.quarkiverse.asyncapi.annotation.scanner.DummyController.transferEmitter1/description")
                .asText())
                .isEqualTo("transferEmitter description1");
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
        //when
        //operation
        assertThat(asyncAPI
                .at("/operations/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/action")
                .asText())
                .isEqualTo("receive");
        assertThat(asyncAPI
                .at("/operations/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/channel/$ref")
                .asText())
                .isEqualTo("#/channels/incoming-channel-part");
        //channel
        //IncomingChannelPart
        assertThat(asyncAPI.at("/channels/incoming-channel-part/bindings/kafka/topic").asText())
                .isEqualTo("incoming-channel-part-topic");
        //GecMessage
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/contentType")
                .asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/name")
                .asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.GecMessage");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/description")
                .asText())
                .isEqualTo("GecMessage description");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/type")
                .asText()).isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties"))
                .hasSize(5);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties/action/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties/kafkaKey/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties/user/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties/plantId/$ref")
                .asText())
                .isEqualTo("#/components/schemas/UUID");
        //Part
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties/value/$ref")
                .asText())
                .isEqualTo("#/components/schemas/Part");
        assertThat(asyncAPI
                .at("/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties/value/$ref")
                .asText()).isEqualTo("#/components/schemas/Part");
    }

    @Test
    void shouldScanAndFilterEmitterAnnotations_CheckIncomingChannelString() throws Exception {
        //when
        //channels
        assertThat(asyncAPI.at("/channels/incoming-channel-string/bindings/kafka/topic").asText())
                .isEqualTo("incoming-channel-string-topic");
        //GecMessage
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/contentType")
                .asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/name")
                .asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.GecMessage");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/description")
                .asText())
                .isEqualTo("GecMessage description");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/type")
                .asText())
                .isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties"))
                .hasSize(5);
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/action/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/kafkaKey/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/user/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/plantId/$ref")
                .asText())
                .isEqualTo("#/components/schemas/UUID");
        //Part
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/type")
                .asText())
                .isEqualTo("object");
        assertThat(asyncAPI
                .at("/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/description")
                .asText()).isEqualTo("Part definition. Part may be a product, a raw material, an equipment or any other part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-part/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessagePart/payload/properties/value/$ref")
                .asText())
                .isEqualTo("#/components/schemas/Part");
        //
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/availabilityState/$ref")
                .asText()).isEqualTo("#/components/schemas/AvailabilityState");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/dimension/description")
                .asText()).isEqualTo("Dimension of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/dimension/type")
                .asText()).isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/externalId/type")
                .asText()).isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/materialType/description")
                .asText()).isEqualTo("Material Type of the part: MATERIAL, PRODUCT");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/materialType/$ref")
                .asText()).isEqualTo("#/components/schemas/MaterialType");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/partGroupId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/partGroupId/description")
                .asText()).isEqualTo("reference to partgroup of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/partTypeId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/partTypeId/description")
                .asText()).isEqualTo("reference to parttype of the part");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/procurementType/description")
                .asText()).isEqualTo("PROCUREMENT TYPE of the part: INHOUSE, EXTERNAL");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/procurementType/$ref")
                .asText()).isEqualTo("#/components/schemas/ProcurementType");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/workorderFinishMode/$ref")
                .asText()).isEqualTo("#/components/schemas/WorkorderFinishMode");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/workorderQuantityAdjustmentMode/$ref")
                .asText()).isEqualTo("#/components/schemas/WorkorderQuantityAdjustmentMode");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/workorderQuantityOverbookPercentage/description")
                .asText())
                .isEqualTo(
                        "Percentage value that the workorder quantity could be overbooked. In combination with finishMode the workorder quantity could be overbooked up to this value before the workorder state will be set automatically to finished");
        assertThat(asyncAPI.at(
                "/channels/incoming-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.receiveMessageString/payload/properties/value/properties/workorderQuantityOverbookPercentage/type")
                .asText())
                .isEqualTo("integer");
    }

    @Test
    void shouldScanAndFilterEmitterAnnotations_CheckOutgoingChannelString() throws Exception {
        //when
        //channel
        assertThat(asyncAPI.at("/channels/outgoing-channel-string/bindings/kafka/topic").asText())
                .isEqualTo("outgoing-channel-string-topic");
        //GecMessage
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/contentType")
                .asText())
                .isEqualTo("application/json");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/name")
                .asText())
                .isEqualTo("io.quarkiverse.asyncapi.annotation.scanner.GecMessage");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload"))
                .hasSize(3);
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/description")
                .asText())
                .isEqualTo("GecMessage description");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/type")
                .asText())
                .isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties"))
                .hasSize(5);
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/action/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/kafkaKey/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/user/type")
                .asText())
                .isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/plantId/$ref")
                .asText())
                .isEqualTo("#/components/schemas/UUID");
        //Part
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/type")
                .asText())
                .isEqualTo("array");
        assertThat(asyncAPI
                .at("/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/description")
                .asText()).isEqualTo("Part definition. Part may be a product, a raw material, an equipment or any other part");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties"))
                .hasSize(10);
        //
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/availabilityState/$ref")
                .asText())
                .isEqualTo("#/components/schemas/AvailabilityState");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/dimension/description")
                .asText()).isEqualTo("Dimension of the part");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/dimension/type")
                .asText()).isEqualTo("object");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/externalId/type")
                .asText()).isEqualTo("string");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/materialType/description")
                .asText()).isEqualTo("Material Type of the part: MATERIAL, PRODUCT");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/materialType/$ref")
                .asText())
                .isEqualTo("#/components/schemas/MaterialType");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/partGroupId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/partGroupId/description")
                .asText()).isEqualTo("reference to partgroup of the part");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/partTypeId/$ref")
                .asText()).isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/partTypeId/description")
                .asText()).isEqualTo("reference to parttype of the part");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/procurementType/description")
                .asText()).isEqualTo("PROCUREMENT TYPE of the part: INHOUSE, EXTERNAL");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/procurementType/$ref")
                .asText())
                .isEqualTo("#/components/schemas/ProcurementType");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/workorderFinishMode/$ref")
                .asText())
                .isEqualTo("#/components/schemas/WorkorderFinishMode");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/workorderQuantityAdjustmentMode/$ref")
                .asText())
                .isEqualTo("#/components/schemas/WorkorderQuantityAdjustmentMode");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/workorderQuantityOverbookPercentage/description")
                .asText())
                .isEqualTo(
                        "Percentage value that the workorder quantity could be overbooked. In combination with finishMode the workorder quantity could be overbooked up to this value before the workorder state will be set automatically to finished");
        assertThat(asyncAPI.at(
                "/channels/outgoing-channel-string/messages/io.quarkiverse.asyncapi.annotation.scanner.DummyController.sendMessageString/payload/properties/value/items/properties/workorderQuantityOverbookPercentage/type")
                .asText())
                .isEqualTo("integer");
    }

    @Test
    void shouldHaveComponentSchemasReferences() throws Exception {
        //then
        assertThat(asyncAPI.at("/components/schemas/AvailabilityState/enum")).hasSize(4);
        assertThat(asyncAPI.at("/components/schemas/Company/type").asText()).isEqualTo("object");
        assertThat(asyncAPI.at("/components/schemas/Company/properties")).hasSize(7);
        assertThat(asyncAPI.at("/components/schemas/Company/properties/description/type").asText()).isEqualTo("string");
        assertThat(asyncAPI.at("/components/schemas/Company/properties/info1/type").asText()).isEqualTo("string");
        assertThat(asyncAPI.at("/components/schemas/Company/properties/info2/type").asText()).isEqualTo("string");
        assertThat(asyncAPI.at("/components/schemas/Company/properties/info3/type").asText()).isEqualTo("string");
        assertThat(asyncAPI.at("/components/schemas/Company/properties/name/type").asText()).isEqualTo("string");
        assertThat(asyncAPI.at("/components/schemas/Company/properties/payload/type").asText()).isEqualTo("string");
        assertThat(asyncAPI.at("/components/schemas/Company/properties/tenantId/description").asText())
                .isEqualTo("Id references the Tenant");
        assertThat(asyncAPI.at("/components/schemas/MaterialType/enum")).hasSize(3);
        assertThat(asyncAPI.at("/components/schemas/Part/properties/availabilityState/$ref").asText())
                .isEqualTo("#/components/schemas/AvailabilityState");
        assertThat(asyncAPI.at("/components/schemas/Part/description").asText())
                .isEqualTo("Part definition. Part may be a product, a raw material, an equipment or any other part");
        assertThat(asyncAPI.at("/components/schemas/Part/properties")).hasSize(10);
        assertThat(asyncAPI.at("/components/schemas/Part/properties/externalId/type").asText()).isEqualTo("string");
        assertThat(asyncAPI.at("/components/schemas/Part/properties/partGroupId/$ref").asText())
                .isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at("/components/schemas/Part/properties/partGroupId/description").asText())
                .isEqualTo("reference to partgroup of the part");
        assertThat(asyncAPI.at("/components/schemas/Part/properties/partTypeId/$ref").asText())
                .isEqualTo("#/components/schemas/UUID");
        assertThat(asyncAPI.at("/components/schemas/Part/properties/partTypeId/description").asText())
                .isEqualTo("reference to parttype of the part");
        assertThat(asyncAPI.at("/components/schemas/Part/type").asText()).isEqualTo("object");
        assertThat(asyncAPI.at("/components/schemas/ProcurementType/enum")).hasSize(2);
        assertThat(asyncAPI.at("/components/schemas/TransferWorkorderMessage/properties")).hasSize(3);
        assertThat(asyncAPI.at("/components/schemas/TransferWorkorderMessage/properties/bookDate/$ref").asText())
                .isEqualTo("#/components/schemas/OffsetDateTime");
        assertThat(asyncAPI.at("/components/schemas/TransferWorkorderMessage/properties/part/$ref").asText())
                .isEqualTo("#/components/schemas/Part");
        assertThat(asyncAPI.at("/components/schemas/TransferWorkorderMessage/properties/company/$ref").asText())
                .isEqualTo("#/components/schemas/Company");
        assertThat(asyncAPI.at("/components/schemas/WorkorderFinishMode/enum")).hasSize(3);
        assertThat(asyncAPI.at("/components/schemas/WorkorderQuantityAdjustmentMode/enum")).hasSize(2);
    }
}
