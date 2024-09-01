package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonAddDto;
import com.pawlik.przemek.onlinegradebook.dto.lesson.LessonDto;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.LessonMapper;
import com.pawlik.przemek.onlinegradebook.model.Lesson;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final SubjectService subjectService;

    public LessonService(LessonRepository lessonRepository, LessonMapper lessonMapper, SubjectService subjectService) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
        this.subjectService = subjectService;
    }

    @Transactional
    public LessonDto createLesson(LessonAddDto lessonAddDto) {
        Subject subject = subjectService.getSubjectObjectById(lessonAddDto.getSubjectId());

        Lesson lesson = lessonMapper.lessonAddDtoToLesson(lessonAddDto);
        lesson.setSubject(subject);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.lessonToLessonDto(lesson);
    }

    public LessonDto getLessonById(Long lessonId) {
        Lesson lesson = getLessonObjectById(lessonId);
        return lessonMapper.lessonToLessonDto(lesson);
    }

    public Lesson getLessonObjectById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + lessonId));
    }

    public List<LessonDto> getAllLessons(Long subjectId) {
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
