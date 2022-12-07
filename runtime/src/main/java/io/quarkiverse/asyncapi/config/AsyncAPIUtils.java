package io.quarkiverse.asyncapi.config;

import io.smallrye.config.common.utils.StringUtil;

public class AsyncAPIUtils {

    public static String getJavaClassName(String name) {
        return capitalizeFirst(StringUtil.replaceNonAlphanumericByUnderscores(name));
    }

    private static String capitalizeFirst(String name) {
        char ch = name.charAt(0);
        return Character.isUpperCase(ch) ? name : Character.toUpperCase(ch) + name.substring(1);
    }

}
