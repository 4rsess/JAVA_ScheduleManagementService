package com.example.FirstAdvancedJavaProject.settings;

import com.example.FirstAdvancedJavaProject.entity.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleTemplateDb extends JpaRepository<ScheduleTemplate, String> {
}
