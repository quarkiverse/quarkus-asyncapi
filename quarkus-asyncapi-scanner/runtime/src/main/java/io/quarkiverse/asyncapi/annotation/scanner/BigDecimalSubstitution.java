
package io.quarkiverse.asyncapi.annotation.scanner;

import java.math.BigDecimal;

import io.quarkus.runtime.ObjectSubstitution;

/**
 * @since 03.08.2023
 */
public class BigDecimalSubstitution implements ObjectSubstitution<BigDecimal, Double> {

    @Override
    public Double serialize(BigDecimal aObj) {
        return aObj == null ? null : aObj.doubleValue();
    }

    @Override
    public BigDecimal deserialize(Double aObj) {
        return aObj == null ? null : BigDecimal.valueOf(aObj);
    }

}
