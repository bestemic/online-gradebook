package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.ClassGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClassGroupRepository extends CrudRepository<ClassGroup, Long> {

    Optional<ClassGroup> findByName(String name);
}
