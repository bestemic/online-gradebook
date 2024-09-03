package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendanceAddDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendanceDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendancesAddDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendancesLessonDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.AttendanceMapper;
import com.pawlik.przemek.onlinegradebook.model.Attendance;
import com.pawlik.przemek.onlinegradebook.model.Lesson;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final UserService userService;
    private final LessonService lessonService;

    public AttendanceService(AttendanceRepository attendanceRepository, AttendanceMapper attendanceMapper, UserService userService, LessonService lessonService) {
        this.attendanceRepository = attendanceRepository;
        this.attendanceMapper = attendanceMapper;
        this.userService = userService;
        this.lessonService = lessonService;
    }

    public AttendancesLessonDto create(AttendancesAddDto attendancesAddDto) {
        Lesson lesson = lessonService.getLessonObjectById(attendancesAddDto.getLessonId());

        int numberOfStudentsInClass = lesson.getSubject().getSchoolClass().getStudents().size();
        if (attendancesAddDto.getAttendances().size() != numberOfStudentsInClass) {
            throw new CustomValidationException("attendances", "Number of attendances does not match the number of students in the class.");
        }

        List<Long> studentIds = attendancesAddDto.getAttendances().stream()
                .map(AttendanceAddDto::getStudentId)
                .collect(Collectors.toList());

        List<User> students;
        try {
            students = userService.getAllStudentsWithIds(studentIds);
        } catch (Exception e) {
            throw new CustomValidationException("attendances", e.getMessage());
        }

        for (User student : students) {
            if (!lesson.getSubject().getSchoolClass().getStudents().contains(student)) {
                throw new CustomValidationException("attendances", "Student with id " + student.getId() + " is not assigned to the class of this lesson.");
            }
        }

        for (AttendanceAddDto attendanceAddDto : attendancesAddDto.getAttendances()) {
            if (attendanceRepository.existsByStudentIdAndLessonId(attendanceAddDto.getStudentId(), attendancesAddDto.getLessonId())) {
                throw new IllegalStateException("Attendance record already exists for student with id " + attendanceAddDto.getStudentId() + " in lesson " + attendancesAddDto.getLessonId() + ".");
            }
        }

        List<Attendance> attendances = attendancesAddDto.getAttendances().stream()
                .map(studentAttendance -> {
                    User student = students.stream().filter(s -> s.getId().equals(studentAttendance.getStudentId())).findFirst().get();
                    Attendance attendance = new Attendance();
                    attendance.setLesson(lesson);
                    attendance.setStudent(student);
                    attendance.setPresent(studentAttendance.getPresent());
                    return attendance;
                })
                .collect(Collectors.toList());

        attendanceRepository.saveAll(attendances);

        AttendancesLessonDto result = attendanceMapper.attendanceToAttendancesLessonDto(attendances.get(0));
        result.setAttendances(attendances.stream()
                .map(attendanceMapper::attendanceToAttendanceDto)
                .collect(Collectors.toList()));
        return result;
    }

    public AttendancesLessonDto getAttendancesOnLesson(Long lessonId) {
        lessonService.getLessonObjectById(lessonId);
        List<Attendance> attendances = attendanceRepository.findAllByLessonId(lessonId);

        if (attendances.isEmpty()) {
            throw new NotFoundException(("Attendance not found for lesson with id " + lessonId + "."));
        }
        AttendancesLessonDto result = attendanceMapper.attendanceToAttendancesLessonDto(attendances.get(0));
        result.setAttendances(attendances.stream()
                .map(attendanceMapper::attendanceToAttendanceDto)
                .collect(Collectors.toList()));
        return result;
    }

    public AttendanceDto getStudentAttendanceOnLesson(Long lessonId, Long studentId) {
        lessonService.getLessonObjectById(lessonId);
        userService.getUserObjectById(studentId);

        Attendance attendance = attendanceRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(() -> new NotFoundException("Attendance not found for user with id " + studentId + " on lesson with id " + lessonId + "."));
        return attendanceMapper.attendanceToAttendanceDto(attendance);
    }
}