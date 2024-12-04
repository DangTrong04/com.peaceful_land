package com.example.peaceful_land.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data @Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class RegisterRequest {
    private String email;

    private String password;

    private String name;

    @JsonProperty("birth_date")
    private LocalDateTime birthDate;

    private String phone;
}