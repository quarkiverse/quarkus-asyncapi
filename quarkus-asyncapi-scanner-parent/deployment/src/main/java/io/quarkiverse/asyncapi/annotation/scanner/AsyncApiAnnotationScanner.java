package io.quarkiverse.asyncapi.annotation.scanner;

import static org.jboss.jandex.Type.Kind.CLASS;
import static org.jboss.jandex.Type.Kind.PARAMETERIZED_TYPE;
import static org.jboss.jandex.Type.Kind.PRIMITIVE;

import java.lang.reflect.Modifier;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.JandexReflection;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;

import com.asyncapi.v2.model.ExternalDocumentation;
import com.asyncapi.v2.model.channel.ChannelItem;
import com.asyncapi.v2.model.channel.message.Message;
import com.asyncapi.v2.model.channel.operation.Operation;
import com.asyncapi.v2.model.component.Components;
import com.asyncapi.v2.model.schema.Schema;

import io.quarkiverse.asyncapi.annotation.scanner.config.Channel;
import io.quarkiverse.asyncapi.annotation.scanner.kafka.binding.KafkaChannelBinding;
import io.quarkiverse.asyncapi.annotation.scanner.kafka.binding.KafkaResolver;

/**
 * @since 09.02.2023
 * @author christiant
 */
public class AsyncApiAnnotationScanner {

    public static final String CONFIG_PREFIX = "io.quarkiverse.asyncapi";
    static final Logger LOGGER = Logger.getLogger(AsyncApiAnnotationScanner.class.getName());

    final IndexView index;
    final AsyncApiConfigResolver configResolver;

    static Set<Type> VISITED_TYPES = new HashSet<>();

    KafkaResolver kafkaResolver;

    public AsyncApiAnnotationScanner(IndexView aIndex, AsyncApiConfigResolver aConfigResolver) {
        index = aIndex;
        configResolver = aConfigResolver;
    }

