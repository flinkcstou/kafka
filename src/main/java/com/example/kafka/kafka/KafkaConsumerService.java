package com.example.kafka.kafka;

import com.example.kafka.models.DTO.AbstractKafkaDTO;
import com.example.kafka.models.DTO.KafkaDTO;
import com.example.kafka.models.DTO.KafkaSecondDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "${kafka.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory2")
    public void kafkaListener(KafkaDTO message) {
        System.out.println("messageTopic" + message);
    }

    @KafkaListener(topics = "${kafka.topic-second}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory2")
    public void kafkaListener(KafkaSecondDTO message) {
        System.out.println("messageTopicSecond" + message);
    }

    @KafkaListener(topics = "${kafka.topic-default}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory2")
    public void kafkaListener(AbstractKafkaDTO message) {
        System.out.println("messageTopicDefault" + message);
    }

    @KafkaListener(topics = "${new-kafka.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "newKafkaListenerContainerFactory")
    public void newKafkaListener(AbstractKafkaDTO message) {
        System.out.println("messageNewTopic" + message);
    }
}
