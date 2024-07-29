package com.bestemic.onlinegradebook.service;

import com.bestemic.onlinegradebook.dto.class_group.ClassGroupAddDto;
import com.bestemic.onlinegradebook.dto.class_group.ClassGroupDto;
import com.bestemic.onlinegradebook.exception.CustomValidationException;
import com.bestemic.onlinegradebook.mapper.ClassGroupMapper;
import com.bestemic.onlinegradebook.model.ClassGroup;
import com.bestemic.onlinegradebook.model.User;
import com.bestemic.onlinegradebook.repository.ClassGroupRepository;
import com.bestemic.onlinegradebook.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassGroupService {

    private final ClassGroupRepository classGroupRepository;
    private final UserRepository userRepository;
    private final ClassGroupMapper classGroupMapper;

    public ClassGroupService(ClassGroupRepository classGroupRepository, UserRepository userRepository, ClassGroupMapper classGroupMapper) {
        this.classGroupRepository = classGroupRepository;
        this.userRepository = userRepository;
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
}
