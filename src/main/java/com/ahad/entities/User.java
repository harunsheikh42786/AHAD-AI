package com.ahad.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    private String id;
    private String name;
    private String username;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String phone;
    @JsonIgnore
    private String gender;
    @JsonIgnore
    private String dob;
    @JsonIgnore
    private String profession;
    @JsonIgnore
    private String city;
    @JsonIgnore
    private String role;
    @JsonIgnore
    private String country;
    @JsonIgnore
    private String password;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Note> notes;

    @JsonIgnore
    private LocalDateTime registrationDate = LocalDateTime.now();
    @JsonIgnore
    private boolean isEnabled = true;
}
