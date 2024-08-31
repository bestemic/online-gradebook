package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.repository.AttendanceRepository;
import org.springframework.stereotype.Service;


@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }
}
