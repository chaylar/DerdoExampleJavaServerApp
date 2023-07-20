package org.derdoapp.DerdoAppServer;

import io.jsonwebtoken.lang.Assert;
import org.assertj.core.api.Assertions;
import org.derdoapp.DataManager.TokenGenerator;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserToken;
import org.derdoapp.Repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class AppUserRepositoryTests extends BaseControllerTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void getUsers() throws Exception {

        String s = "";
        List<AppUser> appUsers = appUserRepository.getAlltestUsers();
        for (int i = 0; i < appUsers.size(); i++) {
            if(appUsers.get(i).userAccessToken == null) {
                continue;
            }

            s += "\"" + appUsers.get(i).userAccessToken.token + "\", ";
        }

        System.out.println("tokens : " + s);
    }

    @Test
    public void saveAppUserTest() {

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
        saveUser.isTestUser = true;
        saveUser.createdAt = Calendar.getInstance().getTime();
        saveUser.lastLoginDate = Calendar.getInstance().getTime();

        AppUser testSaveUser = appUserRepository.save(saveUser);

        //ASSERT
        Assert.notNull(testSaveUser);
        Assert.notNull(testSaveUser.id);

        Assertions.assertThat(testSaveUser.email).isEqualTo(saveUser.email);
        Assertions.assertThat(testSaveUser.userName).isEqualTo(saveUser.userName);
    }

}
