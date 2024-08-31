package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.mapper.AttendanceMapper;
import com.pawlik.przemek.onlinegradebook.repository.AttendanceRepository;
import org.springframework.stereotype.Service;


@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;

    public AttendanceService(AttendanceRepository attendanceRepository, AttendanceMapper attendanceMapper) {
        this.attendanceRepository = attendanceRepository;
        this.attendanceMapper = attendanceMapper;
    }
}
