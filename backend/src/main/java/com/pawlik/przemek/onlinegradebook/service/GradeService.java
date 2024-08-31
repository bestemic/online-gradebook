package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.repository.GradeRepository;
import org.springframework.stereotype.Service;


@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }
}
