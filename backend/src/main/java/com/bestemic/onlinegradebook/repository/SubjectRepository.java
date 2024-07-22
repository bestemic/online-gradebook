package com.bestemic.onlinegradebook.repository;

import com.bestemic.onlinegradebook.model.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubjectRepository extends CrudRepository<Subject, Long> {

    Optional<Subject> findByName(String name);
}
