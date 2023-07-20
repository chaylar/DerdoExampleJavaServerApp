package org.derdoapp.VO;

import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserMatch;

import java.time.LocalDateTime;
import java.util.Calendar;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class MatchUserVO implements Comparable<MatchUserVO> {

    //private static final Double LATLON_KM_CONSTANT = 111.699;

    private static final Double LATLON_M_CONSTANT = 111699.0;

    private MatchUserVO(AppUser appUser) {
        this.userName = appUser.userName;

        Calendar currentDateCalendar = Calendar.getInstance();
        Calendar userBirthDateCalendar = Calendar.getInstance();
        userBirthDateCalendar.setTime(appUser.birthDate);

        int age = (currentDateCalendar.get(YEAR) - userBirthDateCalendar.get(YEAR)) + 1;
        if (currentDateCalendar.get(MONTH) > userBirthDateCalendar.get(MONTH) ||
                (currentDateCalendar.get(MONTH) == userBirthDateCalendar.get(MONTH) && currentDateCalendar.get(Calendar.DATE) > userBirthDateCalendar.get(Calendar.DATE))) {
            age--;
        }

        //this.matchId = appUserMatch.id;
        this.id = appUser.id;
        this.age = age;
        this.profilePictureUrl = appUser.profileImageUrl;
    }

    //TODO : DISTANCE!!!!???
    public MatchUserVO(AppUser appUser, Double requesterLatitude, Double requesterLongitude) {
        this(appUser);

        //TODO : SHOULD NOT BE NULL | FIX IT!
        if(appUser.longitude != null && appUser.latitude != null && requesterLatitude != null && requesterLongitude != null) {
            this.distance = ((Math.pow(requesterLatitude - appUser.latitude, 2) + Math.pow(requesterLongitude - appUser.longitude, 2)) / 2.0) * LATLON_M_CONSTANT /*LATLON_KM_CONSTANT*/;
        }
    }

    public MatchUserVO(AppUser appUser, AppUserMatch userMatch, Double requesterLatitude, Double requesterLongitude) {
        this(appUser, requesterLatitude, requesterLongitude);
        if(userMatch != null) {
            this.createdAt = userMatch.createdAt;
        }
    }

    public String id;

    //public String matchId;

    public String userName;

    public String profilePictureUrl;

    public int age;

    public int nuisanceTypeCode;

    public String nuisanceTypeName;

    public Double distance;

    public LocalDateTime createdAt;

    @Override
    public int compareTo(MatchUserVO o) {
        try {
            if (this.createdAt == null) {
                return -1;
            } else if (o.createdAt == null) {
                return 1;
            }

            return createdAt.compareTo(o.createdAt);
        }
        catch (Exception e) {
            System.out.println("MatchUserVO.compareTo.EX : " + e.getMessage() != null ? e.getMessage() : "MESSAGE_WAS_NULL");
        }

        return -1;
    }

    //public Double latitude;

    //public Double longitude;

}
