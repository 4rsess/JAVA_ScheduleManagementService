package com.example.FirstAdvancedJavaProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
public class Schedule {

    @Id
    @Column(name = "id", length = 32, nullable = false, updatable = false)
    private String id = UUID.randomUUID().toString().replace("-", "");

    @Column(name = "schedule_name", length = 255)
    private String scheduleName;

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;

    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    private Instant updateTime;

}
