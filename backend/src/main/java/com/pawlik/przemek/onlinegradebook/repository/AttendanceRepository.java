package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Attendance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends CrudRepository<Attendance, Long> {

    boolean existsByStudentIdAndLessonId(Long studentId, Long lessonId);

    List<Attendance> findAllByLessonId(Long lessonId);

    Optional<Attendance> findByStudentIdAndLessonId(Long studentId, Long lessonId);
}