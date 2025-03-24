package com.example.FirstAdvancedJavaProject.settings;

import com.example.FirstAdvancedJavaProject.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleDb extends JpaRepository<Schedule, String> {
    Optional<Schedule> findByScheduleName(String scheduleName);
}
