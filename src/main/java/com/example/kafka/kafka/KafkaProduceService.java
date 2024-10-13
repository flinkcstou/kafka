package com.example.kafka.kafka;


import com.example.kafka.models.DTO.AbstractKafkaDTO;
import com.example.kafka.models.DTO.KafkaUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProduceService {

    private final KafkaTemplate<String, AbstractKafkaDTO> kafkaTemplate;


    public void sendMessage(String topic, KafkaUserDTO user) {
        try {
            kafkaTemplate.send(topic, user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Message sent to topic " + topic + ": " + user);
    }
}
