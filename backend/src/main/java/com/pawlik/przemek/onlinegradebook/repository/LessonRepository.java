package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Lesson;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {

    List<Lesson> findBySubjectId(Long subjectId);
}
