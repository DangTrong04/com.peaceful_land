package com.example.peaceful_land.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data @Getter @Setter
public class PostRequest {

    @JsonProperty("property_id")
    private Long propertyId;

    private String title;

    private String description;

    private String content;

}