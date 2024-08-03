package com.ahad.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    @Id
    private String id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "note", fetch = FetchType.LAZY)
    private List<Chat> chats;
    private LocalDateTime date;
    @ManyToOne
    private User user;

}
