package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.SchoolClass;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SchoolClassRepository extends CrudRepository<SchoolClass, Long> {

    Optional<SchoolClass> findByName(String name);
}
