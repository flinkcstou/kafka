package com.example.kafka.scheduler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KafkaScheduler {


    @Scheduled(fixedRate = 30_000)
    public void kafkaScheduler(){

    }
}
