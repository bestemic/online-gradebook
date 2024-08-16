package com.bestemic.onlinegradebook.repository;

import com.bestemic.onlinegradebook.model.ClassGroupSubjectTeacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassGroupSubjectTeacherRepository extends CrudRepository<ClassGroupSubjectTeacher, Long> {

    Optional<ClassGroupSubjectTeacher> findByClassGroupIdAndSubjectId(Long classGroupId, Long subjectId);

    List<ClassGroupSubjectTeacher> findAllByClassGroupId(Long classGroupId);
}