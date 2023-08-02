
package io.quarkiverse.asyncapi.annotation.scanner;

import java.util.List;

import javax.measure.Quantity;

import io.quarkiverse.asyncapi.annotation.Schema;

public class TestMessage<T> {

    private String x;
    private String y;
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
