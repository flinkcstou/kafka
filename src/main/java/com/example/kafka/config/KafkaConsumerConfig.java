package com.example.kafka.config;

import com.example.kafka.deserializer.KafkaConsumerJsonDeserializer;
import com.example.kafka.models.DTO.AbstractKafkaDTO;
import com.example.kafka.models.DTO.KafkaDTO;
import com.example.kafka.models.DTO.KafkaSecondDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaTemplate<String, AbstractKafkaDTO> kafkaTemplate;
    private final KafkaProperties properties;


    private Map<String, Object> commonKafkaProps(ObjectProvider<SslBundles> sslBundles) {
        Map<String, Object> props = properties.buildConsumerProperties(sslBundles.getIfAvailable());

        // ErrorHandlingDeserializer используется если упала ошибка на уровне десериализации, отправить ошибку в DefaultErrorHandler
        // KafkaConsumerJsonDeserializer обертка над JsonSerializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaConsumerJsonDeserializer.class);
        Map<String, Class<?>> topic = new HashMap<>();
        topic.put("kafka-topic", KafkaDTO.class);
        topic.put("kafka-topic-second", KafkaSecondDTO.class);
        props.put("custom-topic", topic);
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaDTO.class);
//        props.put(ObjectMapperConfiguration.OBJECT_MAPPER, objectMapper);
        return props;
    }

    private DeadLetterPublishingRecoverer createDLTRecoverer() {
        var destinationResolver = (BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition>) (record, ex) -> {
            log.error("cPjGid5 :: Error processing message in consumer of KafkaListener value: {}. Exception: {} ", record.value(), ex.getMessage(), ex);

            // Возвращаем TopicPartition, чтобы указать, куда отправить сообщение в DLT
            TopicPartition topicPartition = new TopicPartition(record.topic() + ".DLT", record.partition());
            log.info("gOayeDtk :: Exception kafka message caught and sending to DLT: {} ", topicPartition);
            return topicPartition;
        };
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, destinationResolver);
        return recoverer;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(ObjectProvider<SslBundles> sslBundles) {
        return new DefaultKafkaConsumerFactory<>(commonKafkaProps(sslBundles));

    }


    @Bean
    public DefaultErrorHandler errorHandler() {
        // Если при обработке сообщении в kafkaListener падает ошибка: вызывается повторно 0 раза
        FixedBackOff backOff = new FixedBackOff(1000L, 0);

        ConsumerRecordRecoverer consumerRecordRecoverer = (record, ex) -> {
            log.error("cPjGid5 :: Error processing message in consumer of KafkaListener value: {}. Exception: {} ", record.value(), ex.getMessage(), ex);
        };

        return new DefaultErrorHandler(consumerRecordRecoverer, backOff);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory2(ObjectProvider<SslBundles> sslBundles) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(sslBundles));
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }


}
