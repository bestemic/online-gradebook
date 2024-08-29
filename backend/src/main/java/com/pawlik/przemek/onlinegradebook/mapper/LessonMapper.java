package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonAddDto;
import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonDto;
import com.pawlik.przemek.onlinegradebook.model.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson lessonAddDtoToLesson(LessonAddDto lessonAddDto);

    @Mapping(source = "subject.id", target = "subjectId")
    LessonDto lessonToLessonDto(Lesson lesson);
}