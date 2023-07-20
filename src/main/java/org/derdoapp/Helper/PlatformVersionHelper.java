package org.derdoapp.Helper;

public class PlatformVersionHelper {

    public static Boolean checkFirstIsGreaterEqThanVersion(String firstCheckVersion, String checkVersion) {

        try {
            return Integer.parseInt(firstCheckVersion) >= Integer.parseInt(checkVersion);
        }
        catch(Exception e) {}

        firstCheckVersion = firstCheckVersion.replace(".", "").replace(",", "");
        checkVersion = checkVersion.replace(".", "").replace(",", "");

        if(firstCheckVersion.length() > checkVersion.length()) {
            while(firstCheckVersion.length() != checkVersion.length()) {
                checkVersion += "0";
            }
        }
        else if(checkVersion.length() > firstCheckVersion.length()) {
            while(firstCheckVersion.length() != checkVersion.length()) {
                firstCheckVersion += "0";
            }
        }

        Integer firstCheckInt = Integer.parseInt(firstCheckVersion);
        Integer checkInt = Integer.parseInt(checkVersion);

        if(firstCheckInt >= checkInt) {
            return true;
        }

        return false;
    }

}
