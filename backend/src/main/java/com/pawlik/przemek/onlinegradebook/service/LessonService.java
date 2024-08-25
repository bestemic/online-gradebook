package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonAddDto;
import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonDto;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.LessonMapper;
import com.pawlik.przemek.onlinegradebook.model.ClassGroupSubjectTeacher;
import com.pawlik.przemek.onlinegradebook.model.Lesson;
import com.pawlik.przemek.onlinegradebook.repository.ClassGroupSubjectTeacherRepository;
import com.pawlik.przemek.onlinegradebook.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ClassGroupSubjectTeacherRepository classGroupSubjectTeacherRepository;
    private final LessonMapper lessonMapper;

    public LessonService(LessonRepository lessonRepository, ClassGroupSubjectTeacherRepository classGroupSubjectTeacherRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.classGroupSubjectTeacherRepository = classGroupSubjectTeacherRepository;
        this.lessonMapper = lessonMapper;
    }

    @Transactional
    public LessonDto createLesson(LessonAddDto lessonAddDto) {
        ClassGroupSubjectTeacher classGroupSubjectTeacher = classGroupSubjectTeacherRepository.findById(lessonAddDto.getClassGroupSubjectTeacherId())
                .orElseThrow(() -> new NotFoundException("Class-Subject connection not found with ID: " + lessonAddDto.getClassGroupSubjectTeacherId()));

        Lesson lesson = lessonMapper.lessonAddDtoToLesson(lessonAddDto);
        lesson.setClassGroupSubjectTeacher(classGroupSubjectTeacher);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.lessonToLessonDto(lesson);
    }

    public LessonDto getLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + lessonId));
        return lessonMapper.lessonToLessonDto(lesson);
    }

    public List<LessonDto> getAllLessons(Long classGroupSubjectTeacherId) {
        List<Lesson> lessons;

        if (classGroupSubjectTeacherId != null) {
            lessons = lessonRepository.findByClassGroupSubjectTeacherId(classGroupSubjectTeacherId);
        } else {
            lessons = (List<Lesson>) lessonRepository.findAll();
        }

        return lessons.stream()
                .map(lessonMapper::lessonToLessonDto)
                .collect(Collectors.toList());
    }
}
