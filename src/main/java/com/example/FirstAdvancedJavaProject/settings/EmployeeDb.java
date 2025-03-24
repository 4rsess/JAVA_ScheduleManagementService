package com.example.FirstAdvancedJavaProject.settings;

import com.example.FirstAdvancedJavaProject.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDb extends JpaRepository<Employee, String> {
}
