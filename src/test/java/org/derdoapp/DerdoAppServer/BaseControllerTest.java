package org.derdoapp.DerdoAppServer;

import io.jsonwebtoken.lang.Assert;
import org.assertj.core.api.Assertions;
import org.derdoapp.DataManager.SettingsStaticData;
import org.derdoapp.DataManager.TokenGenerator;
import org.derdoapp.DataModel.*;
import org.derdoapp.Repository.AppUserMatchRepository;
import org.derdoapp.Repository.AppUserRepository;
import org.derdoapp.Repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.*;

public class BaseControllerTest {

    //36.747422, 34.539687

    //private static final double startLat = 36.747422;
    //private static final double startLon = 34.539687;

    private static final double startLat = 36.7997758;
    private static final double startLon = 34.444742;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserMatchRepository matchRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @BeforeTestMethod
    protected void removeTestUsersOnDB() {
        System.out.println("removeTestUsersOnDB");
        appUserRepository.deleteTestUsers();
    }

    protected void initFirsdtPlatformVersions() {
        PlatformVersion iosVer = new PlatformVersion();
        iosVer.platform = "ios";
        iosVer.version = "1.0.0";

        PlatformVersion androidVer = new PlatformVersion();
        androidVer.platform = "android";
        androidVer.version = "1.0.0";

        platformRepository.save(iosVer);
        platformRepository.save(androidVer);
    }

    protected AppUser saveTestUser() throws Exception {
        AppUserToken accessToken = TokenGenerator.GenerateToken();
        AppUserToken authToken = TokenGenerator.GenerateToken();

        AppUser saveUser = new AppUser();
        saveUser.userName = "testUser1";
        saveUser.userAccessToken = accessToken;
        saveUser.isTestUser = true;

        //20 Years
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -20);
        Date birthDate = calendar.getTime();

        saveUser.birthDate = birthDate;
        saveUser.userAuthToken = authToken;
        saveUser.email = "test@test1.com";
        saveUser.gender = "male";

        saveUser.createdAt = Calendar.getInstance().getTime();
        saveUser.lastLoginDate = Calendar.getInstance().getTime();

