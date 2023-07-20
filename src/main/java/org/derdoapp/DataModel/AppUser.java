package org.derdoapp.DataModel;

import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="appUser")
public class AppUser extends AppUserBase {

    public Boolean isTestUser = null;

    public Date createdAt;

    public Date lastLoginDate;

    public Date birthDate;

    public String gender;

    public String version;

    public String platform;

    @Indexed(unique = true)
    public String email;

    public String emailDeletedAccount;

    public String profileImageUrl;

    public String profileImageOriginal;

    public DBObject facebookInfo;

    public DBObject googleInfo;

    public Double latitude;

    public Double longitude;

    public AppUserPrivilageType privilageType;

    public Date privilageStartDate;

    public Boolean isRegisteredUser;

    public Boolean isFirstTimeLogin;

    public Boolean isPrivateModeEnabled;

    public Boolean isDeleted;

    public String latestIp;

    public String deviceId;

    public String appleId;

    public String getProfileImageOriginal() {
        return profileImageOriginal;
    }

    public void setProfileImageOriginal(String profileImageOriginal) {
        this.profileImageOriginal = profileImageOriginal;
    }

    public String getAppleId() {
        return appleId;
    }

    public void setAppleId(String appleId) {
        this.appleId = appleId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLatestIp() {
        return latestIp;
    }

    public void setLatestIp(String latestIp) {
        this.latestIp = latestIp;
    }

    public String getEmailDeletedAccount() {
        return emailDeletedAccount;
    }

    public void setEmailDeletedAccount(String emailDeletedAccount) {
        this.emailDeletedAccount = emailDeletedAccount;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean notificationsEnabled;

    public AppUserSettings appUserSettings;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public AppUserPrivilageType getPrivilageType() {
        return privilageType;
    }

    public void setPrivilageType(AppUserPrivilageType privilageType) {
        this.privilageType = privilageType;
    }

    public Date getPrivilageStartDate() {
        return privilageStartDate;
    }

    public void setPrivilageStartDate(Date privilageStartDate) {
        this.privilageStartDate = privilageStartDate;
    }

    public DBObject getFacebookInfo() {
        return facebookInfo;
    }

    public void setFacebookInfo(DBObject facebookInfo) {
        this.facebookInfo = facebookInfo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getRegisteredUser() {
        return isRegisteredUser;
    }

    public void setRegisteredUser(Boolean registeredUser) {
        isRegisteredUser = registeredUser;
    }

    public Boolean getFirstTimeLogin() {
        return isFirstTimeLogin;
    }

    public void setFirstTimeLogin(Boolean firstTimeLogin) {
        isFirstTimeLogin = firstTimeLogin;
    }

    public Boolean getPrivateModeEnabled() {
        return isPrivateModeEnabled;
    }

    public void setPrivateModeEnabled(Boolean privateModeEnabled) {
        isPrivateModeEnabled = privateModeEnabled;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public AppUserSettings getAppUserSettings() {
        return appUserSettings;
    }

    public void setAppUserSettings(AppUserSettings appUserSettings) {
        this.appUserSettings = appUserSettings;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
