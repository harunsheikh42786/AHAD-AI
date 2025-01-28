package com.ahad.entities;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    private String id;
    private String query;
    @Column(length = 10000)
    private String response;
    private LocalDateTime date;
    @ManyToOne
    private Note note;

}