        AppUser testSaveUser = appUserRepository.save(saveUser);
        return testSaveUser;
    }

    protected List<AppUser> generateTestUsers() throws Exception {
        return generateTestUsers(1000);
    }

    protected List<AppUser> generateTestUsers(int testUserCount) throws Exception {

        List<String> userNamesList = new ArrayList<>();
        userNamesList.add("Ahmet");
        userNamesList.add("Mehmet");
        userNamesList.add("Hasan");
        userNamesList.add("Hüseyin");
        userNamesList.add("Ruşen");

        /*List<String> imageNamesList = new ArrayList<>();
        imageNamesList.add("5df0067c-81c2-4739-a8bc-745c232935fd.jpg");
        imageNamesList.add("9bdb116f-9ea4-4970-9761-bfad81ec8c91.jpg");
        imageNamesList.add("51c41ca8-ba75-40e1-8917-f28c2fb1eff1.jpg");
        imageNamesList.add("60ce5388-c444-41ce-8e8a-60d2f9949f77.jpg");
        imageNamesList.add("693ebcd9-48af-4bb2-b7fa-7ce8ba094770.jpg");*/

        List<String> imageNamesList = new ArrayList<>();
        imageNamesList.add("1eacf7ce-935c-40e7-bac3-a4d3173803e5.jpg");
        imageNamesList.add("1eb1fed9-fc5a-411b-8ca4-937c0ac21e95.jpg");
        imageNamesList.add("22c90a07-ad9d-4976-a1b2-f07598d1aefd.jpg");
        imageNamesList.add("45c7390f-bff7-4a71-b2bd-aab629dafa87.jpeg");
        imageNamesList.add("51c41ca8-ba75-40e1-8917-f28c2fb1eff1.jpg");
        imageNamesList.add("60ce5388-c444-41ce-8e8a-60d2f9949f77.jpg");
        imageNamesList.add("6e787d9f-ace0-4b9b-a7a4-8c1476db1800.jpg");
        imageNamesList.add("9bdb116f-9ea4-4970-9761-bfad81ec8c91.jpg");
        imageNamesList.add("e1ff8c3a-aeb9-4156-94ff-414cd70dfdd1.jpeg");

        removeTestUsersOnDB();

        List<AppUser> resultList = new ArrayList<>();
        int selectedNuisanceTypeIndex = 0;
        for(int i = 0; i < testUserCount; i++) {
            try {
                AppUserToken accessToken = TokenGenerator.GenerateToken();
                AppUserToken authToken = TokenGenerator.GenerateToken();

                AppUser saveUser = new AppUser();

                String userName = userNamesList.get(i % userNamesList.size());
                saveUser.userName = userName + " " + i;

                //saveUser.userName = "test " + i;
                //saveUser.latitude = i * 1.0;
                //saveUser.longitude = i * 1.0;

                //saveUser.latitude = (startLat + (i / 100));
                //saveUser.longitude = (startLon + (i / 100));

                Random r = new Random();
                double latLonPlus = r.nextInt(1000000);
                latLonPlus = (latLonPlus / 1100000);
                saveUser.latitude = startLat + latLonPlus;

                latLonPlus = r.nextInt(1000000);
                latLonPlus = (latLonPlus / 1100000);
                saveUser.longitude = startLon + latLonPlus;


                String profileImage = imageNamesList.get(i % imageNamesList.size());
                if((i % 2) == 0) {
                    saveUser.profileImageUrl = "https://derdstorage.blob.core.windows.net/derdcontainer/" + profileImage;
                }

                saveUser.userAccessToken = accessToken;
                saveUser.isRegisteredUser = true;
                saveUser.isFirstTimeLogin = false;
                saveUser.isPrivateModeEnabled = false;

                //20 Years
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -20);
                Date birthDate = calendar.getTime();

                saveUser.birthDate = birthDate;
                saveUser.userAuthToken = authToken;
                saveUser.email = "test@test" + i + ".com";
                //saveUser.gender = "male";
                //saveUser.gender = ((i % 2) == 0) ? "male" : "female";

                saveUser.gender = (i > testUserCount / 2) ? "male" : "female";
                saveUser.isTestUser = true;

                AppUserSettings settings = new AppUserSettings();
                settings.minAge = SettingsStaticData.MIN_AGE;
                settings.maxAge = SettingsStaticData.MAX_AGE;
                settings.gender = SettingsGenderType.getDefault().getGenderTypeName();

                settings.nuisanceTypeCode = SettingsNuisanceType.getValues().get(selectedNuisanceTypeIndex).getNuisanceTypeCode();

                //
                selectedNuisanceTypeIndex++;
                if(selectedNuisanceTypeIndex >= SettingsNuisanceType.getValues().size()) {
                    selectedNuisanceTypeIndex = 0;
                }
                //

                saveUser.appUserSettings = settings;
                saveUser.createdAt = Calendar.getInstance().getTime();
                saveUser.lastLoginDate = Calendar.getInstance().getTime();

                AppUser testSaveUser = appUserRepository.save(saveUser);

                //ASSERT
                Assert.notNull(testSaveUser);
                Assert.notNull(testSaveUser.id);

                Assertions.assertThat(testSaveUser.email).isEqualTo(saveUser.email);
                Assertions.assertThat(testSaveUser.userName).isEqualTo(saveUser.userName);

                resultList.add(testSaveUser);
            }
            catch (Exception ex) {
                System.out.println("insert error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
            }
        }

        return resultList;
    }
    protected List<AppUser> generateTestUsers(int testUserCount, boolean removeTestUsers) throws Exception {

        List<String> userNamesList = new ArrayList<>();
        userNamesList.add("Ahmet");
        userNamesList.add("Mehmet");
        userNamesList.add("Hasan");
        userNamesList.add("Hüseyin");
        userNamesList.add("Ruşen");

        /*List<String> imageNamesList = new ArrayList<>();
        imageNamesList.add("5df0067c-81c2-4739-a8bc-745c232935fd.jpg");
        imageNamesList.add("9bdb116f-9ea4-4970-9761-bfad81ec8c91.jpg");
        imageNamesList.add("51c41ca8-ba75-40e1-8917-f28c2fb1eff1.jpg");
        imageNamesList.add("60ce5388-c444-41ce-8e8a-60d2f9949f77.jpg");
        imageNamesList.add("693ebcd9-48af-4bb2-b7fa-7ce8ba094770.jpg");*/

        List<String> imageNamesList = new ArrayList<>();
        imageNamesList.add("1eacf7ce-935c-40e7-bac3-a4d3173803e5.jpg");
        imageNamesList.add("1eb1fed9-fc5a-411b-8ca4-937c0ac21e95.jpg");
        imageNamesList.add("22c90a07-ad9d-4976-a1b2-f07598d1aefd.jpg");
        imageNamesList.add("45c7390f-bff7-4a71-b2bd-aab629dafa87.jpeg");
        imageNamesList.add("51c41ca8-ba75-40e1-8917-f28c2fb1eff1.jpg");
        imageNamesList.add("60ce5388-c444-41ce-8e8a-60d2f9949f77.jpg");
        imageNamesList.add("6e787d9f-ace0-4b9b-a7a4-8c1476db1800.jpg");
        imageNamesList.add("9bdb116f-9ea4-4970-9761-bfad81ec8c91.jpg");
        imageNamesList.add("e1ff8c3a-aeb9-4156-94ff-414cd70dfdd1.jpeg");

        if(removeTestUsers) {
            removeTestUsersOnDB();
        }

        List<AppUser> resultList = new ArrayList<>();
        int selectedNuisanceTypeIndex = 0;
        for(int i = 0; i < testUserCount; i++) {
            try {
                AppUserToken accessToken = TokenGenerator.GenerateToken();
                AppUserToken authToken = TokenGenerator.GenerateToken();

                AppUser saveUser = new AppUser();

                String userName = userNamesList.get(i % userNamesList.size());
                saveUser.userName = userName + " " + i;

                //saveUser.userName = "test " + i;
                //saveUser.latitude = i * 1.0;
                //saveUser.longitude = i * 1.0;

                //saveUser.latitude = (startLat + (i / 100));
                //saveUser.longitude = (startLon + (i / 100));

                Random r = new Random();
                double latLonPlus = r.nextInt(1000000);
                latLonPlus = (latLonPlus / 1100000);
                saveUser.latitude = startLat + latLonPlus;

                latLonPlus = r.nextInt(1000000);
                latLonPlus = (latLonPlus / 1100000);
                saveUser.longitude = startLon + latLonPlus;


                String profileImage = imageNamesList.get(i % imageNamesList.size());
                if((i % 2) == 0) {
                    saveUser.profileImageUrl = "https://derdstorage.blob.core.windows.net/derdcontainer/" + profileImage;
                }

                saveUser.userAccessToken = accessToken;
                saveUser.isRegisteredUser = true;
                saveUser.isFirstTimeLogin = false;
                saveUser.isPrivateModeEnabled = false;

                //20 Years
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -20);
                Date birthDate = calendar.getTime();

                saveUser.birthDate = birthDate;
                saveUser.userAuthToken = authToken;
                saveUser.email = "test@test" + i + ".com";
                //saveUser.gender = "male";
                //saveUser.gender = ((i % 2) == 0) ? "male" : "female";

                saveUser.gender = (i > testUserCount / 2) ? "male" : "female";
                saveUser.isTestUser = true;

                AppUserSettings settings = new AppUserSettings();
                settings.minAge = SettingsStaticData.MIN_AGE;
                settings.maxAge = SettingsStaticData.MAX_AGE;
                settings.gender = SettingsGenderType.getDefault().getGenderTypeName();

                settings.nuisanceTypeCode = SettingsNuisanceType.getValues().get(selectedNuisanceTypeIndex).getNuisanceTypeCode();

                //
                selectedNuisanceTypeIndex++;
                if(selectedNuisanceTypeIndex >= SettingsNuisanceType.getValues().size()) {
                    selectedNuisanceTypeIndex = 0;
                }
                //

                saveUser.appUserSettings = settings;
                saveUser.createdAt = Calendar.getInstance().getTime();
                saveUser.lastLoginDate = Calendar.getInstance().getTime();

                AppUser testSaveUser = appUserRepository.save(saveUser);

                //ASSERT
                Assert.notNull(testSaveUser);
                Assert.notNull(testSaveUser.id);

                Assertions.assertThat(testSaveUser.email).isEqualTo(saveUser.email);
                Assertions.assertThat(testSaveUser.userName).isEqualTo(saveUser.userName);

                resultList.add(testSaveUser);
            }
            catch (Exception ex) {
                System.out.println("insert error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
            }
        }

        return resultList;
    }

}
