package io.quarkiverse.asyncapi.annotation.scanner;

import static org.jboss.jandex.PrimitiveType.Primitive.CHAR;
import static org.jboss.jandex.PrimitiveType.Primitive.DOUBLE;
import static org.jboss.jandex.PrimitiveType.Primitive.FLOAT;
import static org.jboss.jandex.PrimitiveType.Primitive.INT;
import static org.jboss.jandex.PrimitiveType.Primitive.LONG;
import static org.jboss.jandex.PrimitiveType.Primitive.SHORT;
import static org.jboss.jandex.Type.Kind.CLASS;
import static org.jboss.jandex.Type.Kind.PARAMETERIZED_TYPE;
import static org.jboss.jandex.Type.Kind.PRIMITIVE;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.Declaration;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.JandexReflection;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;

import com.asyncapi.v3.Reference;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.ExternalDocumentation;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.channel.message.Message;
import com.asyncapi.v3._0_0.model.component.Components;
import com.asyncapi.v3._0_0.model.operation.Operation;
import com.asyncapi.v3._0_0.model.operation.OperationAction;
import com.asyncapi.v3.binding.operation.kafka.KafkaOperationBinding;
import com.asyncapi.v3.schema.AsyncAPISchema;

import io.quarkiverse.asyncapi.annotation.scanner.kafka.binding.KafkaResolver;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

/**
 * @since 09.02.2023
 * @author christiant
 */
public class AsyncApiAnnotationScanner {

