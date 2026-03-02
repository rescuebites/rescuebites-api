package com.rescuebites.api.commerce.data.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity(name = "business_hours")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "businessHoursId")
@ToString(exclude = "commerce")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"commerce_id", "day_of_week"}))
public class BusinessHours {

    @Id
    @Column(name = "business_hours_id")
    @Builder.Default
    private UUID businessHoursId = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "commerce_id", nullable = false)
    private Commerce commerce;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Builder.Default
    @Column(nullable = false)
    private boolean closed = false;

    // Horario de apertura (mañana o único turno)
    @Column(name = "open_time")
    private LocalTime openTime;

    // Horario de cierre (mañana o único turno)
    @Column(name = "close_time")
    private LocalTime closeTime;

    // Horario de apertura del turno tarde (nullable = turno continuo)
    @Column(name = "afternoon_open_time")
    private LocalTime afternoonOpenTime;

    // Horario de cierre del turno tarde (nullable = turno continuo)
    @Column(name = "afternoon_close_time")
    private LocalTime afternoonCloseTime;

    public boolean hasSplitSchedule() {
        return afternoonOpenTime != null && afternoonCloseTime != null;
    }

     // Verifica si una hora dada cae dentro del horario de atención
    public boolean isOpenAt(LocalTime time) {
        if (closed || openTime == null || closeTime == null) {
            return false;
        }

        boolean inMorningShift = !time.isBefore(openTime) && time.isBefore(closeTime);

        if (hasSplitSchedule()) {
            boolean inAfternoonShift = !time.isBefore(afternoonOpenTime) && time.isBefore(afternoonCloseTime);
            return inMorningShift || inAfternoonShift;
        }

        return inMorningShift;
    }

    public boolean willReopenLater(LocalTime currentTime) {
        if (closed || !hasSplitSchedule()) {
            return false;
        }

        // Está entre el cierre de la mañana y la apertura de la tarde
        return !currentTime.isBefore(closeTime) && currentTime.isBefore(afternoonOpenTime);
    }
}
