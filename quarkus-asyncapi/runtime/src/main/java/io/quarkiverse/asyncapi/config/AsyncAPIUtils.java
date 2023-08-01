package io.quarkiverse.asyncapi.config;

import java.util.Arrays;
import java.util.List;

import io.smallrye.config.ConfigSourceContext;

public final class AsyncAPIUtils {

    public static List<String> getValues(ConfigSourceContext context, String propertyName, List<String> defaultValue) {
        String propValue = context.getValue(propertyName).getValue();
        return propValue == null ? defaultValue : Arrays.asList(propValue.split(",; "));
    }

    private AsyncAPIUtils() {
    }
}
