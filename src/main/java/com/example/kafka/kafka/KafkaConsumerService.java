package com.example.kafka.kafka;

import com.example.kafka.models.DTO.AbstractKafkaDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "${kafka-variable.topic-key}", groupId = "${spring.kafka.consumer.group-id}")
    public void kafkaListener(AbstractKafkaDTO message) {
        System.out.println("message" + message);
    }
}
