package com.example.kafka.models.DTO.test;

import org.apache.kafka.common.header.Headers;

public class Asd implements IAsd {


    @Override
    public void deserialize(String topic, Headers headers, byte[] data) {
        System.out.println("deserialize222");
    }


    @Override
    public void deserialize(String topic, byte[] data) {
        System.out.println("deserialize111");
    }
}
