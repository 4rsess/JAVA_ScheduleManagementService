package com.example.FirstAdvancedJavaProject.settings;

import com.example.FirstAdvancedJavaProject.entity.SchedulePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SchedulePeriodDb extends JpaRepository<SchedulePeriod, String> {
    List<SchedulePeriod> findByScheduleId(String scheduleId);
}
