package com.example.FirstAdvancedJavaProject.controllers;

import com.example.FirstAdvancedJavaProject.entity.Schedule;
import com.example.FirstAdvancedJavaProject.entity.SchedulePeriod;
import com.example.FirstAdvancedJavaProject.entity.ScheduleSlot;
import com.example.FirstAdvancedJavaProject.models.*;
import com.example.FirstAdvancedJavaProject.settings.EmployeeDb;
import com.example.FirstAdvancedJavaProject.settings.ScheduleDb;
import com.example.FirstAdvancedJavaProject.settings.SchedulePeriodDb;
import com.example.FirstAdvancedJavaProject.settings.ScheduleSlotDb;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.time.OffsetTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedulePeriod")
@Tag(name = "SchedulePeriod")
@RequiredArgsConstructor
public class SchedulePeriodController {

    private final SchedulePeriodDb schedulePeriodDb;
    private final ScheduleSlotDb scheduleSlotDb;
    private final ScheduleDb scheduleDb;
    private final EmployeeDb employeeDb;

    @PostMapping("/createSchedulePeriod")
    @Operation(
            summary = "Создание Периода расписания",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SchedulePeriod.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> createSchedulePeriod(@RequestParam String slot_id,
                                                  @RequestParam String schedule_id,
                                                  @RequestParam(required = false) Optional<SlotType> slotType,
                                                  @RequestHeader("x-current-user") String administrator_id,
                                                  @RequestParam String executor_id) {
        try {
            if (!scheduleSlotDb.existsById(slot_id)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "id Слота расписания не найден"));
            }

            if (!scheduleDb.existsById(schedule_id)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "id Расписания не найден"));
            }

            if (!employeeDb.existsById(administrator_id)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "id Владельца не найден"));
            }

            if (!employeeDb.existsById(executor_id)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "id Исполнителя не найден"));
            }

            if (executor_id != null && executor_id.equals(administrator_id)) {
                executor_id = null;
            }

            Optional<ScheduleSlot> slotOptional = scheduleSlotDb.findById(slot_id);
            if (slotOptional.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "Слот не найден"));
            }
            ScheduleSlot newSlot = slotOptional.get();
            OffsetTime newBeginTime = newSlot.getBeginTime();
            OffsetTime newEndTime = newSlot.getEndTime();

            List<SchedulePeriod> existingPeriods = schedulePeriodDb.findAll();

            boolean hasOverlap = existingPeriods.stream().anyMatch(period -> {
                Optional<ScheduleSlot> existingSlotOptional = scheduleSlotDb.findById(period.getSlotId());

                if (existingSlotOptional.isEmpty()){
                    return false;
                }

                ScheduleSlot existingSlot = existingSlotOptional.get();
                OffsetTime existingBeginTime = existingSlot.getBeginTime();
                OffsetTime existingEndTime = existingSlot.getEndTime();

                return !(newEndTime.isBefore(existingBeginTime) || newBeginTime.isAfter(existingEndTime));
            });

            if (hasOverlap) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel(400, "Слот пересекается с уже существующим периодом"));
            }


            Optional<Schedule> scheduleOptional = scheduleDb.findById(schedule_id);
            Schedule schedule = scheduleOptional.get();
            schedule.setUpdateTime(Instant.now());
            scheduleDb.save(schedule);

            SchedulePeriod schedulePeriod = new SchedulePeriod();
            schedulePeriod.setSlotId(slot_id);
            schedulePeriod.setScheduleId(schedule_id);
            schedulePeriod.setSlotType(slotType.orElse(SlotType.UNDEFINED));
            schedulePeriod.setAdministratorId(administrator_id);
            schedulePeriod.setExecutorId(executor_id);

            schedulePeriodDb.save(schedulePeriod);
            return ResponseEntity.ok(schedulePeriod);

        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }
    }

    @GetMapping("/getSchedulePeriod")
    @Operation(
            summary = "Получение Периода расписания",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SchedulePeriod.class))),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> getSchedulePeriod(@RequestParam(required = false) String id,
                                               @RequestParam(required = false) String slotId,
                                               @RequestParam(required = false) String scheduleId,
                                               @RequestParam(required = false) SlotType slotType,
                                               @RequestParam(required = false) String administratorId,
                                               @RequestParam(required = false) String executorId,
                                               @RequestParam(required = false) SchedulePeriodField field,
                                               @RequestParam(required = false) SortDirection direction,
                                               @RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size){
        try {

            Sort.Direction sortDirection = (direction == SortDirection.DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(sortDirection, field != null ? field.name() : "id");
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SchedulePeriod> result = schedulePeriodDb.findAll(pageable);

            List<SchedulePeriod> list = result.getContent().stream()
                    .filter(filter -> (id == null || filter.getId().equals(id)))
                    .filter(filter -> (slotId == null || filter.getSlotId().equals(slotId)))
                    .filter(filter -> (scheduleId == null || filter.getScheduleId().equals(scheduleId)))
                    .filter(filter -> (slotType == null || filter.getSlotType() == slotType))
                    .filter(filter -> (administratorId == null || filter.getAdministratorId().equals(administratorId)))
                    .filter(filter -> (executorId == null || Objects.equals(filter.getExecutorId(), executorId)))
                    .toList();

            return ResponseEntity.ok(list);

        }catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }

    }
}