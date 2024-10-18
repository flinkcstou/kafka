package com.example.kafka.models.DTO.test;

import org.apache.kafka.common.header.Headers;

public class AsdChild extends Asd {


    public static void main(String[] args) {
        AsdChild asdChild = new AsdChild();

        asdChild.deserialize(null, null, "dasdas");

    }

    @Override
    public void deserialize(String topic, Headers headers, byte[] data) {
        System.out.println("fsdfsdfds");
        super.deserialize(topic, headers, data);
    }

    @Override
    public void deserialize(String topic, byte[] data) {
        System.out.println("aaaaasfsfsdfds");
        super.deserialize(topic, data);
    }
}
