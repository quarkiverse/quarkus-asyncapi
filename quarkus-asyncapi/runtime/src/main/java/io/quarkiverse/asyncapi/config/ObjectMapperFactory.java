package io.quarkiverse.asyncapi.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ObjectMapperFactory {

    private static ObjectMapper jsonObjectMapper = setupMapper(new ObjectMapper());
    private static ObjectMapper ymlObjectMapper = setupMapper(new ObjectMapper(new YAMLFactory()));

    private ObjectMapperFactory() {
    }

    private static ObjectMapper setupMapper(ObjectMapper mapper) {
        return mapper.findAndRegisterModules().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(Include.NON_EMPTY);
    }

    public static ObjectMapper yaml() {
        return ymlObjectMapper;
    }

    public static ObjectMapper json() {
        return jsonObjectMapper;
    }
}
