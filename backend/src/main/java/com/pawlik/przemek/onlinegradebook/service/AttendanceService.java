package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.attendance.AddAttendanceDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.AddLessonAttendancesDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.GetAttendanceDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.GetLessonAttendancesDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.AttendanceMapper;
import com.pawlik.przemek.onlinegradebook.model.Attendance;
import com.pawlik.przemek.onlinegradebook.model.Lesson;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.AttendanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final UserService userService;
    private final LessonService lessonService;

    public GetLessonAttendancesDto addLessonAttendances(AddLessonAttendancesDto addLessonAttendancesDto) {
        Lesson lesson = lessonService.getLessonObjectById(addLessonAttendancesDto.lessonId());

        int numberOfStudentsInClass = lesson.getSubject().getSchoolClass().getStudents().size();
        if (addLessonAttendancesDto.attendances().size() != numberOfStudentsInClass) {
            throw new CustomValidationException("attendances", "Number of attendances does not match the number of students in the class.");
        }

        List<Long> studentIds = addLessonAttendancesDto.attendances().stream()
                .map(AddAttendanceDto::studentId)
                .toList();

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

        for (AddAttendanceDto addAttendanceDto : addLessonAttendancesDto.attendances()) {
            if (attendanceRepository.existsByStudentIdAndLessonId(addAttendanceDto.studentId(), addLessonAttendancesDto.lessonId())) {
                throw new IllegalStateException("Attendance record already exists for student with id " + addAttendanceDto.studentId() + " in lesson " + addLessonAttendancesDto.lessonId() + ".");
            }
        }

        List<Attendance> attendances = addLessonAttendancesDto.attendances().stream()
                .map(studentAttendance -> {
                    User student = students.stream().filter(s -> s.getId().equals(studentAttendance.studentId())).findFirst().get();
                    Attendance attendance = new Attendance();
                    attendance.setLesson(lesson);
                    attendance.setStudent(student);
                    attendance.setPresent(studentAttendance.present());
                    return attendance;
                })
                .toList();

        attendanceRepository.saveAll(attendances);

        GetLessonAttendancesDto result = attendanceMapper.attendanceToAttendancesLessonDto(attendances.getFirst());
        result.attendances().addAll(attendances.stream()
                .map(attendanceMapper::attendanceToAttendanceDto)
                .toList());
        return result;
    }

    public GetLessonAttendancesDto getLessonAttendances(Long lessonId) {
        lessonService.getLessonObjectById(lessonId);
        List<Attendance> attendances = attendanceRepository.findAllByLessonId(lessonId);

        if (attendances.isEmpty()) {
            throw new NotFoundException(("Attendance not found for lesson with id " + lessonId + "."));
        }
        GetLessonAttendancesDto result = attendanceMapper.attendanceToAttendancesLessonDto(attendances.getFirst());
        result.attendances().addAll(attendances.stream()
                .map(attendanceMapper::attendanceToAttendanceDto)
                .toList());
        return result;
    }

    public GetAttendanceDto getAttendance(Long lessonId, Long studentId) {
        lessonService.getLessonObjectById(lessonId);
        userService.getUserObjectById(studentId);

        Attendance attendance = attendanceRepository.findByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(() -> new NotFoundException("Attendance not found for user with id " + studentId + " on lesson with id " + lessonId + "."));
        return attendanceMapper.attendanceToAttendanceDto(attendance);
    }
}
