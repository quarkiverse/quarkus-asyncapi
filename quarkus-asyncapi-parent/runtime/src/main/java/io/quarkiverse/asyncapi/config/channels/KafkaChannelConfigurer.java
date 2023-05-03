package io.quarkiverse.asyncapi.config.channels;

import java.util.Map;

import com.asyncapi.v2._6_0.model.channel.operation.Operation;
import com.asyncapi.v2._6_0.model.server.Server;

public class KafkaChannelConfigurer extends AbstractChannelConfigurer {

    private static final String KAFKA_SERIALIZER = "value.serializer";
    private static final String KAFKA_DESERIALIZER = "value.deserializer";
    private static final String TOPIC = "topic";

    public KafkaChannelConfigurer() {
        super("kafka", "smallrye-kafka");
    }

    @Override
    protected void addOutgoingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result) {
        result.put(outgoingProperty(smallryeChannel, TOPIC), channelName);
        result.put(outgoingProperty(smallryeChannel, KAFKA_SERIALIZER),
                "org.apache.kafka.common.serialization.ByteArraySerializer");
    }

    @Override
    protected void addIncomingChannel(String smallryeChannel, String channelName, Operation operation, Server server,
            Map<String, String> result) {
        result.put(incomingProperty(smallryeChannel, TOPIC), channelName);
        result.put(incomingProperty(smallryeChannel, KAFKA_DESERIALIZER),
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    }

    @Override
    public void commonConfig(Server server, Map<String, String> result) {
        String serverUri = server.getUrl();
        result.compute("kafka.bootstrap.servers", (k, v) -> v == null ? serverUri : v + "," + serverUri);
    }
}
