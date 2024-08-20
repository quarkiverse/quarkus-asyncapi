package io.quarkiverse.asyncapi.annotation.scanner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.asyncapi.bindings.kafka.v0._5_0.channel.KafkaChannelBinding;
import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.operation.Operation;
import com.asyncapi.v3._0_0.model.operation.OperationAction;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkiverse.asyncapi.annotation.scanner.config.AsyncApiRuntimeConfig;
import io.quarkus.runtime.annotations.Recorder;

/**
 * @since 09.02.2023
 */
@Recorder
public class AsyncApiRecorder {

    private static final Logger LOGGER = Logger.getLogger(AsyncApiRecorder.class.getName());

    public static final String ASYNC_API_JSON = "asyncApi.json";
    public static final String ASYNC_API_YAML = "asyncApi.yaml";
    public static final String ASYNC_API_PUML = "asyncApi.puml";

    public void store(AsyncAPI aAsyncAPI, AsyncApiRuntimeConfig aConfig) {
        try {
            AsyncAPI filteredAPI = filter(aAsyncAPI, aConfig);
            store(ObjectMapperFactory.yaml().writeValueAsString(filteredAPI), ASYNC_API_YAML);
            store(ObjectMapperFactory.json().writeValueAsString(filteredAPI), ASYNC_API_JSON);
            String plantUml = toPlantUml(filteredAPI);
            store(plantUml, ASYNC_API_PUML);
        } catch (JsonProcessingException e) {
            LOGGER.throwing("io.quarkiverse.asyncapi.annotation.scanner.AsyncApiRecorder", "scanAsyncAPIs", e);
        }
    }

    void store(String aContent, String aFileName) {
        try {
            Path path = Paths.get(System.getProperty("java.io.tmpdir"), aFileName);
            LOGGER.info("AsycnApiRecorder.store to " + path);
            Files.writeString(path, aContent);
        } catch (IOException e) {
            LOGGER.throwing("io.quarkiverse.asyncapi.annotation.scanner.AsyncApiRecorder", "store", e);
        }
    }

    AsyncAPI filter(AsyncAPI aAsyncAPI, AsyncApiRuntimeConfig aConfig) {
        AsyncAPI result = aAsyncAPI;
        AsyncApiFilter filter = getFilter(aConfig);
        if (filter != null) {
            result = filter.filterAsyncAPI(aAsyncAPI);
            Map<String, Object> filteredChannels = result.getChannels().entrySet().stream()
                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(),
                            filter.filterChannel(e.getKey(), (Channel) e.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            result.setChannels(filteredChannels);
        }
        return result;
    }

    AsyncApiFilter getFilter(AsyncApiRuntimeConfig aConfig) {
        Optional<String> filterClassName = aConfig.filter;
        if (filterClassName.isPresent()) {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                Class<?> c = loader.loadClass(filterClassName.get());
                return (AsyncApiFilter) c.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                LOGGER.log(Level.SEVERE, "Filter-class " + filterClassName + " not found", ex);
            }
        }
        return null;
    }

    String toPlantUml(AsyncAPI aAsyncAPI) {
        String server = "[" + aAsyncAPI.getInfo().getTitle() + "\\n" + aAsyncAPI.getInfo().getVersion() + "]";
        String start = "@startuml\n"
                + "left to right direction\n"
                + "skinparam pathHoverColor Blue\n";
        return aAsyncAPI.getOperations().values().stream()
                .map(o -> (Operation) o)
                .map(operation -> toPlantUmlArrow(server, aAsyncAPI, operation))
                .distinct()//ignore multiple publishers/subscribers
                .collect(Collectors.joining("\n", start, "\n@enduml"));
    }

    String toPlantUmlArrow(String aServer, AsyncAPI aAsyncAPI, Operation aOperation) {
        String ref = aOperation.getChannel().getRef();
        String channelName = ref.substring(ref.lastIndexOf('/') + 1);
        Channel channel = (Channel) aAsyncAPI.getChannels().get(channelName);
        String arrow = OperationAction.SEND.equals(aOperation.getAction())
                ? " -[#red,bold]-> "
                : " <-[#green,bold]- ";
        KafkaChannelBinding kafkaChannelBinding = (KafkaChannelBinding) channel.getBindings().get("kafka");
        return aServer + arrow + "(" + kafkaChannelBinding.getTopic() + ")";
    }
}
