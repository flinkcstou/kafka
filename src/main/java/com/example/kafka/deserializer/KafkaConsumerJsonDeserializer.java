package com.example.kafka.deserializer;

import com.example.kafka.models.DTO.AbstractKafkaDTO;
import lombok.SneakyThrows;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.utils.Utils;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.Supplier;


public class KafkaConsumerJsonDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(String topic, Headers headers, ByteBuffer data) {
        return deserialize(topic, headers, Utils.toNullableArray(data));
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return deserializeDecorator(
                () -> super.deserialize(topic, headers, data),
                () -> deserializeToAbstractKafkaDto(data));

    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return deserializeDecorator(
                () -> super.deserialize(topic, data),
                () -> deserializeToAbstractKafkaDto(data));
    }

    @SneakyThrows
    public T deserializeToAbstractKafkaDto(byte[] data) {
        return objectMapper.readValue(data, AbstractKafkaDTOType());
    }

    public T deserializeDecorator(Supplier<T> s, Supplier<T> s1) {
        try {
            return s.get();
        } catch (Exception e) {
            return s1.get();
        }
    }

    @Override
    public synchronized void configure(Map<String, ?> configs, boolean isKey) {
        super.configure(configs, isKey);
    }

    protected Class<T> AbstractKafkaDTOType() {
        return (Class<T>) AbstractKafkaDTO.class;
    }


}