package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.service.AttendanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/attendances")
@Tag(name = "Attendances API", description = "Endpoints for managing attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
}
