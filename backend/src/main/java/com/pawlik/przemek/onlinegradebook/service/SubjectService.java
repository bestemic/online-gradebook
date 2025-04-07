package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectAddDto;
import com.pawlik.przemek.onlinegradebook.dto.subject.SubjectDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.SubjectMapper;
import com.pawlik.przemek.onlinegradebook.model.SchoolClass;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.SubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final SchoolClassService schoolClassService;
    private final UserService userService;

    @Transactional
    public SubjectDto createSubject(SubjectAddDto subjectAddDto) {
        boolean subjectExists = subjectRepository.existsByNameAndSchoolClassIdAndTeacherId(subjectAddDto.getName(), subjectAddDto.getClassId(), subjectAddDto.getTeacherId());

        if (subjectExists) {
            throw new CustomValidationException("name", "This class already has a subject with this name taught by this teacher");
        }

        SchoolClass schoolClass = schoolClassService.getClassObjectById(subjectAddDto.getClassId());
        User teacher = userService.getUserObjectById(subjectAddDto.getTeacherId());

        if (teacher.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_TEACHER"))) {
            throw new CustomValidationException("teacherId", "Only users with role Teacher can teach subject");
        }

        Subject subject = new Subject();
        subject.setName(subjectAddDto.getName());
        subject.setSchoolClass(schoolClass);
        subject.setTeacher(teacher);
        subjectRepository.save(subject);
        return subjectMapper.subjectToSubjectDto(subject);
    }

    public List<SubjectDto> getAllSubjects(Long classId, Long teacherId) {
        if (classId != null && teacherId != null) {
            return subjectRepository.findBySchoolClassIdAndTeacherId(classId, teacherId)
                    .stream()
                    .map(subjectMapper::subjectToSubjectDto)
                    .toList();
        } else if (classId != null) {
            return subjectRepository.findBySchoolClassId(classId)
                    .stream()
                    .map(subjectMapper::subjectToSubjectDto)
                    .toList();
        } else if (teacherId != null) {
            return subjectRepository.findByTeacherId(teacherId)
                    .stream()
                    .map(subjectMapper::subjectToSubjectDto)
                    .toList();
        }

        return ((List<Subject>) subjectRepository.findAll()).stream()
                .map(subjectMapper::subjectToSubjectDto)
                .toList();
    }


    public SubjectDto getSubjectById(Long subjectId) {
        Subject subject = getSubjectObjectById(subjectId);
        return subjectMapper.subjectToSubjectDto(subject);
    }

    public Subject getSubjectObjectById(Long subjectId) {
        return subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found with ID: " + subjectId));
    }
}
