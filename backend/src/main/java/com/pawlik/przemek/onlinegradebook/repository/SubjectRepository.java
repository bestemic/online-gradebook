package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubjectRepository extends CrudRepository<Subject, Long> {

    boolean existsByNameAndSchoolClassIdAndTeacherId(String name, Long classId, Long teacherId);

    List<Subject> findBySchoolClassId(Long schoolClassId);

    List<Subject> findByTeacherId(Long teacherId);

    List<Subject> findBySchoolClassIdAndTeacherId(Long classId, Long teacherId);
}
