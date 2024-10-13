package com.example.kafka.models.DTO;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KafkaUserDTO extends AbstractKafkaDTO {
    private String name;

    public KafkaUserDTO(String id, String name) {
        super(id);
        this.name = name;
    }

}
