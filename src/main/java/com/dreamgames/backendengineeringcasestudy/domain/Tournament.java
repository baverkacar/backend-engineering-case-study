package com.dreamgames.backendengineeringcasestudy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "tournaments")
@Getter
@Setter
public class Tournament implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tournamentId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "Active";
    }
}