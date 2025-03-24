package com.example.FirstAdvancedJavaProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "schedule_template")
@Getter
@Setter
@NoArgsConstructor
public class ScheduleTemplate {

    @Id
    @Column(name = "id", nullable = false, length = 32, updatable = false)
    private String id = UUID.randomUUID().toString().replace("-", "");

    @CreationTimestamp
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;

    @Column(name = "template_type", nullable = false, length = 2)
    private String templateType = generateRandomTemplateType();

    private static String generateRandomTemplateType() {
        Random random = new Random();
        char first = (char) ('A' + random.nextInt(26));
        char second = (char) ('A' + random.nextInt(26));
        return String.valueOf(first) + second;
    }
}
