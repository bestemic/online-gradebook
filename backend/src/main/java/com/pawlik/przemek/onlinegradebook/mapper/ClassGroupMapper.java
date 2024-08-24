package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.class_group.ClassGroupDto;
import com.pawlik.przemek.onlinegradebook.dto.class_group.ClassGroupSubjectTeacherDto;
import com.pawlik.przemek.onlinegradebook.model.ClassGroup;
import com.pawlik.przemek.onlinegradebook.model.ClassGroupSubjectTeacher;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class, SubjectMapper.class})
public interface ClassGroupMapper {

    ClassGroupDto classGroupToClassGroupDto(ClassGroup classGroup);

    ClassGroupSubjectTeacherDto classGroupSubjectTeacherToClassGroupSubjectTeacherDto(ClassGroupSubjectTeacher classGroupSubjectTeacher);
}