package com.karacam.stock_service.utils;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.karacam.stock_service.constants.KafkaConstants;
import com.karacam.stock_service.gen.AppExecutionReportOuterClass;
import com.karacam.stock_service.gen.IncomingOrder;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.HashMap;
import java.util.Map;

public class ProtobufDeserializerFactory implements Deserializer<GeneratedMessage> {

    private final Map<String, Parser<? extends GeneratedMessage>> parserRegistry = new HashMap();

    public ProtobufDeserializerFactory() {
        this.parserRegistry.put(KafkaConstants.incoming_order_topic, IncomingOrder.OrderMessage.parser());
        this.parserRegistry.put(KafkaConstants.execution_report_topic, AppExecutionReportOuterClass.AppExecutionReport.parser());
    }


    @Override
    public GeneratedMessage deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        Parser<? extends GeneratedMessage> parser = this.parserRegistry.get(topic);

        if (parser == null) {
            throw new SerializationException("No parser was registered for this topic: " + topic);
        }

        try {
            return parser.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new SerializationException("Deserialization failed for topic: " + topic);
        }
    }
}
