package io.quarkiverse.asyncapi.annotation.scanner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.asyncapi.v2._0_0.model.AsyncAPI;
import com.asyncapi.v2._0_0.model.channel.ChannelItem;
import com.asyncapi.v2.binding.channel.kafka.KafkaChannelBinding;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkiverse.asyncapi.annotation.scanner.config.AsyncApiRuntimeConfig;
import io.quarkus.runtime.annotations.Recorder;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * @since 09.02.2023
 */
@Recorder
public class AsyncApiRecorder {

    private static final Logger LOGGER = Logger.getLogger(AsyncApiRecorder.class.getName());

    public static final String ASYNC_API_JSON = "asyncApi.json";
    public static final String ASYNC_API_YAML = "asyncApi.yaml";
    public static final String ASYNC_API_PUML = "asyncApi.puml";
    public static final String ASYNC_API_SVG = "asyncApi.svg";

    public void store(AsyncAPI aAsyncAPI, AsyncApiRuntimeConfig aConfig) {
        try {
            AsyncAPI filteredAPI = filter(aAsyncAPI, aConfig);
            store(ObjectMapperFactory.yaml().writeValueAsString(filteredAPI), ASYNC_API_YAML);
            store(ObjectMapperFactory.json().writeValueAsString(filteredAPI), ASYNC_API_JSON);
            String plantUml = toPlantUml(filteredAPI);
            store(plantUml, ASYNC_API_PUML);
            store(plantUmlToGrafik(plantUml, FileFormat.SVG), ASYNC_API_SVG);
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
            Map<String, ChannelItem> filteredChannels = result.getChannels().entrySet().stream()
                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), filter.filterChannelItem(e.getKey(), e.getValue())))
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
                + "'needed on servers without graphviz...but then the 'left to right direction' does not work ;(\n"
                + "!pragma layout smetana\n"
                + "left to right direction\n"
                + "skinparam pathHoverColor Blue\n";
        return aAsyncAPI.getChannels().values().stream()
                .map(channelItem -> toPlantUmlArrow(server, channelItem))
                .distinct()//ignore multiple publishers/subscribers
                .collect(Collectors.joining("\n", start, "\n@enduml"));
    }

    String toPlantUmlArrow(String aServer, ChannelItem aChannelItem) {
        String arrow = aChannelItem.getPublish() != null
                ? " -[#red,bold]-> "
                : " <-[#green,bold]- ";
        KafkaChannelBinding kafkaChannelBinding = (KafkaChannelBinding) aChannelItem.getBindings().get("kafka");
        return aServer + arrow + "(" + kafkaChannelBinding.getTopic() + ")";
    }

    String plantUmlToGrafik(String aPlantUml, FileFormat aFormat) {
        try {
            SourceStringReader reader = new SourceStringReader(aPlantUml);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            reader.outputImage(os, new FileFormatOption(aFormat));
            os.close();
            return new String(os.toByteArray(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOGGER.throwing("io.quarkiverse.asyncapi.annotation.scanner.AsyncApiRecorder", "plantUmlToSvg", e);
            return "unable to generated SVG";
        }
    }

}
