package com.example.kafka.models.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KafkaUserDTO extends AbstractKafkaDTO {
    private String name;

    public KafkaUserDTO(String id, LocalDateTime localDateTime, String name) {
        super(id, localDateTime);
        this.name = name;
    }

}
