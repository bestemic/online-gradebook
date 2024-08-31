package com.pawlik.przemek.onlinegradebook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    private LocalDate birth;

    @Column(name = "password_changed", columnDefinition = "boolean default false")
    private Boolean passwordChanged = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<Subject> subjects;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Attendance> attendances;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Grade> grades;
}