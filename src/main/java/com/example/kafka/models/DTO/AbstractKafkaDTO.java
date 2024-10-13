package com.example.kafka.models.DTO;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "@type",
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = KafkaUserDTO.class, name = "kafkaUserDTO")
})
@Data

@AllArgsConstructor
@NoArgsConstructor
public class AbstractKafkaDTO {
    private String id;
}
