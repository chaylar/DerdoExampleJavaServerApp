package org.derdoapp.DataManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class DateTimeManager {

    private static final String ZONE_ID = "Europe/Istanbul";

    public static LocalDateTime getDateTodayMidnight() {
        //NOTE : Eight Hour Integration
        if(true) {
            return EightHourIntegration.getDateOfLastReset();
        }

        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.of(ZONE_ID));
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);

        return todayMidnight;
    }

    public static long getSecondsToNextMidnight() {
        //NOTE : Eight Hour Integration
        if(true) {
            return EightHourIntegration.getSecondsToNextResetPerEightHours();
        }

        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate nextDay = LocalDate.now(ZoneId.of(ZONE_ID)).plusDays(1);
        LocalDateTime nextMidnight = LocalDateTime.of(nextDay, midnight);

        long until = nextMidnight.until(LocalDateTime.now(ZoneId.of(ZONE_ID)), ChronoUnit.SECONDS);
        if(until < 0) {
            until *= -1;
        }

        return until;
    }

    public static LocalDateTime getNow() {
        LocalDateTime nowDate = LocalDateTime.now(ZoneId.of(ZONE_ID));
        return nowDate;
    }

    public static class EightHourIntegration {

        public static LocalDateTime getDateOfLastReset() {

            LocalTime nowLocalTime = LocalTime.now(ZoneId.of(ZONE_ID));
            LocalTime lastResetLocalTime = null;
            int nowHour = nowLocalTime.getHour();
            if(nowHour > 16) {
                lastResetLocalTime = LocalTime.of(16,0,0);
            }
            else if(nowHour > 8) {
                lastResetLocalTime = LocalTime.of(8,0,0);
            }
            else {
                lastResetLocalTime = LocalTime.of(0,0,1);
            }

            LocalDate today = LocalDate.now(ZoneId.of(ZONE_ID));
            LocalDateTime resultDateTime = LocalDateTime.of(today, lastResetLocalTime);

            return resultDateTime;
        }

        public static long getSecondsToNextResetPerEightHours() {

            LocalTime nowLocalTime = LocalTime.now(ZoneId.of(ZONE_ID));
            LocalTime nextResetTime = null;
            int nowHour = nowLocalTime.getHour();
            if(nowHour < 8) {
                nextResetTime = LocalTime.of(8,0,0);
            }
            else if(nowHour < 16) {
                nextResetTime = LocalTime.of(16,0,0);
            }
            else {
                nextResetTime = LocalTime.of(23,59,59);
            }

            long until = nextResetTime.until(nowLocalTime, ChronoUnit.SECONDS);
            if(until < 0) {
                until *= -1;
            }

            return until;
        }

    }

}
