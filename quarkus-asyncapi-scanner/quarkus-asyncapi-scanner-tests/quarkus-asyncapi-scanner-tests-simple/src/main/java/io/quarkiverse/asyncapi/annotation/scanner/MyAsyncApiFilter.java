package io.quarkiverse.asyncapi.annotation.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.channel.message.Message;
import com.asyncapi.v3.schema.AsyncAPISchema;
import com.asyncapi.v3.schema.Type;
import com.fasterxml.jackson.annotation.JsonView;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * @since 13.02.2023
 */
@RegisterForReflection
public class MyAsyncApiFilter implements AsyncApiFilter {

    @Override
    public Channel filterChannel(String aName, Channel aChannel) {
        if (aName.contains("transfer")) {
            Map<String, Object> messages = aChannel.getMessages();
            Map.Entry<String, Object> firstMessage = messages.entrySet().iterator().next();
            Message message = (Message) firstMessage.getValue();
            Class<?> messageClass = getMessageClass(firstMessage.getKey());
            if (messageClass != null) {
                AsyncAPISchema transferMessagePayload = (AsyncAPISchema) message.getPayload();
                recurse(messageClass, (AsyncAPISchema) transferMessagePayload.getProperties().get("value"));
            }
        }
        return aChannel;
    }

    void recurse(Class aClass, AsyncAPISchema aSchema) {
        if (aSchema.getProperties() == null) {
            return;
        }
        //get over all fields
        Map<String, Object> filteredPayload = aSchema.getProperties().entrySet().stream()
                .filter(e -> isClassTransferRelevant(aClass) || isFieldTransferRelevant(aClass, e.getKey()))
                .peek(e -> {
                    if (Type.OBJECT.equals(((AsyncAPISchema) e.getValue()).getType())) {
                        Field field = getFieldRecursiv(aClass, e.getKey());
                        if (field != null) {
                            recurse(field.getType(), (AsyncAPISchema) e.getValue());
                        }
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        aSchema.setProperties(filteredPayload);
    }

    Field getFieldRecursiv(Class aClass, String aFieldName) {
        try {
            return aClass.getDeclaredField(aFieldName);
        } catch (NoSuchFieldException | SecurityException noSuchFieldException) {
            return aClass.getSuperclass() == Object.class
                    ? null
                    : getFieldRecursiv(aClass.getSuperclass(), aFieldName);
        }
    }

    Class<?> getMessageClass(String aOperationId) {
        try {
            Class<?> clazz = Class.forName(aOperationId.substring(0, aOperationId.lastIndexOf('.')));
            Field field = clazz.getDeclaredField(aOperationId.substring(aOperationId.lastIndexOf(".") + 1));
            ParameterizedType outerGenericType = (ParameterizedType) field.getGenericType();
            ParameterizedType innerGenericType = (ParameterizedType) outerGenericType.getActualTypeArguments()[0];
            return (Class<?>) innerGenericType.getActualTypeArguments()[0];
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    boolean isClassTransferRelevant(Class aClass) {
        return aClass.isAnnotationPresent(JsonView.class)
                && Arrays.stream(((JsonView) aClass.getAnnotation(JsonView.class)).value())
                        .anyMatch(TransferRelevant.class::equals);
    }

    boolean isFieldTransferRelevant(Class aClass, String aName) {
        Field field = getFieldRecursiv(aClass, aName);
        if (field == null) {
            return false;
        }
        return (field.isAnnotationPresent(JsonView.class)
                && Arrays.stream(((JsonView) field.getAnnotation(JsonView.class)).value())
                        .anyMatch(TransferRelevant.class::equals))
                || isMethodTransferRelevant(getGetter(field));
    }

    boolean isMethodTransferRelevant(Method aMethod) {
        if (aMethod == null) {
            return false;
        }
        return aMethod.isAnnotationPresent(JsonView.class)
                && Arrays.stream(((JsonView) aMethod.getAnnotation(JsonView.class)).value())
                        .anyMatch(TransferRelevant.class::equals);
    }

    Method getGetter(Field aField) {
        try {
            return aField.getDeclaringClass()
                    .getMethod("get" + aField.getName().substring(0, 1).toUpperCase() + aField.getName().substring(1));
        } catch (NoSuchMethodException | SecurityException noSuchMethodException) {
            return null;
        }
    }
}
