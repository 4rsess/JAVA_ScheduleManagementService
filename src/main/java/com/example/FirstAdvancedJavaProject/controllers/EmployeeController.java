package com.example.FirstAdvancedJavaProject.controllers;

import com.example.FirstAdvancedJavaProject.entity.Employee;
import com.example.FirstAdvancedJavaProject.models.EmployeePosition;
import com.example.FirstAdvancedJavaProject.models.EmployeeStatus;
import com.example.FirstAdvancedJavaProject.models.ResponseModel;
import com.example.FirstAdvancedJavaProject.settings.EmployeeDb;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/employee")
@Tag(name = "Employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeDb employeeDb;

    @PostMapping("/createEmployee")
    @Operation(
            summary = "Создание Сотрудника",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Employee.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> createEmployee(@RequestParam String name,
                                            @RequestParam EmployeeStatus status,
                                            @RequestParam(required = false) Optional<EmployeePosition> position){
        try {
            if (name.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseModel(400, "Неверные указано имя"));
            }

            Employee employee = new Employee();
            employee.setEmployeeName(name);
            employee.setStatus(status);
            employee.setPosition(position.orElse(EmployeePosition.UNDEFINED));

            employeeDb.save(employee);

            return ResponseEntity.ok(employee);
        } catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }

    }


    @GetMapping("/getEmployee/{id}")
    @Operation(
            summary = "Получение Сотрудника по id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Employee.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content()),
                    @ApiResponse(responseCode = "500", description = "InternalServerError", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseModel.class)))
            }
    )
    public ResponseEntity<?> getEmployeeById(@RequestParam String id){
        try {

            Optional<Employee> employee = employeeDb.findById(id);

            if (employee.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel(404, "Сотрудник не найден"));
            }

            return ResponseEntity.ok(employee.get());

        }catch (Exception error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel(500, "Ошибка: " + error.getMessage()));
        }

    }

}