    public Map<String, ChannelItem> getChannels() {
        TreeMap<String, ChannelItem> result;
        try {
            Stream<AbstractMap.SimpleEntry<String, ChannelItem>> annotatedChannels = index
                    .getAnnotations("org.eclipse.microprofile.reactive.messaging.Channel")
                    .stream()
                    .map(annotation -> getChannel(annotation, ResolveType.CHANNEL));
            Stream<AbstractMap.SimpleEntry<String, ChannelItem>> annotatedIncomings = index
                    .getAnnotations("org.eclipse.microprofile.reactive.messaging.Incoming")
                    .stream()
                    .map(annotation -> getChannel(annotation, ResolveType.INCOMING));
            Stream<AbstractMap.SimpleEntry<String, ChannelItem>> annotatedOutgoings = index
                    .getAnnotations("org.eclipse.microprofile.reactive.messaging.Outgoing")
                    .stream()
                    .map(annotation -> getChannel(annotation, ResolveType.OUTGOING));
            result = Stream
                    .concat(Stream.concat(annotatedChannels, annotatedIncomings), annotatedOutgoings)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, TreeMap::new));
        } finally {
            if (kafkaResolver != null) {
                kafkaResolver.close();
            }
        }
        return result;
    }

    AbstractMap.SimpleEntry<String, ChannelItem> getChannel(AnnotationInstance aAnnotationInstance, ResolveType aResolveType) {
        VISITED_TYPES.clear();
        ChannelData channelData = new ChannelData(aAnnotationInstance, aResolveType);
        String channelName = aAnnotationInstance.value().asString();
        if (channelData.operationId != null && configResolver.isSmallRyeKafkaTopic(channelData.isEmitter, channelName)) {
            String topic = configResolver.getTopic(channelData.isEmitter, channelName);
            if (kafkaResolver == null) {
                kafkaResolver = new KafkaResolver();
            }
            KafkaChannelBinding channelBinding = kafkaResolver.getKafkaChannelBindings(topic);
            ChannelItem.ChannelItemBuilder channelBuilder = ChannelItem.builder()
                    .bindings(Map.of("kafka", channelBinding));
            Operation operation = Operation.builder()
                    .message(getMessage(channelData.messageType))
                    .operationId(channelData.operationId)
                    .build();
            addSchemaAnnotationData(aAnnotationInstance.target(), operation);
            ChannelItem channelItem = channelData.isEmitter
                    ? channelBuilder.publish(operation).build()
                    : channelBuilder.subscribe(operation).build();
            Channel channel = configResolver.getChannel(channelName);
            if (channel != null) {
                channel.description.ifPresent(channelItem::setDescription);
            }
            return new AbstractMap.SimpleEntry<>(channelName, channelItem);
        }
        //TODO other than kafka...
        return null;
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
                        messageType = switch (genericMessageType.kind()) {
                            case CLASS ->
                                genericMessageType.asClassType();
                            case PARAMETERIZED_TYPE ->
                                genericMessageType.asParameterizedType();
                            default ->
                                throw new IllegalArgumentException("unhandled messageType " + genericMessageType.kind());
                        };
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
                    .annotation(io.quarkiverse.asyncapi.spi.Schema.class);
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

        final Type resolveType(Type aFirstParameterType) {
            return aFirstParameterType.kind().equals(PARAMETERIZED_TYPE)
                    ? aFirstParameterType.asParameterizedType()
                    : aFirstParameterType;
        }
    }

    public Components getGlobalComponents() {
        return Components.builder()
                .schemas(new TreeMap<>(Map.of(
                        "OffsetDateTime", Schema.builder()
                                .format("date-time")
                                .externalDocs(ExternalDocumentation.builder()
                                        .description(
                                                "A date-time with an offset from UTC/Greenwich in the ISO-8601 calendar system")
                                        .url("https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html")
                                        .build())
                                .type(com.asyncapi.v2.model.schema.Type.STRING)
                                .examples(List.of("2022-03-10T12:15:50-04:00"))
                                .build(),
                        "UUID", Schema.builder()
                                .format("uuid")
                                .pattern("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
                                .type(com.asyncapi.v2.model.schema.Type.STRING)
                                .build(),
                        "LocalTime", Schema.builder()
                                .format("local-time")
                                .type(com.asyncapi.v2.model.schema.Type.STRING)
                                .externalDocs(ExternalDocumentation.builder()
                                        .description("ISO-8601 representation of a extended local time")
                                        .url("https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_TIME")
                                        .build())
                                .examples(List.of("13:45.30.123456789"))
                                .build(),
                        "Duration", Schema.builder()
                                .format("duration")
                                .type(com.asyncapi.v2.model.schema.Type.STRING)
                                .externalDocs(ExternalDocumentation.builder()
                                        .description("ISO-8601 representation of a duration")
                                        .url("https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#toString--")
                                        .build())
                                .examples(List.of("P1D"))
                                .build(),
                        "DayOfWeek", Schema.builder()
                                .type(com.asyncapi.v2.model.schema.Type.STRING)
                                .enumValue(List.of(
                                        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"))
                                .build(),
                        "Scale", Schema.builder()
                                .type(com.asyncapi.v2.model.schema.Type.STRING)
                                .enumValue(List.of("ABSOLUTE", "RELATIVE"))
                                .build(),
                        "Quantity", Schema.builder()
                                .required(List.of("value", "unit"))
                                .type(com.asyncapi.v2.model.schema.Type.OBJECT)
                                .properties(new TreeMap<>(Map.of(
                                        "scale", Schema.builder()
                                                .ref("#/components/schemas/Scale")
                                                .defaultValue("ABSOLUTE").build(),
                                        "unit", Schema.builder()
                                                .description("Symbol of unit.")
                                                .type(com.asyncapi.v2.model.schema.Type.STRING).build(),
                                        "value", Schema.builder()
                                                .type(com.asyncapi.v2.model.schema.Type.NUMBER).build())))
                                .build())))
                .build();
    }

    boolean isGlobalDefinedSchema(Type aType) {
        Stream<DotName> classpathClasses = Stream
                .of(OffsetDateTime.class, UUID.class, LocalTime.class, Duration.class, DayOfWeek.class)
                .map(DotName::createSimple);
        Stream<DotName> nonClassPathClasses = Stream.of(DotName.createSimple("javax.measure.Quantity"));
        DotName typeName = aType.name();
        return Stream.concat(classpathClasses, nonClassPathClasses)
                .anyMatch(typeName::equals);
    }

    Message getMessage(Type aMessageType) {
        return Message.builder()
                .name(aMessageType.name().toString()) //TODO expect to be overriden by annotation
                .contentType("application/json")
                .payload(getSchema(aMessageType, new HashMap<>()))
                .build();
    }

    Schema getSchema(Type aType, Map<String, Type> typeVariableMap) {
        Schema.SchemaBuilder schemaBuilder = Schema.builder();
        switch (aType.kind()) {
            case PRIMITIVE ->
                getPrimitiveSchema(aType, schemaBuilder);
            case ARRAY ->
                getArraySchema(aType, typeVariableMap, schemaBuilder);
            case CLASS, PARAMETERIZED_TYPE ->
                getClassSchema(aType, schemaBuilder, typeVariableMap);
            default -> //TODO other types
                schemaBuilder.type(com.asyncapi.v2.model.schema.Type.OBJECT);
        }
        return schemaBuilder.build();
    }

    void getClassSchema(Type aType, Schema.SchemaBuilder aSchemaBuilder, Map<String, Type> aTypeVariableMap) {
        ClassInfo classInfo = index.getClassByName(aType.name());
        if (aType.name().packagePrefix().startsWith("java.lang")) {
            getJavaLangPackageSchema(aType, aSchemaBuilder);
        } else if (isGlobalDefinedSchema(aType)) {
            aSchemaBuilder.ref("#/components/schemas/" + aType.name().withoutPackagePrefix());
        } else if (classInfo != null && classInfo.isEnum()) {
            aSchemaBuilder.enumValue(classInfo.enumConstants().stream().map(FieldInfo::name).map(Object.class::cast).toList());
        } else if (VISITED_TYPES.contains(aType)) {
            LOGGER.fine("getClassSchema() Already visited type " + aType + ". Stopping recursion!");
        } else {
            VISITED_TYPES.add(aType);
            aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.OBJECT);
            if (classInfo != null) {
                addSchemaAnnotationData(classInfo, aSchemaBuilder);
                if (aType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)) {
                    for (int i = 0; i < aType.asParameterizedType().arguments().size(); i++) {
                        aTypeVariableMap.put(classInfo.typeParameters().get(i).identifier(),
                                aType.asParameterizedType().arguments().get(i));
                    }
                }
                //TODO consider getter-annotations, too
                List<FieldInfo> allFieldsRecursiv = getAllFieldsRecursiv(classInfo, aTypeVariableMap);
                Map<String, Schema> properties = allFieldsRecursiv.stream().collect(Collectors.toMap(
                        FieldInfo::name,
                        f -> getFieldSchema(f, aTypeVariableMap), (a, b) -> b, TreeMap::new));
                aSchemaBuilder.properties(properties);
            } else {
                //class is not in jandex...try to get the class by reflection
                LOGGER.fine("getClassSchema() Loading raw type " + aType);
                Class<?> rawClass = JandexReflection.loadRawType(aType);
                if (Collection.class.isAssignableFrom(rawClass)) {
                    Type collectionType = aType.kind().equals(Type.Kind.PARAMETERIZED_TYPE)
                            ? aTypeVariableMap.getOrDefault(aType.asParameterizedType().arguments().get(0).toString(),
                                    aType.asParameterizedType().arguments().get(0))
                            : aType;

                    aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.ARRAY)
                            .items(getSchema(collectionType, aTypeVariableMap));
                }
                //TODO other non-indexed tyes from jre, e.g. maps...
            }
        }
    }

    Schema getFieldSchema(FieldInfo aFieldInfo, Map<String, Type> aTypeVariableMap) {
        Schema schema = getSchema(aTypeVariableMap.getOrDefault(aFieldInfo.type().toString(), aFieldInfo.type()),
                aTypeVariableMap);
        addSchemaAnnotationData(aFieldInfo, schema);
        return schema;
    }

    void addSchemaAnnotationData(AnnotationTarget aAnnotationTarget, Schema aSchema) {
        addSchemaAnnotationStringData(aAnnotationTarget, "description", aSchema::setDescription);
    }

    void addSchemaAnnotationData(AnnotationTarget aAnnotationTarget, Schema.SchemaBuilder aSchemaBuilder) {
        addSchemaAnnotationStringData(aAnnotationTarget, "description", aSchemaBuilder::description);
    }

    void addSchemaAnnotationData(AnnotationTarget aAnnotationTarget, Operation aOperation) {
        addSchemaAnnotationStringData(aAnnotationTarget, "description", aOperation::setDescription);
    }

    void addSchemaAnnotationStringData(AnnotationTarget aAnnotationTarget, String aAnnotationFieldName,
            Consumer<String> aSetter) {
        AnnotationInstance asyncApiSchemaAnnotation = aAnnotationTarget
                .declaredAnnotation(io.quarkiverse.asyncapi.spi.Schema.class);
        AnnotationInstance openApiAnnotation = aAnnotationTarget.declaredAnnotation(
                DotName.createSimple("org.eclipse.microprofile.openapi.annotations.media.Schema"));
        AnnotationValue annotationValue;
        if (asyncApiSchemaAnnotation != null) {
            annotationValue = asyncApiSchemaAnnotation.value(aAnnotationFieldName);
        } else if (openApiAnnotation != null) {
            annotationValue = openApiAnnotation.value(aAnnotationFieldName);
        } else {
            annotationValue = null;
        }
        if (annotationValue != null && !annotationValue.asString().isEmpty()) {
            aSetter.accept(annotationValue.asString());
        }
    }

    void getJavaLangPackageSchema(Type aType, Schema.SchemaBuilder aSchemaBuilder) {
        switch (aType.name().withoutPackagePrefix()) {
            case "Boolean" ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.BOOLEAN);
            case "Character", "String" ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.STRING);
            case "Integer", "Long", "Short" ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.INTEGER);
            case "Double", "Float" ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.NUMBER);
            default ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.OBJECT);
        }
    }

    void getArraySchema(Type aType, Map<String, Type> aTypeVariableMap, Schema.SchemaBuilder aSchemaBuilder) {
        Type arrayType = aType.asArrayType().component().kind().equals(Type.Kind.TYPE_VARIABLE)
                ? aTypeVariableMap.get(aType.asArrayType().component().toString())
                : aType.asArrayType().component();
        aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.ARRAY).items(getSchema(arrayType, aTypeVariableMap));
    }

    void getPrimitiveSchema(Type aType, Schema.SchemaBuilder aSchemaBuilder) {
        switch (aType.asPrimitiveType().primitive()) {
            case BOOLEAN ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.BOOLEAN);
            case CHAR ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.STRING);
            case INT, LONG, SHORT ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.INTEGER);
            case DOUBLE, FLOAT ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.NUMBER);
            default ->
                aSchemaBuilder.type(com.asyncapi.v2.model.schema.Type.OBJECT);
        }
    }

    List<FieldInfo> getAllFieldsRecursiv(ClassInfo aClassInfo, Map<String, Type> aTypeVariableMap) {
        ArrayList<FieldInfo> fieldInfos = new ArrayList<>();
        if (aClassInfo.fields() != null) {
            aClassInfo.fields().stream()
                    .filter(f -> !Modifier.isStatic(f.flags()))
                    .filter(Predicate.not(FieldInfo::isEnumConstant))
                    .forEach(fieldInfos::add);
        }
        ClassInfo superClass = index.getClassByName(aClassInfo.superName());
        if (superClass != null) {
            fieldInfos.addAll(getAllFieldsRecursiv(superClass, aTypeVariableMap));
        }
        return fieldInfos;
    }
}
