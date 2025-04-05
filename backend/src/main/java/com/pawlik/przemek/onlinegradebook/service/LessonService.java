package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonAddDto;
import com.pawlik.przemek.onlinegradebook.dto.lesson.GetLessonDto;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.LessonMapper;
import com.pawlik.przemek.onlinegradebook.model.Lesson;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.repository.LessonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final SubjectService subjectService;

    @Transactional
    public GetLessonDto createLesson(LessonAddDto lessonAddDto) {
        Subject subject = subjectService.getSubjectObjectById(lessonAddDto.getSubjectId());

        Lesson lesson = lessonMapper.lessonAddDtoToLesson(lessonAddDto);
        lesson.setSubject(subject);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.lessonToLessonDto(lesson);
    }

    public GetLessonDto getLessonById(Long lessonId) {
        Lesson lesson = getLessonObjectById(lessonId);
        return lessonMapper.lessonToLessonDto(lesson);
    }

    public Lesson getLessonObjectById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + lessonId));
    }

    public List<GetLessonDto> getAllLessons(Long subjectId) {
        if (subjectId != null) {
            return lessonRepository.findBySubjectId(subjectId).stream()
                    .map(lessonMapper::lessonToLessonDto)
                    .collect(Collectors.toList());
        }

        return ((List<Lesson>) lessonRepository.findAll()).stream()
                .map(lessonMapper::lessonToLessonDto)
                .collect(Collectors.toList());
    }
}
