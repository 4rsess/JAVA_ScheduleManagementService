package com.example.FirstAdvancedJavaProject.settings;

import com.example.FirstAdvancedJavaProject.entity.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleSlotDb extends JpaRepository<ScheduleSlot, String> {
}
