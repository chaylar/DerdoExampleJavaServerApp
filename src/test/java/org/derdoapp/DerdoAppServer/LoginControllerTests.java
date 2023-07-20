package org.derdoapp.DerdoAppServer;

import io.jsonwebtoken.lang.Assert;
import org.derdoapp.Controller.LoginController;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.RequestParams;
import org.derdoapp.VO.ServiceResponseVO;
import org.derdoapp.VO.SignupSocialResultVO;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringBootTest
public class LoginControllerTests extends BaseControllerTest {

    @Autowired
    private LoginController loginController;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String jsonSignupStr = "{\"id\":\"10159949745301164\",\"isTestUser\":true, \"name\":\"Çağlar Lobisi\",\"email\":\"chaylar@gmail.com\",\"birthday\":\"04\\/08\\/1985\",\"first_name\":\"Çağlar\",\"last_name\":\"Lobisi\",\"gender\":\"male\",\"location\":{\"id\":\"105977082774327\",\"name\":\"Harare, Zimbabwe\"},\"picture\":{\"data\":{\"height\":50,\"is_silhouette\":false,\"url\":\"https:\\/\\/platform-lookaside.fbsbx.com\\/platform\\/profilepic\\/?asid=10159949745301164&height=50&width=50&ext=1590338660&hash=AeS2gW8yCsaU7ruy\",\"width\":50}}}";
    private final String jsonGoogleSignupStr = "{\"id\":\"10159949745301164\",\"isTestUser\":true, \"name\":\"gTest\",\"email\":\"gtest@gmail.com\"}";

    @Test
    public void signUpFacebookTest() throws Exception {

        //Required Operations
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("test@test1.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        if(appUser != null) {
            mongoTemplate.remove(appUser);
            appUser = mongoTemplate.findOne(query, AppUser.class);
        }

        //If Exists - Stops Operation
        Assert.isNull(appUser);
        //

        JSONObject facebookLoginObj = new JSONObject(jsonSignupStr);
        ServiceResponseVO responseVO = loginController.signUpFacebook(facebookLoginObj);

        //Assertions
        Assert.notNull(responseVO);
        Assert.isTrue(responseVO.success);

        //TODO : TEST THIS AFTER ACCES TOKEN CHANGES!
        //Query queryTestUser = new Query();
        //queryTestUser.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //AppUser testUser = mongoTemplate.findOne(queryTestUser, AppUser.class);

        //Assertions.assertNotEquals(appUser.accessToken, testUser.accessToken);
        //Assertions.assertNotEquals(appUser.authToken, testUser.authToken);
    }

    @Test
    public void signupAppleTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("asu@test.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        if(appUser != null) {
            mongoTemplate.remove(appUser);
            appUser = mongoTemplate.findOne(query, AppUser.class);
        }
        //
        Assert.isNull(appUser);
        //
        ServiceResponseVO responseVO = loginController.signupApple("test_apple_id", "asu@test.com");

        //Assertions
        Assert.notNull(responseVO);
        Assert.isTrue(responseVO.success);

        //Required Operations
        appUser = mongoTemplate.findOne(query, AppUser.class);
        //If Exists - Stops Operation
        Assertions.assertNotNull(appUser);
        Assertions.assertNotNull(appUser.appleId);
        //
    }

    @Test
    public void loginAppleTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("asu@test.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        if(appUser != null) {
            mongoTemplate.remove(appUser);
            appUser = mongoTemplate.findOne(query, AppUser.class);
        }
        //
        Assert.isNull(appUser);
        //
        ServiceResponseVO responseVO = loginController.signupApple("test_apple_id", "asu@test.com");

        //Assertions
        Assert.notNull(responseVO);
        Assert.isTrue(responseVO.success);

        //Required Operations
        appUser = mongoTemplate.findOne(query, AppUser.class);
        //If Exists - Stops Operation
        Assertions.assertNotNull(appUser);
        //

        responseVO = loginController.signupApple("test_apple_id", null);

        //Assertions
        Assert.notNull(responseVO);
        Assert.isTrue(responseVO.success);

        //Required Operations

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("appleId").is("test_apple_id"));
        AppUser appUserLogin = mongoTemplate.findOne(query2, AppUser.class);
        //If Exists - Stops Operation
        Assertions.assertNotNull(appUserLogin);
        //
        Assertions.assertEquals(appUserLogin.email, "asu@test.com");
        //
        Assertions.assertEquals(appUserLogin.id, appUser.id);
        Assertions.assertEquals(appUserLogin.appleId, appUser.appleId);

    }

    @Test
    public void signUpGoogleTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("gtest@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        if(appUser != null) {
            mongoTemplate.remove(appUser);
            appUser = mongoTemplate.findOne(query, AppUser.class);
        }
        //
        Assert.isNull(appUser);
        //

        JSONObject googleLoginObj = new JSONObject(jsonGoogleSignupStr);
        ServiceResponseVO responseVO = loginController.signUpGoogle(googleLoginObj);

        //Assertions
        Assert.notNull(responseVO);
        Assert.isTrue(responseVO.success);

        //Required Operations
        appUser = mongoTemplate.findOne(query, AppUser.class);
        //If Exists - Stops Operation
        Assertions.assertNotNull(appUser);
        //
    }

    @Test
    public void signUpRawTest() throws Exception {

        //Required Operations
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("test@test1.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        if(appUser != null) {
            mongoTemplate.remove(appUser);
            appUser = mongoTemplate.findOne(query, AppUser.class);
        }

        //If Exists - Stops Operation
        Assert.isNull(appUser);
        //

        JSONObject facebookLoginObj = new JSONObject(jsonSignupStr);
        ServiceResponseVO responseVO = loginController.signUpFacebook(facebookLoginObj);

        //Assertions
        Assert.notNull(responseVO);
        Assert.isTrue(responseVO.success);

        SignupSocialResultVO ssrvo = (SignupSocialResultVO) responseVO.data;
        Assert.notNull(ssrvo);
        //

        String testUserName = "testUserName1";
        String testBirthDate = "04/08/1985";
        String testGender = "male";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO signupResponseVO = loginController.signup(testUserName, testBirthDate, testGender);

        //Assertions
        Assert.notNull(signupResponseVO);
        Assert.isTrue(signupResponseVO.success);
    }
}
