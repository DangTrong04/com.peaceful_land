package com.example.peaceful_land.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.LocalDateTime;

@Data @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@MappedSuperclass
public class BaseEntity {

    @Column(name = "meta")
    private String meta;

    @Column(name = "hide")
    private Boolean hide;

    @Column(name = "order_index")
    private Integer order;

    @Column(name = "date_begin")
    private LocalDateTime dateBegin;

    @PrePersist
    protected void onCreate(){
        meta = "";
        hide = false;
        order = 0;
        dateBegin = LocalDateTime.now();
    }

}
