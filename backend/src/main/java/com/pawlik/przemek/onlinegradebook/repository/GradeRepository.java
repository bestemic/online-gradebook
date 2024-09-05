package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Grade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends CrudRepository<Grade, Long> {

    boolean existsBySubjectIdAndName(Long subjectId, String name);

    List<Grade> findBySubjectId(Long subjectId);

    List<Grade> findBySubjectIdAndStudentId(Long subjectId, Long studentId);
}