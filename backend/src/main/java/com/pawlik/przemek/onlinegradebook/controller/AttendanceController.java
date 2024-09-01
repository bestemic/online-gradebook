package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendanceDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendancesAddDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.AttendancesLessonDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ErrorResponseDto;
import com.pawlik.przemek.onlinegradebook.dto.error.ValidationErrorDto;
import com.pawlik.przemek.onlinegradebook.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/attendances")
@Tag(name = "Attendances API", description = "Endpoints for managing attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Operation(summary = "Attendances creation", description = "Endpoint for attendances creation. Only users with role Teacher can access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Attendances created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttendancesLessonDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Lesson not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict - Attendance already assigned to student",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<AttendancesLessonDto> create(@Valid @RequestBody AttendancesAddDto attendancesAddDto) {
        AttendancesLessonDto attendancesLessonDto = attendanceService.create(attendancesAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(attendancesLessonDto);
    }

    @Operation(summary = "Get attendances on lesson", description = "Endpoint for retrieving the list of attendances on lesson.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendances retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AttendancesLessonDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Lesson not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("lesson/{lessonId}")
    public ResponseEntity<AttendancesLessonDto> getAttendancesOnLesson(@PathVariable Long lessonId) {
        AttendancesLessonDto attendancesLessonDto = attendanceService.getAttendancesOnLesson(lessonId);
        return ResponseEntity.ok().body(attendancesLessonDto);
    }

    @Operation(summary = "Get student attendance on lesson", description = "Endpoint for retrieving attendance of student on lesson.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance retrieved successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AttendanceDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not Found - Lesson, student or attendance not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("lesson/{lessonId}/student/{studentId}")
    public ResponseEntity<AttendanceDto> getStudentAttendanceOnLesson(@PathVariable Long lessonId, @PathVariable Long studentId) {
        AttendanceDto attendance = attendanceService.getStudentAttendanceOnLesson(lessonId, studentId);
        return ResponseEntity.ok().body(attendance);
    }
}
