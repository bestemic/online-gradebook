package com.pawlik.przemek.onlinegradebook.controller;

import com.pawlik.przemek.onlinegradebook.dto.attendance.AddLessonAttendancesDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.GetAttendanceDto;
import com.pawlik.przemek.onlinegradebook.dto.attendance.GetLessonAttendancesDto;
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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendances")
@Tag(name = "Attendances API", description = "Endpoints for managing student attendances")
@AllArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(
            summary = "Create attendances for a lesson",
            description = "Registers attendance records for students in a specific lesson. Only users with the role TEACHER can access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Attendances successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetLessonAttendancesDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Lesson not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict - Attendance already recorded for one or more students",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<GetLessonAttendancesDto> addLessonAttendances(@Valid @RequestBody AddLessonAttendancesDto addLessonAttendancesDto) {
        var response = attendanceService.addLessonAttendances(addLessonAttendancesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Retrieve attendances for a lesson",
            description = "Fetches the attendance records for a specified lesson."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendances retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetLessonAttendancesDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Lesson not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<GetLessonAttendancesDto> getLessonAttendances(@PathVariable Long lessonId) {
        var response = attendanceService.getLessonAttendances(lessonId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Retrieve a student's attendance for a lesson",
            description = "Fetches the attendance record of a specific student in a given lesson."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance record retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAttendanceDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Lesson, student, or attendance record not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/lesson/{lessonId}/student/{studentId}")
    public ResponseEntity<GetAttendanceDto> getAttendance(@PathVariable Long lessonId, @PathVariable Long studentId) {
        var response = attendanceService.getAttendance(lessonId, studentId);
        return ResponseEntity.ok().body(response);
    }
}
