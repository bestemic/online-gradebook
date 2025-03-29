package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.attendance.GetAttendanceDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.GetLessonAttendancesDto;
import com.pawlik.przemek.onlinegradebook.model.Attendance;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {UserMapper.class, LessonMapper.class})
public interface AttendanceMapper {

    GetLessonAttendancesDto attendanceToAttendancesLessonDto(Attendance attendance);

    GetAttendanceDto attendanceToAttendanceDto(Attendance attendance);
}
