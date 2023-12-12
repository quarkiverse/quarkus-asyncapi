package io.quarkiverse.asyncapi.annotation.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(UnFilteredProfile.class)
public class AsyncApiAnnotationScannerUnFilteredTest {

    @Test
    void shouldScanEmitterAnnotations() throws Exception {
        //given
        String yaml = Files.readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.yaml")).stream()
                .collect(Collectors.joining("\n"));
        assertThat(yaml).isNotNull();
        JsonNode asyncAPI = ObjectMapperFactory.yaml().readTree(yaml);
        //when
        assertThat(asyncAPI.at("/channels")).isInstanceOf(ObjectNode.class);
        assertThat(asyncAPI.at("/channels")).hasSizeGreaterThanOrEqualTo(6);
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload")).hasSize(3);
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/$ref").asText())
                .isEqualTo("#/components/schemas/TransferWorkorderMessage");
        assertThat(asyncAPI.at("/components/schemas/TransferWorkorderMessage/properties/part/$ref").asText())
                .isEqualTo("#/components/schemas/Part");
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/$ref").asText())
                .isEqualTo("#/components/schemas/TransferWorkorderMessage");
        assertThat(asyncAPI.at("/components/schemas/TransferWorkorderMessage/properties/company/$ref").asText())
                .isEqualTo("#/components/schemas/Company");
        //oneOf
        assertThat(asyncAPI.at("/channels/channel-x/publish/message/payload/properties/translation/$ref").asText())
                .isEqualTo("#/components/schemas/Translation");
        JsonNode oneOfOpenApiNodeOneOf = asyncAPI
                .at("/channels/channel-x/publish/message/payload/properties/openApiOneOfObject/oneOf");
        assertThat(oneOfOpenApiNodeOneOf.get(0).get("type").asText()).isEqualTo("string");
        assertThat(oneOfOpenApiNodeOneOf.get(1).get("type").asText()).isEqualTo("integer");

        //Uni<Message<Part>>
        assertThat(asyncAPI.at("/channels/outgoing-channel-reactive-part/publish/message/payload/$ref").asText())
                .isEqualTo("#/components/schemas/Part");

        //JsonGetter
        assertThat(asyncAPI.at("/channels/channel-x/publish/message/payload/properties/i18n/description").asText())
                .isNotEmpty();
        assertThat(
                asyncAPI.at("/channels/channel-x/publish/message/payload/properties/i18n/additionalProperties/$ref").asText())
                .isEqualTo("#/components/schemas/I18n");

        // @Deprecated, @Min, @Max
        assertThat(asyncAPI.at("/channels/channel-x/publish/message/payload/properties/sum/maximum").asInt()).isEqualTo(10);
        assertThat(asyncAPI.at("/channels/channel-x/publish/message/payload/properties/sum/minimum").asInt()).isEqualTo(5);
        assertThat(asyncAPI.at("/channels/channel-x/publish/message/payload/properties/sum/deprecated").asBoolean()).isTrue();

        //@NotNull
        assertThat(asyncAPI.at("/channels/channel-x/publish/message/payload/required")).hasSize(1);
        assertThat(asyncAPI.at("/channels/channel-x/publish/message/payload/required/0").asText()).isEqualTo("sum");

        //component/schemas
        JsonNode translationNodeOneOf = asyncAPI.at("/components/schemas/Translation/oneOf");
        assertThat(translationNodeOneOf).hasSize(2);
        assertThat(translationNodeOneOf.get(0).get("type").asText()).isEqualTo("string");
        assertThat(translationNodeOneOf.get(1).get("$ref").asText()).isEqualTo("#/components/schemas/I18n");
        JsonNode i18nNode = asyncAPI.at("/components/schemas/I18n");
        assertThat(i18nNode.get("description").asText()).isNotNull();
        assertThat(i18nNode.get("properties")).hasSizeGreaterThan(10);
        assertThat(asyncAPI.at("/components/schemas/Part/properties")).hasSizeGreaterThan(3);
        assertThat(asyncAPI.at("/components/schemas/Company/properties")).hasSize(7);
        assertThat(asyncAPI.at("/components/schemas/I18n/properties")).hasSizeGreaterThan(10);
    }
}
