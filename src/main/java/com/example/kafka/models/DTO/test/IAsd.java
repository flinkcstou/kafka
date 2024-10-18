package com.example.kafka.models.DTO.test;

import org.apache.kafka.common.header.Headers;

public interface IAsd {


    void deserialize(String topic, byte[] data);

    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic   topic associated with the data
     * @param headers headers associated with the record; may be empty.
     * @param data    serialized bytes; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    default void deserialize(String topic, Headers headers, byte[] data) {
        deserialize(topic, data);
    }

    /**
     * Deserialize a record value from a ByteBuffer into a value or object.
     *
     * @param topic   topic associated with the data
     * @param headers headers associated with the record; may be empty.
     * @param data    serialized ByteBuffer; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    default void deserialize(String topic, Headers headers, String data) {
        deserialize(topic, headers, new byte[]{});
    }
}
