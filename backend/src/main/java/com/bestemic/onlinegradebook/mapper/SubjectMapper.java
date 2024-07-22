package com.bestemic.onlinegradebook.mapper;

import com.bestemic.onlinegradebook.dto.subject.SubjectAddDto;
import com.bestemic.onlinegradebook.dto.subject.SubjectDto;
import com.bestemic.onlinegradebook.model.Subject;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SubjectMapper {
    Subject subjectAddDtoToSubject(SubjectAddDto subjectAddDto);

    SubjectDto subjectToSubjectDto(Subject subject);
}