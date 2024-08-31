package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Attendance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends CrudRepository<Attendance, Long> {
}