    public static final String CONFIG_PREFIX = "io.quarkiverse.asyncapi";
    static final Logger LOGGER = Logger.getLogger(AsyncApiAnnotationScanner.class.getName());
    static final DotName OPEN_API_SCHEMA_ANNOTATION = DotName
            .createSimple("org.eclipse.microprofile.openapi.annotations.media.Schema");
    static final String GLOBAL_COMPONENTS_SCHEMAS_PREFIX = "#/components/schemas/";
    static final TreeMap<String, Object> GLOBAL_COMPONENTS = new TreeMap<>(Map.of(
            "OffsetDateTime", AsyncAPISchema.builder()
                    .format("date-time")
                    .externalDocs(ExternalDocumentation.builder()
                            .description(
                                    "A date-time with an offset from UTC/Greenwich in the ISO-8601 calendar system")
                            .url("https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html")
                            .build())
                    .type(com.asyncapi.v3.schema.Type.STRING)
                    .examples(List.of("2022-03-10T12:15:50-04:00"))
                    .build(),
            "UUID", AsyncAPISchema.builder()
                    .format("uuid")
                    .pattern("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
                    .type(com.asyncapi.v3.schema.Type.STRING)
                    .build(),
            "LocalTime", AsyncAPISchema.builder()
                    .format("local-time")
                    .type(com.asyncapi.v3.schema.Type.STRING)
                    .externalDocs(ExternalDocumentation.builder()
                            .description("ISO-8601 representation of a extended local time")
                            .url("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_TIME")
                            .build())
                    .examples(List.of("13:45.30.123456789"))
                    .build(),
            "Duration", AsyncAPISchema.builder()
                    .format("duration")
                    .type(com.asyncapi.v3.schema.Type.STRING)
                    .externalDocs(ExternalDocumentation.builder()
                            .description("ISO-8601 representation of a duration")
                            .url("https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#toString--")
                            .build())
                    .examples(List.of("P1D"))
                    .build(),
            "DayOfWeek", AsyncAPISchema.builder()
                    .type(com.asyncapi.v3.schema.Type.STRING)
                    .enumValue(List.of(
                            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"))
                    .build(),
            "Scale", AsyncAPISchema.builder()
                    .type(com.asyncapi.v3.schema.Type.STRING)
                    .enumValue(List.of("ABSOLUTE", "RELATIVE"))
                    .build(),
            "Quantity", AsyncAPISchema.builder()
                    .required(List.of("value", "unit"))
                    .type(com.asyncapi.v3.schema.Type.OBJECT)
                    .properties(new TreeMap<>(Map.of(
                            "scale", AsyncAPISchema.builder()
                                    .ref(GLOBAL_COMPONENTS_SCHEMAS_PREFIX + "Scale")
                                    .defaultValue("ABSOLUTE").build(),
                            "unit", AsyncAPISchema.builder()
                                    .description("Symbol of unit.")
                                    .type(com.asyncapi.v3.schema.Type.STRING).build(),
                            "value", AsyncAPISchema.builder()
                                    .type(com.asyncapi.v3.schema.Type.NUMBER).build())))
                    .build()));

    final IndexView index;
    final AsyncApiConfigResolver configResolver;
    final static Set<Type> VISITED = Collections.synchronizedSet(new HashSet<>());

    KafkaResolver kafkaResolver;
    final Map<String, Object> channels = new TreeMap<>();
    final Map<String, Object> operations = new TreeMap<>();

    public AsyncApiAnnotationScanner(IndexView aIndex, AsyncApiConfigResolver aConfigResolver) {
        index = aIndex;
        configResolver = aConfigResolver;
        resolveChannelsAndOperations();
    }

    AsyncAPI.AsyncAPIBuilder setData(AsyncAPI.AsyncAPIBuilder aBuilder) {
        Components components = Components.builder()
                .schemas(GLOBAL_COMPONENTS)
                .build();
        return aBuilder
                .components(components)
                .channels(channels)
                .operations(operations);
    }

    final void resolveChannelsAndOperations() {
        try {
            index
                    .getAnnotations("org.eclipse.microprofile.reactive.messaging.Channel")
                    .forEach(annotation -> resolveChannelAndOperation(annotation, ResolveType.CHANNEL));
            index
                    .getAnnotations("org.eclipse.microprofile.reactive.messaging.Incoming")
                    .forEach(annotation -> resolveChannelAndOperation(annotation, ResolveType.INCOMING));
            index
                    .getAnnotations("org.eclipse.microprofile.reactive.messaging.Outgoing")
                    .forEach(annotation -> resolveChannelAndOperation(annotation, ResolveType.OUTGOING));
        } finally {
            if (kafkaResolver != null) {
                kafkaResolver.close();
            }
        }
    }

    void resolveChannelAndOperation(AnnotationInstance aAnnotationInstance, ResolveType aResolveType) {
        ChannelData channelData = new ChannelData(aAnnotationInstance, aResolveType);
        String channelName = aAnnotationInstance.value().asString();
        if (channelData.operationId != null && configResolver.isSmallRyeKafkaTopic(channelData.isEmitter, channelName)) {
            String topic = configResolver.getTopic(channelData.isEmitter, channelName);
            if (kafkaResolver == null) {
                kafkaResolver = new KafkaResolver();
            }
            Channel.ChannelBuilder channelBuilder = Channel.builder()
                    .address(channelName)
                    .bindings(Map.of("kafka", kafkaResolver.getKafkaChannelBindings(topic)))
                    .messages(Map.of(channelData.operationId, getMessage(channelData.messageType)));
            io.quarkiverse.asyncapi.annotation.scanner.config.Channel channel = configResolver.getChannel(channelName);
            if (channel != null) {
                channel.description.ifPresent(channelBuilder::description);
            }
            channels.put(channelName, channelBuilder.build());
            Operation.OperationBuilder operationBuilder = Operation.builder()
                    .action(channelData.isEmitter ? OperationAction.SEND : OperationAction.RECEIVE)
                    .channel(new Reference("#/channels/" + channelName));
            addSchemaAnnotationData(aAnnotationInstance.target(), operationBuilder);
            configResolver.getGroupId(channelData.isEmitter, channelName)
                    .map(groupId -> AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.STRING)
                            .enumValue(List.of(groupId)).build())
                    .map(schema -> KafkaOperationBinding.builder().groupId(schema).build())
                    .ifPresent(kafkaOperationBinding -> operationBuilder.bindings(Map.of("kafka", kafkaOperationBinding)));
            operations.put(channelData.operationId, operationBuilder.build());
        }
        //TODO other than kafka...
    }

    enum ResolveType {
        CHANNEL,
        INCOMING,
        OUTGOING;
    }

    static class ChannelData {

        String operationId;
        boolean isEmitter;
        Type messageType;

        public ChannelData(AnnotationInstance aAnnotationInstance, ResolveType aType) {
            Type annotationTargetType;
            MethodInfo method;
            switch (aType) {
                case CHANNEL:
                    switch (aAnnotationInstance.target().kind()) {
                        case FIELD:
                            FieldInfo field = aAnnotationInstance.target().asField();
                            operationId = field.declaringClass().name() + "." + field.name();
                            annotationTargetType = field.type();
                            break;
                        default:
                            return;
                    }
                    isEmitter = annotationTargetType.name().toString().contains("Emitter");
                    if (annotationTargetType.kind().equals(PARAMETERIZED_TYPE)) {
                        Type genericMessageType = annotationTargetType.asParameterizedType().arguments().get(0);
                        switch (genericMessageType.kind()) {
                            case CLASS:
                                messageType = genericMessageType.asClassType();
                                break;
                            case PARAMETERIZED_TYPE:
                                messageType = genericMessageType.asParameterizedType();
                                break;
                            default:
                                throw new IllegalArgumentException("unhandled messageType " + genericMessageType.kind());
                        }
                        ;
                    } else {
                        throw new IllegalArgumentException(
                                "Channel-field has to be parameterized " + aAnnotationInstance.target());
                    }
                    break;
                case INCOMING:
                    isEmitter = false;
                    method = aAnnotationInstance.target().asMethod();
                    operationId = method.declaringClass().name() + "." + method.name();
                    messageType = resolveType(method.parameterType(0));
                    break;
                case OUTGOING:
                    isEmitter = true;
                    method = aAnnotationInstance.target().asMethod();
                    operationId = method.declaringClass().name() + "." + method.name();
                    messageType = resolveType(method.returnType());
                    break;
                default:
                    throw new AssertionError();
            }
            AnnotationInstance asyncApiSchema = aAnnotationInstance.target()
                    .annotation(io.quarkiverse.asyncapi.annotation.Schema.class);
            if (asyncApiSchema != null
                    && asyncApiSchema.value("implementation") != null
                    && asyncApiSchema.value("implementation").asClassArray().length > 0) {
                //don't use messageType but the annotated types
                Type[] types = asyncApiSchema.value("implementation").asClassArray();
                if (types.length == 1) {
                    messageType = types[0];
                } else {
                    ParameterizedType type = null;
                    for (int i = types.length - 1; i >= 0; i--) {
                        type = ParameterizedType.create(types[i].name(), type != null ? new Type[] { type } : null, null);
                    }
                    messageType = type;
                }
            }
        }

        final Type resolveType(Type aType) {
            Type bType = aType;
            if (DotName.createSimple(Uni.class).equals(aType.name())
                    || DotName.createSimple(Multi.class).equals(aType.name())) {
                bType = aType.asParameterizedType().arguments().get(0);
            }
            if (DotName.createSimple("org.eclipse.microprofile.reactive.messaging.Message").equals(bType.name())) {
                bType = bType.asParameterizedType().arguments().get(0);
            }
            Type type = bType.kind().equals(PARAMETERIZED_TYPE)
                    ? bType.asParameterizedType()
                    : bType;
            return type;
        }
    }

    Message getMessage(Type aMessageType) {
        return Message.builder()
                .name(aMessageType.name().toString()) //TODO expect to be overriden by annotation
                .contentType("application/json")
                .payload(getSchema(aMessageType, new HashMap<>()))
                .build();
    }

    static final Set<DotName> NO_REF_TYPES = Set.of(
            DotName.createSimple(Collection.class),
            DotName.createSimple(Set.class),
            DotName.createSimple(List.class),
            DotName.createSimple(Object.class),
            DotName.createSimple(Class.class),
            DotName.createSimple(String.class),
            DotName.createSimple(Boolean.class),
            DotName.createSimple(Byte.class),
            DotName.createSimple(Character.class),
            DotName.createSimple(Double.class),
            DotName.createSimple(Float.class),
            DotName.createSimple(Integer.class),
            DotName.createSimple(Long.class),
            DotName.createSimple(Short.class));

    /**
     * Don't add type to gloabal components and use it's ref everywhere if
     * <ul>
     * <li>it is a wrapper for a primitive or
     * <li>paramaterized type
     * </ul>
     *
     * @param aType
     * @return
     */
    boolean isNoRefType(Type aType) {
        return NO_REF_TYPES.contains(aType.name());
    }

    AsyncAPISchema getSchema(Type aType, Map<String, Type> typeVariableMap) {
        switch (aType.kind()) {
            case PRIMITIVE:
                return getPrimitiveSchema(aType);
            case ARRAY:
                return getArraySchema(aType, typeVariableMap);
            case PARAMETERIZED_TYPE:
                return getClassSchema(aType, typeVariableMap);
            case CLASS:
                if (isNoRefType(aType)) {
                    return getClassSchema(aType, typeVariableMap);
                }
                String typeKey = aType.name().local();
                AsyncAPISchema ref = AsyncAPISchema.builder().ref(GLOBAL_COMPONENTS_SCHEMAS_PREFIX + typeKey).build();
                if (VISITED.contains(aType) || GLOBAL_COMPONENTS.containsKey(typeKey)) {
                    return ref;
                }
                VISITED.add(aType); //prevent endless recursion
                GLOBAL_COMPONENTS.put(typeKey, getClassSchema(aType, typeVariableMap));
                return ref;
            default: //TODO other types
                return AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.OBJECT).build();
        }
    }

    AsyncAPISchema getClassSchema(Type aType, Map<String, Type> aTypeVariableMap) {
        ClassInfo classInfo = index.getClassByName(aType.name());
        if (aType.name().packagePrefix().startsWith("java.lang")) {
            return getJavaLangPackageSchema(aType);
        }
        if (classInfo != null && classInfo.isEnum()) {
            return AsyncAPISchema.builder()
                    .id(classInfo.toString())
                    .enumValue(classInfo.enumConstants().stream()
                            .map(FieldInfo::name)
                            .map(Object.class::cast)
                            .collect(Collectors.toList()))
                    .build();
        }
        if (aType.name().withoutPackagePrefix().endsWith("Map")
                && aType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)
                && aType.asParameterizedType().arguments().size() == 2) {
            Type valueType = aType.asParameterizedType().arguments().get(1);
            //            VISITED_TYPES.remove(valueType);//dirty: force re-scan
            //it's a Map, use type object and add it's value as additionalProperties,
            //see https://swagger.io/docs/specification/data-models/dictionaries/
            //and https://www.asyncapi.com/docs/reference/specification/v3.6.0#schemaObject -> Model with Map/Dictionary Properties
            return AsyncAPISchema.builder()
                    .type(com.asyncapi.v3.schema.Type.OBJECT)
                    .additionalProperties(getSchema(valueType, aTypeVariableMap))
                    .build();
        }
        AsyncAPISchema schema = AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.OBJECT).build();
        if (classInfo != null) {
            boolean hasOneOfSchema = addSchemaAnnotationData(classInfo, schema, aTypeVariableMap);
            if (aType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
                for (int i = 0; i < aType.asParameterizedType().arguments().size(); i++) {
                    aTypeVariableMap.put(classInfo.typeParameters().get(i).identifier(),
                            aType.asParameterizedType().arguments().get(i));
                }
            }
            //each oneOf declares it's own type&properties, leave the type&properties found in the annotated object
            if (hasOneOfSchema) {
                schema.setType(null);
            } else {
                Set<String> required = new TreeSet<>();
                //annotated fields
                Map<String, Object> properties = getAllFieldsRecursiv(classInfo).stream()
                        .map(f -> {
                            if (f.hasAnnotation(NotNull.class)) {
                                required.add(f.name());
                            }
                            return f;
                        })
                        .collect(Collectors.toMap(
                                FieldInfo::name,
                                f -> getFieldSchema(f, aTypeVariableMap), (a, b) -> b, TreeMap::new));
                //annotated getters
                getAllGetterWithSchemaAnnotationRecursiv(classInfo).stream()
                        .map(m -> {
                            if (m.hasAnnotation(NotNull.class)) {
                                required.add(getPropertyName(m));
                            }
                            return m;
                        })
                        .forEach(m -> properties.put(getPropertyName(m), getGetterSchema(m, aTypeVariableMap)));
                schema.setProperties(properties);
                if (!required.isEmpty()) {
                    schema.setRequired(new ArrayList<>(required));
                }
            }
        } else {
            //class is not in jandex...try to get the class by reflection
            LOGGER.fine("getClassSchema() Loading raw type " + aType);
            Class<?> rawClass = JandexReflection.loadRawType(aType);
            if (Collection.class.isAssignableFrom(rawClass)) {
                Type collectionType = aType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)
                        ? aTypeVariableMap.getOrDefault(aType.asParameterizedType().arguments().get(0).toString(),
                                aType.asParameterizedType().arguments().get(0))
                        : aType;

                schema.setType(com.asyncapi.v3.schema.Type.ARRAY);
                schema.setItems(getSchema(collectionType, aTypeVariableMap));
            }
            //TODO other non-indexed tyes from jre, e.g. maps...
        }
        return schema;
    }

    String getPropertyName(MethodInfo aMethodInfo) {
        return aMethodInfo.name().substring(3, 4).toLowerCase()
                + (aMethodInfo.name().length() > 3 ? aMethodInfo.name().substring(4) : "");
    }

    AsyncAPISchema getFieldSchema(FieldInfo aFieldInfo, Map<String, Type> aTypeVariableMap) {
        AsyncAPISchema schema = getDeclarationSchema(aFieldInfo, aFieldInfo.type(), aTypeVariableMap);
        addBeanValidationAnnotationData(aFieldInfo, schema);
        addDeprecatedAnnotationData(aFieldInfo, schema);
        return schema;
    }

    AsyncAPISchema getGetterSchema(MethodInfo aMethodInfo, Map<String, Type> aTypeVariableMap) {
        AsyncAPISchema schema = getDeclarationSchema(aMethodInfo, aMethodInfo.returnType(), aTypeVariableMap);
        addBeanValidationAnnotationData(aMethodInfo, schema);
        addDeprecatedAnnotationData(aMethodInfo, schema);
        return schema;
    }

    AsyncAPISchema getDeclarationSchema(Declaration aDeclaration, Type aType, Map<String, Type> aTypeVariableMap) {
        AsyncAPISchema schema = getSchema(aTypeVariableMap.getOrDefault(aType.toString(), aType), aTypeVariableMap);
        addSchemaAnnotationData(aDeclaration, schema, aTypeVariableMap);
        return schema;
    }

    void addBeanValidationAnnotationData(Declaration aDeclaration, AsyncAPISchema aSchema) {
        Optional.ofNullable(aDeclaration.annotation(Min.class))
                .map(AnnotationInstance::value)
                .map(AnnotationValue::asLong)
                .map(BigDecimal::valueOf)
                .ifPresent(aSchema::setMinimum);
        Optional.ofNullable(aDeclaration.annotation(Max.class))
                .map(AnnotationInstance::value)
                .map(AnnotationValue::asLong)
                .map(BigDecimal::valueOf)
                .ifPresent(aSchema::setMaximum);
    }

    void addDeprecatedAnnotationData(Declaration aDeclaration, AsyncAPISchema aSchema) {
        Optional.ofNullable(aDeclaration.annotation(Deprecated.class))
                .ifPresent(x -> aSchema.setDeprecated(true));
    }

    boolean addSchemaAnnotationData(AnnotationTarget aAnnotationTarget, AsyncAPISchema aSchema,
            Map<String, Type> aTypeVariableMap) {
        Optional.ofNullable(getSchemaAnnotationValue(aAnnotationTarget, "description"))
                .map(AnnotationValue::asString)
                .ifPresent(aSchema::setDescription);
        Optional.ofNullable(getSchemaAnnotationValue(aAnnotationTarget, "readOnly"))
                .map(AnnotationValue::asBoolean)
                .ifPresent(aSchema::setReadOnly);
        Optional.ofNullable(getSchemaAnnotationValue(aAnnotationTarget, "deprecated"))
                .map(AnnotationValue::asBoolean)
                .ifPresent(aSchema::setDeprecated);
        return Optional.ofNullable(getSchemaAnnotationValue(aAnnotationTarget, "oneOf"))
                .map(AnnotationValue::asClassArray)
                .map(ta -> Arrays.stream(ta).map(t -> getSchema(t, aTypeVariableMap)).collect(Collectors.toList()))
                .map(oneOf -> {
                    aSchema.setOneOf(oneOf);
                    return oneOf;
                })
                .isPresent();
    }

    void addSchemaAnnotationData(AnnotationTarget aAnnotationTarget, Operation.OperationBuilder aOperationBuilder) {
        Optional.ofNullable(getSchemaAnnotationValue(aAnnotationTarget, "description"))
                .map(AnnotationValue::asString)
                .ifPresent(aOperationBuilder::description);
    }

    AnnotationValue getSchemaAnnotationValue(AnnotationTarget aAnnotationTarget, String aAnnotationFieldName) {
        AnnotationInstance asyncApiSchemaAnnotation = aAnnotationTarget
                .declaredAnnotation(io.quarkiverse.asyncapi.annotation.Schema.class);
        AnnotationInstance openApiAnnotation = aAnnotationTarget.declaredAnnotation(OPEN_API_SCHEMA_ANNOTATION);
        AnnotationValue annotationValue;
        if (asyncApiSchemaAnnotation != null) {
            annotationValue = asyncApiSchemaAnnotation.value(aAnnotationFieldName);
        } else if (openApiAnnotation != null) {
            annotationValue = openApiAnnotation.value(aAnnotationFieldName);
        } else {
            annotationValue = null;
        }
        return annotationValue;
    }

    AsyncAPISchema getJavaLangPackageSchema(Type aType) {
        switch (aType.name().withoutPackagePrefix()) {
            case "Boolean":
                return AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.BOOLEAN).build();
            case "Character":
            case "String":
                return AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.STRING).build();
            case "Integer":
            case "Long":
            case "Short":
                return AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.INTEGER).build();
            case "Double":
            case "Float":
                return AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.NUMBER).build();
            default:
                return AsyncAPISchema.builder().type(com.asyncapi.v3.schema.Type.OBJECT).build();
        }
    }

    AsyncAPISchema getArraySchema(Type aType, Map<String, Type> aTypeVariableMap) {
        Type arrayType = aType.asArrayType().constituent().kind().equals(Type.Kind.TYPE_VARIABLE)
                ? aTypeVariableMap.get(aType.asArrayType().constituent().toString())
                : aType.asArrayType().constituent();
        return AsyncAPISchema.builder()
                .type(com.asyncapi.v3.schema.Type.ARRAY)
                .items(getSchema(arrayType, aTypeVariableMap))
                .build();
    }

    AsyncAPISchema getPrimitiveSchema(Type aType) {
        AsyncAPISchema.AsyncAPISchemaBuilder builder = AsyncAPISchema.builder();
        switch (aType.asPrimitiveType().primitive()) {
            case BOOLEAN:
                builder.type(com.asyncapi.v3.schema.Type.BOOLEAN);
                break;
            case CHAR:
                builder.type(com.asyncapi.v3.schema.Type.STRING);
                break;
            case INT:
            case LONG:
            case SHORT:
                builder.type(com.asyncapi.v3.schema.Type.INTEGER);
                break;
            case DOUBLE:
            case FLOAT:
                builder.type(com.asyncapi.v3.schema.Type.NUMBER);
            default:
                builder.type(com.asyncapi.v3.schema.Type.OBJECT);
        }
        return builder.build();
    }

    List<FieldInfo> getAllFieldsRecursiv(ClassInfo aClassInfo) {
        ArrayList<FieldInfo> fieldInfos = new ArrayList<>();
        if (aClassInfo.fields() != null) {
            aClassInfo.fields().stream()
                    .filter(f -> !Modifier.isStatic(f.flags()))
                    .filter(Predicate.not(FieldInfo::isEnumConstant))
                    .forEach(fieldInfos::add);
        }
        ClassInfo superClass = index.getClassByName(aClassInfo.superName());
        if (superClass != null) {
            fieldInfos.addAll(getAllFieldsRecursiv(superClass));
        }
        return fieldInfos;
    }

    List<MethodInfo> getAllGetterWithSchemaAnnotationRecursiv(ClassInfo aClassInfo) {
        ArrayList<MethodInfo> methodInfos = new ArrayList<>();
        if (aClassInfo.methods() != null) {
            aClassInfo.methods().stream()
                    .filter(m -> m.name().startsWith("get"))
                    .filter(m -> m.parametersCount() == 0)
                    .filter(m -> !m.returnType().kind().equals(Type.Kind.VOID))
                    .filter(m -> !Modifier.isStatic(m.flags()))
                    .filter(m -> m.hasAnnotation(io.quarkiverse.asyncapi.annotation.Schema.class)
                            || m.hasAnnotation(OPEN_API_SCHEMA_ANNOTATION))
                    .forEach(methodInfos::add);
        }
        ClassInfo superClass = index.getClassByName(aClassInfo.superName());
        if (superClass != null) {
            methodInfos.addAll(getAllGetterWithSchemaAnnotationRecursiv(superClass));
        }
        aClassInfo.interfaceNames().stream()
                .map(index::getClassByName)
                .filter(Objects::nonNull)
                .map(this::getAllGetterWithSchemaAnnotationRecursiv)
                .forEach(methodInfos::addAll);
        return methodInfos;
    }
}
