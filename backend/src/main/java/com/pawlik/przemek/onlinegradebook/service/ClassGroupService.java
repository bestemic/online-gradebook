package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.class_group.ClassGroupAddDto;
import com.pawlik.przemek.onlinegradebook.dto.class_group.ClassGroupDto;
import com.pawlik.przemek.onlinegradebook.dto.class_group.ClassGroupSubjectTeacherAssignDto;
import com.pawlik.przemek.onlinegradebook.dto.class_group.ClassGroupSubjectTeacherDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.ClassGroupMapper;
import com.pawlik.przemek.onlinegradebook.model.ClassGroup;
import com.pawlik.przemek.onlinegradebook.model.ClassGroupSubjectTeacher;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.ClassGroupRepository;
import com.pawlik.przemek.onlinegradebook.repository.ClassGroupSubjectTeacherRepository;
import com.pawlik.przemek.onlinegradebook.repository.SubjectRepository;
import com.pawlik.przemek.onlinegradebook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassGroupService {

    private final ClassGroupRepository classGroupRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ClassGroupSubjectTeacherRepository classGroupSubjectTeacherRepository;
    private final ClassGroupMapper classGroupMapper;

    public ClassGroupService(ClassGroupRepository classGroupRepository, UserRepository userRepository, SubjectRepository subjectRepository, ClassGroupSubjectTeacherRepository classGroupSubjectTeacherRepository, ClassGroupMapper classGroupMapper) {
        this.classGroupRepository = classGroupRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.classGroupSubjectTeacherRepository = classGroupSubjectTeacherRepository;
        this.classGroupMapper = classGroupMapper;
    }

    @Transactional
    public ClassGroupDto createClass(ClassGroupAddDto classGroupAddDto) {
        if (classGroupRepository.findByName(classGroupAddDto.getName()).isPresent()) {
            throw new CustomValidationException("name", "Class with provided name already exists");
        }

        List<User> students = (List<User>) userRepository.findAllById(classGroupAddDto.getStudentsIds());

        if (students.size() != classGroupAddDto.getStudentsIds().size()) {
            throw new CustomValidationException("studentsIds", "Some students do not exist");
        }

        students.forEach(user -> {
            boolean isStudent = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"));
            if (!isStudent) {
                throw new CustomValidationException("studentsIds", "User with ID: " + user.getId() + " is not a student");
            }
        });

        ClassGroup newClass = new ClassGroup();
        newClass.setName(classGroupAddDto.getName());
        newClass.setClassroom(classGroupAddDto.getClassroom());
        newClass.setStudents(students);
        classGroupRepository.save(newClass);

        students.forEach(student -> {
            student.setClassGroup(newClass);
            userRepository.save(student);
        });

        return classGroupMapper.classGroupToClassGroupDto(newClass);
    }

    public List<ClassGroupDto> getAllClasses() {
        return ((List<ClassGroup>) classGroupRepository.findAll()).stream()
                .map(classGroupMapper::classGroupToClassGroupDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassGroupSubjectTeacherDto assignSubjectAndTeacherToClass(Long classId, ClassGroupSubjectTeacherAssignDto assignDto) {
        ClassGroup classGroup = classGroupRepository.findById(classId).orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));
        Subject subject = subjectRepository.findById(assignDto.getSubjectId()).orElseThrow(() -> new NotFoundException("Subject not found with ID: " + assignDto.getSubjectId()));
        User teacher = userRepository.findById(assignDto.getTeacherId()).orElseThrow(() -> new NotFoundException("Teacher not found with ID: " + assignDto.getTeacherId()));

        Optional<ClassGroupSubjectTeacher> existingAssignment = classGroupSubjectTeacherRepository.findByClassGroupIdAndSubjectId(classId, subject.getId());
        if (existingAssignment.isPresent()) {
            throw new IllegalStateException("Subject is already assigned to this class");
        }

        if (teacher.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_TEACHER"))) {
            throw new CustomValidationException("teacherId", "Only users with role Teacher can be assigned to a class");
        }

        if (!subject.getTeachers().contains(teacher)) {
            throw new CustomValidationException("teacherId", "The assigned teacher does not teach this subject");
        }

        ClassGroupSubjectTeacher classGroupSubjectTeacher = new ClassGroupSubjectTeacher();
        classGroupSubjectTeacher.setClassGroup(classGroup);
        classGroupSubjectTeacher.setSubject(subject);
        classGroupSubjectTeacher.setTeacher(teacher);

        classGroupSubjectTeacher = classGroupSubjectTeacherRepository.save(classGroupSubjectTeacher);
        return classGroupMapper.classGroupSubjectTeacherToClassGroupSubjectTeacherDto(classGroupSubjectTeacher);
    }

    public ClassGroupDto getClassById(Long classId) {
        ClassGroup classGroup = classGroupRepository.findById(classId).orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));
        return classGroupMapper.classGroupToClassGroupDto(classGroup);
    }

    public List<ClassGroupSubjectTeacherDto> getAllSubjectsAssignedToClass(Long classId) {
        classGroupRepository.findById(classId).orElseThrow(() -> new NotFoundException("Class not found with ID: " + classId));

        return classGroupSubjectTeacherRepository.findAllByClassGroupId(classId)
                .stream()
                .map(classGroupMapper::classGroupSubjectTeacherToClassGroupSubjectTeacherDto)
                .collect(Collectors.toList());
    }

    public List<ClassGroupSubjectTeacherDto> getAllSubjects() {
        return ((List<ClassGroupSubjectTeacher>) classGroupSubjectTeacherRepository.findAll()).stream()
                .map(classGroupMapper::classGroupSubjectTeacherToClassGroupSubjectTeacherDto)
                .collect(Collectors.toList());
    }
}
