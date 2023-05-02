
package io.quarkiverse.asyncapi.annotation.scanner;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * @since 13.02.2023
 */
public class UnFilteredProfile implements QuarkusTestProfile {

    @Override
    public String getConfigProfile() {
        return "unfiltered";
    }

}
