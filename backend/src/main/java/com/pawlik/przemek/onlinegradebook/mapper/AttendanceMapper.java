package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendanceDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendancesLessonDto;
import com.pawlik.przemek.onlinegradebook.model.Attendance;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class, LessonMapper.class})
public interface AttendanceMapper {

    AttendancesLessonDto attendanceToAttendancesLessonDto(Attendance attendance);

    AttendanceDto attendanceToAttendanceDto(Attendance attendance);
}