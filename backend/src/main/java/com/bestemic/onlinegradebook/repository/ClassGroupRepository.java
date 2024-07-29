package com.bestemic.onlinegradebook.repository;

import com.bestemic.onlinegradebook.model.ClassGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClassGroupRepository extends CrudRepository<ClassGroup, Long> {

    Optional<ClassGroup> findByName(String name);
}
