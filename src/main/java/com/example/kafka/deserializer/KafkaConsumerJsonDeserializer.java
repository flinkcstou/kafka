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

    private Map<String, Class<?>> customTopic;

    @Override
    public T deserialize(String topic, Headers headers, ByteBuffer data) {
        return deserialize(topic, headers, Utils.toNullableArray(data));
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return deserializeDecorator(
                () -> super.deserialize(topic, headers, data),
                () -> deserializeToAbstractKafkaDto(data, topic));

    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return deserializeDecorator(
                () -> super.deserialize(topic, data),
                () -> deserializeToAbstractKafkaDto(data, topic));
    }

    @SneakyThrows
    public T deserializeToAbstractKafkaDto(byte[] data, String topic) {
        try {
            return objectMapper.readValue(data, AbstractKafkaDTOType());
        } catch (Exception e) {
            return (T) objectMapper.readValue(data, this.customTopic.get(topic));
        }

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

        this.customTopic = (Map<String, Class<?>>) configs.get("custom-topic");

        super.configure(configs, isKey);
    }

    protected Class<T> AbstractKafkaDTOType() {
        return (Class<T>) AbstractKafkaDTO.class;
    }


}