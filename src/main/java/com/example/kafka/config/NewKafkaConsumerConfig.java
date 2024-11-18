package com.example.kafka.config;

import com.example.kafka.deserializer.KafkaConsumerJsonDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NewKafkaConsumerConfig {

    private final KafkaProperties properties;

    @Value("${new-kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${new-kafka.properties.sasl.jaas.config}")
    private String SASL_JAAS_CONFIG;
    @Value("${new-kafka.properties.security.protocol}")
    private String protocol;
    @Value("${new-kafka.properties.sasl.mechanism}")
    private String mechanism;


    public Map<String, Object> commonKafkaProps(ObjectProvider<SslBundles> sslBundles) {
        Map<String, Object> props = this.properties.buildConsumerProperties(sslBundles.getIfAvailable());

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(SaslConfigs.SASL_JAAS_CONFIG, SASL_JAAS_CONFIG);
        props.put(SaslConfigs.SASL_MECHANISM, mechanism);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaConsumerJsonDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, Object> newConsumerFactory(ObjectProvider<SslBundles> sslBundles) {
        return new DefaultKafkaConsumerFactory<>(commonKafkaProps(sslBundles));
    }

    @Bean
    public DefaultErrorHandler newErrorHandler() {
        // Если при обработке сообщении в kafkaListener падает ошибка: вызывается повторно 0 раза
        FixedBackOff backOff = new FixedBackOff(1000L, 0);

        ConsumerRecordRecoverer consumerRecordRecoverer = (record, ex) -> {
            log.error("cPjGid5 :: Error processing message in consumer of KafkaListener value: {}. Exception: {} ", record.value(), ex.getMessage(), ex);
        };

        return new DefaultErrorHandler(consumerRecordRecoverer, backOff);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> newKafkaListenerContainerFactory(ObjectProvider<SslBundles> sslBundles) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(newConsumerFactory(sslBundles));
        factory.setCommonErrorHandler(newErrorHandler());
        return factory;
    }


}
