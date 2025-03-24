package com.example.FirstAdvancedJavaProject.entity;

import com.example.FirstAdvancedJavaProject.models.EmployeePosition;
import com.example.FirstAdvancedJavaProject.models.EmployeeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @Column(name = "id", nullable = false, length = 32, updatable = false)
    private String id = UUID.randomUUID().toString().replace("-", "");

    @Column(name = "employee_name", nullable = false, length = 255)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EmployeeStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", length = 20)
    private EmployeePosition position = EmployeePosition.UNDEFINED;
}
