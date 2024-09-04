package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.grade.GradeAddDto;
import com.pawlik.przemek.onlinegradebook.dto.grade.GradesAddDto;
import com.pawlik.przemek.onlinegradebook.dto.grade.GradesDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.mapper.GradeMapper;
import com.pawlik.przemek.onlinegradebook.model.Grade;
import com.pawlik.przemek.onlinegradebook.model.Subject;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GradeMapper gradeMapper;
    private final SubjectService subjectService;
    private final UserService userService;

    public GradeService(GradeRepository gradeRepository, GradeMapper gradeMapper, SubjectService subjectService, UserService userService) {
        this.gradeRepository = gradeRepository;
        this.gradeMapper = gradeMapper;
        this.subjectService = subjectService;
        this.userService = userService;
    }

    public GradesDto create(GradesAddDto gradesAddDto) {
        Subject subject = subjectService.getSubjectObjectById(gradesAddDto.getSubjectId());

        int numberOfStudentsInClass = subject.getSchoolClass().getStudents().size();
        if (gradesAddDto.getGrades().size() > numberOfStudentsInClass) {
            throw new CustomValidationException("grades", "Number of grades is grater than number of students in the class.");
        }

        List<Long> studentIds = gradesAddDto.getGrades().stream()
                .map(GradeAddDto::getStudentId)
                .toList();

        Set<User> studentsInClass = subject.getSchoolClass().getStudents();

        List<User> missingStudents = studentsInClass.stream()
                .filter(student -> !studentIds.contains(student.getId()))
                .toList();

        List<User> students;
        try {
            students = userService.getAllStudentsWithIds(studentIds);
        } catch (Exception e) {
            throw new CustomValidationException("grades", e.getMessage());
        }

        for (User student : students) {
            if (!subject.getSchoolClass().getStudents().contains(student)) {
                throw new CustomValidationException("grades", "Student with id " + student.getId() + " is not assigned to the class of this subject.");
            }
        }

        if (gradeRepository.existsBySubjectIdAndName(subject.getId(), gradesAddDto.getName())) {
            throw new IllegalStateException("Grade with name " + gradesAddDto.getName() + " already exists for subject with id " + subject.getId() + ".");
        }

        LocalDate assignedDate = LocalDate.now();

        List<Grade> grades = new ArrayList<>(gradesAddDto.getGrades().stream()
                .map(studentGrade -> {
                    User student = students.stream().filter(s -> s.getId().equals(studentGrade.getStudentId())).findFirst().get();
                    Grade grade = new Grade();
                    grade.setSubject(subject);
                    grade.setName(gradesAddDto.getName());
                    grade.setAssignedDate(assignedDate);
                    grade.setStudent(student);
                    grade.setGrade(studentGrade.getGrade());
                    return grade;
                })
                .toList());

        List<Grade> nullGrades = missingStudents.stream()
                .map(student -> {
                    Grade grade = new Grade();
                    grade.setSubject(subject);
                    grade.setName(gradesAddDto.getName());
                    grade.setAssignedDate(assignedDate);
                    grade.setStudent(student);
                    grade.setGrade(null);
                    return grade;
                })
                .toList();

        grades.addAll(nullGrades);
        gradeRepository.saveAll(grades);

        GradesDto result = gradeMapper.gradeToGradesDto(grades.get(0));
        result.setGrades(grades.stream()
                .map(gradeMapper::gradeToGradeDto)
                .collect(Collectors.toList()));
        return result;
    }
}
