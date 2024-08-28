package com.pawlik.przemek.onlinegradebook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "classes")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String classroom;

    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<User> users;
}