package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.grade.GradeDto;
import com.pawlik.przemek.onlinegradebook.dto.grade.GradesDto;
import com.pawlik.przemek.onlinegradebook.model.Grade;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {SubjectMapper.class, UserMapper.class})
public interface GradeMapper {

    GradesDto gradeToGradesDto(Grade grade);

    GradeDto gradeToGradeDto(Grade grade);
}