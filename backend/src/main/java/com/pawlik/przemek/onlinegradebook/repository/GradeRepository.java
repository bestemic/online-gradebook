package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Grade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends CrudRepository<Grade, Long> {

    boolean existsBySubjectIdAndName(Long subjectId, String name);
}