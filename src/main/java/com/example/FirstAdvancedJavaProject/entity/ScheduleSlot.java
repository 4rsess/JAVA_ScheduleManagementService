package com.example.FirstAdvancedJavaProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.UUID;

@Entity
@Table(name = "schedule_slots")
@Getter
@Setter
@NoArgsConstructor
public class ScheduleSlot {

    @Id
    @Column(name = "id", nullable = false, length = 32, updatable = false)
    private String id = UUID.randomUUID().toString().replace("-", "");

    @Column(name = "schedule_template_id", nullable = false, length = 32)
    private String scheduleTemplateId;

    @Column(name = "begin_time", nullable = false)
    private OffsetTime beginTime;

    @Column(name = "end_time", nullable = false)
    private OffsetTime endTime;
}
