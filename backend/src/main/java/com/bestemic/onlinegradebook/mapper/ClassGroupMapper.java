package com.bestemic.onlinegradebook.mapper;

import com.bestemic.onlinegradebook.dto.class_group.ClassGroupDto;
import com.bestemic.onlinegradebook.dto.class_group.ClassGroupSubjectTeacherDto;
import com.bestemic.onlinegradebook.model.ClassGroup;
import com.bestemic.onlinegradebook.model.ClassGroupSubjectTeacher;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class, SubjectMapper.class})
public interface ClassGroupMapper {

    ClassGroupDto classGroupToClassGroupDto(ClassGroup classGroup);

    ClassGroupSubjectTeacherDto classGroupSubjectTeacherToClassGroupSubjectTeacherDto(ClassGroupSubjectTeacher classGroupSubjectTeacher);
}