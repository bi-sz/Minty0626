package com.Reboot.Minty.trade.repository;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Schedule;
import com.Reboot.Minty.trade.entity.ScheduleDay;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule findByUserId(User user);

    boolean existsByUserIdAndHopeArea(User user, String hopeArea);

    boolean existsByUserIdAndHopeDay(User user, ScheduleDay hopeDay);

    boolean existsByUserIdAndHopeDuration(User user, ScheduleDuration hopeDuration);

    boolean existsByUserIdAndIntroduction(User user, String introduction);
}
