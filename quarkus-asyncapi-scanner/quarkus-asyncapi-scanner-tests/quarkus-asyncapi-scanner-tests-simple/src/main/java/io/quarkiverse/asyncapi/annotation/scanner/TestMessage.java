
package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.List;

import javax.measure.Quantity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TestMessage<T> implements HasTranslations {

    private String x;
    private String y;
    @Min(5)
    @Max(10)
    @NotNull
    @Deprecated
    private Integer sum;
    private T data;
    private T[] dataArray;
    private String[] stringArray;
    private List<T> dataList;
    private List<String> stringList;
    private Quantity quantity;
    private Translation translation;
    @org.eclipse.microprofile.openapi.annotations.media.Schema(oneOf = { String.class, Integer.class })
    private Object openApiOneOfObject;

}
