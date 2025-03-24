package com.example.FirstAdvancedJavaProject.controllers;

import com.example.FirstAdvancedJavaProject.entity.Schedule;
import com.example.FirstAdvancedJavaProject.entity.SchedulePeriod;
import com.example.FirstAdvancedJavaProject.entity.ScheduleSlot;
import com.example.FirstAdvancedJavaProject.models.ResponseModel;
import com.example.FirstAdvancedJavaProject.settings.ScheduleDb;
import com.example.FirstAdvancedJavaProject.settings.SchedulePeriodDb;
import com.example.FirstAdvancedJavaProject.settings.ScheduleSlotDb;
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
import java.util.*;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "Schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleDb scheduleDb;
    private final SchedulePeriodDb schedulePeriodDb;
    private final ScheduleSlotDb scheduleSlotDb;

    @PostMapping("/createSchedule")
    @Operation(
            summary = "Создание Расписания",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Schedule.class))),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> createSchedule(@RequestParam(required = false) String name){
        try{

            Schedule schedule = new Schedule();
            schedule.setScheduleName(name);

            scheduleDb.save(schedule);
            return ResponseEntity.ok(schedule);

        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }
    }


    @GetMapping("/getSchedule/{id}")
    @Operation(
            summary = "Получение Расписания по id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Schedule.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> getScheduleById(@RequestParam(required = false) String id, @RequestParam(required = false) String name){
        try {
            if (id == null && name == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel(400, "Необходимо указать id или scheduleName"));
            }

            Optional<Schedule> scheduleOptional = (id !=null)? scheduleDb.findById(id) : scheduleDb.findByScheduleName(name).stream().findFirst();


            if (scheduleOptional.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "Расписание не найдено"));
            }

            Schedule schedule = scheduleOptional.get();

            List<SchedulePeriod> periods = schedulePeriodDb.findByScheduleId(schedule.getId());

            List<SchedulePeriod> sortedPeriods = periods.stream()
                    .sorted(Comparator.comparing(period -> {

                        Optional<ScheduleSlot> slotOptional = scheduleSlotDb.findById(period.getSlotId());

                        return slotOptional.map(ScheduleSlot::getBeginTime).orElse(OffsetTime.MAX);
                    }))
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("schedule", schedule);
            response.put("periods", sortedPeriods);

            return ResponseEntity.ok(response);
        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }
    }
}
