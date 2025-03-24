package com.example.FirstAdvancedJavaProject.entity;

import com.example.FirstAdvancedJavaProject.models.SlotType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "schedule_period")
@Getter
@Setter
@NoArgsConstructor
public class SchedulePeriod {

    @Id
    @Column(name = "id", nullable = false, length = 32, updatable = false)
    private String id = UUID.randomUUID().toString().replace("-", "");

    @Column(name = "slot_id", nullable = false, length = 32)
    private String slotId;

    @Column(name = "schedule_id", nullable = false, length = 32)
    private String scheduleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", length = 20)
    private SlotType slotType = SlotType.UNDEFINED;

    @Column(name = "administrator_id", nullable = false, length = 32)
    private String administratorId;

    @Column(name = "executor_id", length = 32)
    private String executorId;
}
