
package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * @since 10.02.2023
 */
public class InMemoryConfigSource implements ConfigSource {
    private static final Map<String, String> configuration = new HashMap<>();

    static {
        configuration.put("my.prop", "1234");
    }

    @Override
    public int getOrdinal() {
        return 275;
    }

    @Override
    public Set<String> getPropertyNames() {
        return configuration.keySet();
    }

    @Override
    public String getValue(final String propertyName) {
        return configuration.get(propertyName);
    }

    @Override
    public String getName() {
        return InMemoryConfigSource.class.getSimpleName();
    }
}
