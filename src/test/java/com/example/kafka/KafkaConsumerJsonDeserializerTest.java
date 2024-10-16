package com.example.kafka;

import com.example.kafka.deserializer.KafkaConsumerJsonDeserializer;
import com.example.kafka.models.DTO.AbstractKafkaDTO;
import com.example.kafka.models.DTO.KafkaUserDTO;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class KafkaConsumerJsonDeserializerTest extends KafkaApplicationTests {
    private final KafkaConsumerJsonDeserializer<AbstractKafkaDTO> deserializer = new KafkaConsumerJsonDeserializer();

    @Test
    void deserializeKafkaUserDTO() {
        String json = """
                {
                  "@type": "kafkaUserDTO",
                  "id": "idValsue",
                  "at": "",
                  "name": "namesValue"
                }
                """;

        AbstractKafkaDTO message = deserializer.deserialize("my-topic", json.getBytes());
        assertTrue(message instanceof KafkaUserDTO);
        assertEquals("idValsue", ((KafkaUserDTO) message).getId());
    }

    @Test
    void deserializeUnknownType() {
        String json = "{\"type\":\"unknown\",\"field\":\"Value\"}";
        assertThrows(InvalidTypeIdException.class, () -> {
            deserializer.deserialize("my-topic", json.getBytes());
        });
    }
}
