package com.bestemic.onlinegradebook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "classes")
public class ClassGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String classroom;

    @OneToMany(mappedBy = "classGroup")
    private List<User> students = new ArrayList<>();

    @OneToMany(mappedBy = "classGroup")
    private Set<ClassGroupSubjectTeacher> classGroupSubjectTeachers = new HashSet<>();

}
