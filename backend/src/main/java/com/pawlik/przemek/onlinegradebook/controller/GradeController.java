package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.service.GradeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/grades")
@Tag(name = "Grades API", description = "Endpoints for managing grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }
}
