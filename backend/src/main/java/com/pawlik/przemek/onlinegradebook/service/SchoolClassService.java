package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.school_class.SchoolClassAddDto;
import com.pawlik.przemek.onlinegradebook.dto.school_class.SchoolClassDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.SchoolClassMapper;
import com.pawlik.przemek.onlinegradebook.model.SchoolClass;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final UserService userService;
    private final SchoolClassMapper schoolClassMapper;

    public SchoolClassService(SchoolClassRepository schoolClassRepository, UserService userService, SchoolClassMapper schoolClassMapper) {
        this.schoolClassRepository = schoolClassRepository;
        this.userService = userService;
        this.schoolClassMapper = schoolClassMapper;
    }

    @Transactional
    public SchoolClassDto createClass(SchoolClassAddDto schoolClassAddDto) {
        if (schoolClassRepository.findByName(schoolClassAddDto.getName()).isPresent()) {
            throw new CustomValidationException("name", "Class with provided name already exists");
        }

        List<User> students;
        try {
            students = userService.getAllStudentsWithIds(schoolClassAddDto.getStudentsIds());
        } catch (Exception e) {
            throw new CustomValidationException("studentsIds", e.getMessage());
        }

        SchoolClass newClass = new SchoolClass();
        newClass.setName(schoolClassAddDto.getName());
        newClass.setClassroom(schoolClassAddDto.getClassroom());
        newClass.setStudents(new HashSet<>(students));
        students.forEach(student -> student.setSchoolClass(newClass));
        schoolClassRepository.save(newClass);
        return schoolClassMapper.schoolClassToSchoolClassDto(newClass);
    }

    public List<SchoolClassDto> getAllClasses() {
        return ((List<SchoolClass>) schoolClassRepository.findAll()).stream()
                .map(schoolClassMapper::schoolClassToSchoolClassDto)
                .collect(Collectors.toList());
    }

    public SchoolClassDto getClassById(Long classId) {
        SchoolClass schoolClass = getClassObjectById(classId);
        return schoolClassMapper.schoolClassToSchoolClassDto(schoolClass);
    }

    public SchoolClass getClassObjectById(Long classId) {
        return schoolClassRepository.findById(classId).orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));
    }

    public void removeStudentFromClass(Long classId, Long userId) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId).orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));
        User student = userService.getUserObjectById(userId);

        if (!schoolClass.getStudents().contains(student)) {
            throw new IllegalArgumentException("User is not assigned to this class");
        }

        student.setSchoolClass(null);
        schoolClass.getStudents().remove(student);
        schoolClassRepository.save(schoolClass);
    }

    public void addStudentToClass(Long classId, Long userId) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId).orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));
        User student = userService.getUserObjectById(userId);

        student.setSchoolClass(schoolClass);
        schoolClass.getStudents().add(student);
        schoolClassRepository.save(schoolClass);
    }
}
