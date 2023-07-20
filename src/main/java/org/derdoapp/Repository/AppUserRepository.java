package org.derdoapp.Repository;

import org.bson.types.ObjectId;
import org.derdoapp.DataManager.DateTimeManager;
import org.derdoapp.DataManager.SettingsStaticData;
import org.derdoapp.DataModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@Repository
public class AppUserRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void deleteTestUsers() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isTestUser").is(true));
        mongoTemplate.remove(query, AppUser.class);
    }

    public AppUser findByEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, AppUser.class);
    }

    public AppUser findByAppleId(String appleId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appleId").is(appleId));
        return mongoTemplate.findOne(query, AppUser.class);
    }

    public AppUser findByAuthToken(String authToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userAuthToken.token").is(authToken));
        return mongoTemplate.findOne(query, AppUser.class);
    }

    public AppUser findByAuthTokenTemp(String authTokenTemp) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userAuthTokenTemp.token").is(authTokenTemp));
        return mongoTemplate.findOne(query, AppUser.class);
    }

    public AppUser findByAccessToken(String accessToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userAccessToken.token").is(accessToken));
        return mongoTemplate.findOne(query, AppUser.class);
    }

    public AppUserBase findBaseByAccessToken(String accessToken) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userAccessToken.token").is(accessToken));
        return mongoTemplate.findOne(query, AppUserBase.class);
    }

    public AppUserToken setNewAuthTokenTemp(String userId, AppUserToken authTokenTemp) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.userAuthTokenTemp = authTokenTemp;
        appUser = mongoTemplate.save(appUser);

        return appUser.userAuthTokenTemp;
    }

    public String setNewDeviceId(String userId, String deviceId) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.deviceId = deviceId;
        appUser = mongoTemplate.save(appUser);

        return appUser.deviceId;
    }

    public String setAppleId(String userId, String appleId) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.appleId = appleId;
        appUser = mongoTemplate.save(appUser);

        return appUser.appleId;
    }

    public String setProfileImage(String userId, String profileImageUrl) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.profileImageUrl = profileImageUrl;
        appUser = mongoTemplate.save(appUser);

        return appUser.profileImageUrl;
    }

    public String setProfileImageOriginal(String userId, String profileImageOriginal) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.profileImageOriginal = profileImageOriginal;
        appUser = mongoTemplate.save(appUser);

        return appUser.profileImageOriginal;
    }

    public String setProfileImageAndOriginalReturnProfileImageUrl(String userId, String profileImageOriginal, String profileImageUrl) {
        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.profileImageUrl = profileImageUrl;
        appUser.profileImageOriginal = profileImageOriginal;
        appUser = mongoTemplate.save(appUser);

        return appUser.profileImageUrl;
    }

    public AppUserSettings setAppUserSettings(String userId, AppUserSettings nSettings) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.appUserSettings = nSettings;
        appUser = mongoTemplate.save(appUser);

        return appUser.appUserSettings;
    }

    public AppUserToken setNewAuthToken(String userId) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);

        AppUserToken authToken = appUser.userAuthTokenTemp;
        appUser.userAuthToken = authToken;
        appUser.userAuthTokenTemp = null;

        appUser = mongoTemplate.save(appUser);

        return appUser.userAuthToken;
    }

    public Boolean changeNotificationsMode(String userId) {

        System.out.println("changeNotificationsMode.INIT");

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.notificationsEnabled = !appUser.notificationsEnabled;
        appUser = mongoTemplate.save(appUser);

        return appUser.notificationsEnabled;
    }

    public Boolean changePrivateMode(String userId) {

        System.out.println("changePrivateMode.INIT");

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.isPrivateModeEnabled = !appUser.isPrivateModeEnabled;
        appUser = mongoTemplate.save(appUser);

        return appUser.isPrivateModeEnabled;
    }

    public Boolean deleteAccount(String userId) {

        System.out.println("deleteAccount.INIT");

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        String email = appUser.email;

        appUser.isDeleted = true;
        appUser.emailDeletedAccount = email;
        appUser.email = appUser.email + "_" + appUser.id;
        appUser.appleId = appUser.appleId + "_" + appUser.id + "_del";
        appUser.userAuthToken = null;
        appUser.userAccessToken = null;

        appUser = mongoTemplate.save(appUser);

        return appUser.isDeleted;
    }

    public AppUserToken setNewAccessToken(String userId, AppUserToken accessToken) {

        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.userAccessToken = accessToken;
        appUser = mongoTemplate.save(appUser);

        //TODO : ??
        return appUser.userAccessToken;
    }

    public void setLastLoginDate(String userId) {
        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.lastLoginDate = Calendar.getInstance().getTime();
        mongoTemplate.save(appUser);
    }

    public void updateFirstLoginStatus(String userId) {
        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.isFirstTimeLogin = false;
        mongoTemplate.save(appUser);
    }

    public void updateVersion(String userId, String version) {
        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.version = version;
        mongoTemplate.save(appUser);
    }

    public void updatePlatform(String userId, String platform) {
        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.platform = platform;
        mongoTemplate.save(appUser);
    }

    public void updateLatestIp(String userId, String ip) {
        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        appUser.latestIp = ip;
        mongoTemplate.save(appUser);
    }

    public AppUser findById(String userId) {
        AppUser appUser = mongoTemplate.findById(userId, AppUser.class);
        return appUser;
    }

    public List<AppUser> getGreetUsersWithExcludeListByGenderConditions(String appUserGender, AppUserSettings appUserSettings, List<String> appUserIds, int count) {
        Query fullQuery = new Query();
        fullQuery.addCriteria(Criteria.where("id").nin(appUserIds));

        //NOTE : MAX+
        int maxAge = 0;
        if(appUserSettings.maxAge >= SettingsStaticData.MAX_AGE) {
            maxAge = 300; //ULTIMATE MAX AGE
        }
        else {
            maxAge = appUserSettings.maxAge;
        }

        LocalDateTime maxAgeYear = DateTimeManager.getNow().plusYears(-1 * maxAge);
        LocalDateTime minAgeYear = DateTimeManager.getNow().plusYears(-1 * appUserSettings.minAge);
        fullQuery.addCriteria(Criteria.where("birthDate").gte(maxAgeYear).lte(minAgeYear));

        fullQuery.addCriteria(Criteria.where("isRegisteredUser").is(true));
        fullQuery.addCriteria(Criteria.where("isPrivateModeEnabled").is(false));
        fullQuery.addCriteria(Criteria.where("isDeleted").ne(true));
        fullQuery.addCriteria(Criteria.where("appUserSettings.nuisanceTypeCode").is(appUserSettings.nuisanceTypeCode));

        if(!appUserSettings.gender.equals(SettingsGenderType.NO_PREFERENCE.getGenderTypeName())) {
            fullQuery.addCriteria(Criteria.where("gender").is(appUserSettings.gender));
        }

        if(appUserGender.equals(SettingsGenderType.MALE.getGenderTypeName())) {
            Criteria femaleOnSettings = Criteria.where("appUserSettings.gender").is(SettingsGenderType.FEMALE.getGenderTypeName());
            Criteria withFemaleGender = Criteria.where("gender").is(SettingsGenderType.FEMALE.getGenderTypeName());
            Criteria femalesWithOnlyFemalesOnGenderSettings = new Criteria().andOperator(
                    femaleOnSettings,
                    withFemaleGender
            );

            Criteria notFemalesWithOnlyFemalesOnGenderSettings = new Criteria().norOperator(femalesWithOnlyFemalesOnGenderSettings);
            fullQuery.addCriteria(notFemalesWithOnlyFemalesOnGenderSettings);
        }

        //lastLoginDate
        fullQuery = fullQuery.with(Sort.by(Sort.Direction.DESC, "lastLoginDate"));

        return mongoTemplate.find(fullQuery.limit(count), AppUser.class);
    }

    //TODO : LOCATION!
    public List<AppUser> getGreetUsersWithExcludeList(AppUserSettings appUserSettings, List<String> appUserIds, int count) {
        Query fullQuery = new Query();
        fullQuery.addCriteria(Criteria.where("id").nin(appUserIds));

        //NOTE : MAX+
        int maxAge = 0;
        if(appUserSettings.maxAge >= SettingsStaticData.MAX_AGE) {
            maxAge = 300; //ULTIMATE MAX AGE
        }
        else {
            maxAge = appUserSettings.maxAge;
        }

        LocalDateTime maxAgeYear = DateTimeManager.getNow().plusYears(-1 * maxAge);
        LocalDateTime minAgeYear = DateTimeManager.getNow().plusYears(-1 * appUserSettings.minAge);
        fullQuery.addCriteria(Criteria.where("birthDate").gte(maxAgeYear).lte(minAgeYear));

        fullQuery.addCriteria(Criteria.where("isRegisteredUser").is(true));
        fullQuery.addCriteria(Criteria.where("isPrivateModeEnabled").is(false));
        fullQuery.addCriteria(Criteria.where("isDeleted").ne(true));
        fullQuery.addCriteria(Criteria.where("appUserSettings.nuisanceTypeCode").is(appUserSettings.nuisanceTypeCode));

        if(!appUserSettings.gender.equals(SettingsGenderType.NO_PREFERENCE.getGenderTypeName())) {
            fullQuery.addCriteria(Criteria.where("gender").is(appUserSettings.gender));
        }

        //lastLoginDate
        fullQuery = fullQuery.with(Sort.by(Sort.Direction.DESC, "lastLoginDate"));

        return mongoTemplate.find(fullQuery.limit(count), AppUser.class);
    }

    public List<AppUser> getUsersByIds(List<ObjectId> appUserIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").ne(true));
        query.addCriteria(Criteria.where("id").in(appUserIds));
        return mongoTemplate.find(query, AppUser.class);
    }

    public List<AppUser> getAlltestUsers() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isTestUser").is(true));
        return mongoTemplate.find(query, AppUser.class);
    }

    public AppUser save(AppUser appUser) {
        return mongoTemplate.save(appUser);
    }
}
