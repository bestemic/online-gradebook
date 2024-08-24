package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectAddDto;
import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectDto;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SubjectMapper {
    Subject subjectAddDtoToSubject(SubjectAddDto subjectAddDto);

    SubjectDto subjectToSubjectDto(Subject subject);
}