package com.example.kafka.controller;


import com.example.kafka.kafka.KafkaProduceService;
import com.example.kafka.models.DTO.KafkaUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProduceService kafkaProduceService;

    @Value("${kafka-variable.topic-key}")
    private String topic;

    @GetMapping("/send")
    public String sendMessageToTopic(@RequestParam("message") String message) {

        kafkaProduceService.sendMessage(topic, new KafkaUserDTO("idValue", "nameValue"));
        return message;
    }

}
