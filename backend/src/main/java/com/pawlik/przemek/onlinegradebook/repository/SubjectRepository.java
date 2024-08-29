package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends CrudRepository<Subject, Long> {

    Optional<Subject> findByName(String name);

    boolean existsByNameAndSchoolClassIdAndTeacherId(String name, Long classId, Long teacherId);

    List<Subject> findBySchoolClassId(Long schoolClassId);
}
