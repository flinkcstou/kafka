package com.example.kafka.config;

import com.example.kafka.deserializer.KafkaConsumerJsonDeserializer;
import com.example.kafka.models.DTO.AbstractKafkaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
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
    private final KafkaProperties kafkaProperties;


    public Map<String, Object> consumerConfig() {
        Map<String, Object> stringObjectMap = kafkaProperties.buildConsumerProperties(null);
        Map<String, Object> props = new HashMap<>(stringObjectMap);


        // ErrorHandlingDeserializer используется если упала ошибка на уровне десериализации, отправить ошибку в DefaultErrorHandler
        // KafkaConsumerJsonDeserializer обертка над JsonSerializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaConsumerJsonDeserializer.class);
//        props.put(ObjectMapperConfiguration.OBJECT_MAPPER, objectMapper);
        return props;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        // Если при обработке сообщении в kafkaListener падает ошибка: вызывается повторно 2 раза, а дальше отправляется в DTL
        // Создание DeadLetterPublishingRecoverer с логированием через BiFunction
        var destinationResolver = (BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition>) (record, ex) -> {
            log.error("cPjGid5 :: Error processing message in consumer of KafkaListener value: {}. Exception: {} ", record.value(), ex.getMessage(), ex);

            // Возвращаем TopicPartition, чтобы указать, куда отправить сообщение в DLT
            TopicPartition topicPartition = new TopicPartition(record.topic() + ".DLT", record.partition());
            log.info("gOayeDtk :: Exception kafka message caught and sending to DLT: {} ", topicPartition);
            return topicPartition;
        };
        // Если при обработке сообщении в kafkaListener падает ошибка: вызывается повторно 2 раза, а дальше в DTL
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, destinationResolver);
        FixedBackOff backOff = new FixedBackOff(1000L, 2);
        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }


}
