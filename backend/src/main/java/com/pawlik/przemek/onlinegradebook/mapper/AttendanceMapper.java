package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendanceDto;
import com.pawlik.przemek.onlinegradebook.model.Attendance;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class, LessonMapper.class})
public interface AttendanceMapper {

    AttendanceDto attendanceToAttendanceDto(Attendance attendance);
}