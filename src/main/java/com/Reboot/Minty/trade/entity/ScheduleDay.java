package com.Reboot.Minty.trade.entity;

import com.Reboot.Minty.member.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Setter
@Entity
@Table(name = "ScheduleDay")
public class ScheduleDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int sunday;

    private int monday;

    private int tuesday;

    private int wednesday;

    private int thursday;

    private int friday;

    private int saturday;

    public DayOfWeek getHopeDay() {
        if (sunday == 1) return DayOfWeek.SUNDAY;
        if (monday == 1) return DayOfWeek.MONDAY;
        if (tuesday == 1) return DayOfWeek.TUESDAY;
        if (wednesday == 1) return DayOfWeek.WEDNESDAY;
        if (thursday == 1) return DayOfWeek.THURSDAY;
        if (friday == 1) return DayOfWeek.FRIDAY;
        if (saturday == 1) return DayOfWeek.SATURDAY;
        return null;
    }
}
