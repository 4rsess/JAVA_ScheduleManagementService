package com.example.FirstAdvancedJavaProject.controllers;

import com.example.FirstAdvancedJavaProject.entity.ScheduleTemplate;
import com.example.FirstAdvancedJavaProject.models.ResponseModel;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/scheduleTemplate")
@Tag(name = "ScheduleTemplate")
@RequiredArgsConstructor
public class ScheduleTemplateController {

    private final ScheduleTemplateDb scheduleTemplateDb;

    @PostMapping("/createScheduleTemplate")
    @Operation(
            summary = "Создание Шаблона расписания",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleTemplate.class))),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> createScheduleTemplate(){

        try {
            ScheduleTemplate scheduleTemplate = new ScheduleTemplate();

            scheduleTemplateDb.save(scheduleTemplate);
            return ResponseEntity.ok(scheduleTemplate);

        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }


    }

    @GetMapping("/getScheduleTemplate/{id}")
    @Operation(
            summary = "Получение Шаблона расписания по id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleTemplate.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> getScheduleTemplateById (@RequestParam String id){
        try {
            Optional<ScheduleTemplate> scheduleTemplate = scheduleTemplateDb.findById(id);

            if (scheduleTemplate.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "Шаблон расписания не найден"));
            }

            return ResponseEntity.ok(scheduleTemplate.get());
        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }
    }
}
