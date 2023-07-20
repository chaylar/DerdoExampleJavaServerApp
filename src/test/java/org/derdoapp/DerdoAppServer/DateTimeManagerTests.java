package org.derdoapp.DerdoAppServer;

import org.derdoapp.DataManager.DateTimeManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class DateTimeManagerTests {

    @Test
    public void getResetTimeValues() {

        LocalDateTime lastResetTime = DateTimeManager.EightHourIntegration.getDateOfLastReset();
        long secondsToNextReset = DateTimeManager.EightHourIntegration.getSecondsToNextResetPerEightHours();

        System.out.println("lastResetTime : " + lastResetTime.toString());
        System.out.println("secondsToNextReset : " + secondsToNextReset);
    }

}
