package com.example.FirstAdvancedJavaProject.controllers;

import com.example.FirstAdvancedJavaProject.entity.ScheduleSlot;
import com.example.FirstAdvancedJavaProject.models.ResponseModel;
import com.example.FirstAdvancedJavaProject.settings.ScheduleSlotDb;
import com.example.FirstAdvancedJavaProject.settings.ScheduleTemplateDb;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduleSlot")
@Tag(name = "ScheduleSlot")
@RequiredArgsConstructor
public class ScheduleSlotController {

    private final ScheduleSlotDb scheduleSlotDb;
    private final ScheduleTemplateDb scheduleTemplateDb;

    @PostMapping("/createScheduleSlot")
    @Operation(
            summary = "Создание Слота расписания",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleSlot.class))),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> createScheduleSlot (@RequestParam String schedule_template_id,
                                                 @RequestParam OffsetTime beginTime,
                                                 @RequestParam OffsetTime endTime){
        try {
            boolean exists = scheduleTemplateDb.existsById(schedule_template_id);
            if (!exists) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "id Шаблон расписания не найден"));
            }

            if (beginTime.isAfter(endTime) || beginTime.equals(endTime)) {
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseModel(400, "Время начала должно быть раньше времени окончания"));
            }
            ScheduleSlot scheduleSlot = new ScheduleSlot();

            scheduleSlot.setScheduleTemplateId(schedule_template_id);
            scheduleSlot.setBeginTime(beginTime);
            scheduleSlot.setEndTime(endTime);
            scheduleSlotDb.save(scheduleSlot);

            return ResponseEntity.ok(scheduleSlot);
        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }
    }


    @GetMapping("/getScheduleSlot/{id}")
    @Operation(
            summary = "Получение Слота расписания по id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleSlot.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> getScheduleSlotById (@RequestParam String id){
        try {
            Optional<ScheduleSlot> scheduleSlot = scheduleSlotDb.findById(id);

            if (scheduleSlot.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "Слот расписания не найден"));
            }

            return ResponseEntity.ok(scheduleSlot.get());
        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }
    }
}
