package org.derdoapp.Controller;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.derdoapp.DataManager.SettingsStaticData;
import org.derdoapp.DataManager.TokenGenerator;
import org.derdoapp.DataModel.*;
import org.derdoapp.Repository.AppUserRepository;
import org.derdoapp.Repository.NotificationTempHolderRepository;
import org.derdoapp.VO.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.management.OperationsException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
@RequestMapping(value ="/login")
public class LoginController  extends BaseController {

    @Autowired
    protected AppUserRepository appUserRepository;

    @Autowired
    private NotificationTempHolderRepository notificationTempHolderRepository;

    @ApiOperation(value = "Login services should be used to save user's apple id and get 'authtoken' to be used in 'login' service", response = SignupSocialResultVO.class)
    @RequestMapping(value = "/signupapple", method = RequestMethod.PUT)
    @ResponseBody
    public ServiceResponseVO signupApple(
            @ApiParam(value = "Id for signup")
            @RequestParam(value = "id") String id,
            @ApiParam(value = "Email for signup")
            @RequestParam(value = "email") String email) throws Exception {

        if(id == null || id == "") {
            throw new MissingServletRequestParameterException("id", String.class.getTypeName());
        }

        AppUser appUser = appUserRepository.findByAppleId(id);
        if(appUser == null && email != null && email != "") {
            appUser = appUserRepository.findByEmail(email);
        }

        Boolean isFirstLogin = true;
        Boolean isRegisteredUser = false;
        Boolean hasProfilePhoto = false;
        AppUserSettings settings = null;
        if(appUser != null) {
            //TODO : DO LOGIN & SEND CREDENTIALS!
            isFirstLogin = false;
            isRegisteredUser = appUser.getRegisteredUser() == null ? false : appUser.getRegisteredUser();
            settings = appUser.getAppUserSettings();
            hasProfilePhoto = (appUser.profileImageUrl == null || appUser.profileImageUrl == "") ? false : true;

            if(appUser.appleId == null || appUser.appleId == "") {
                appUser.appleId = appUserRepository.setAppleId(appUser.id, id);
            }
        }
        else {
            isFirstLogin = true;
            isRegisteredUser = false;

            appUser = new AppUser();
            appUser.email = email;
            appUser.appleId = id;

            //SETTINGS
            settings = new AppUserSettings();
            settings.minAge = SettingsStaticData.MIN_AGE;
            settings.maxAge = SettingsStaticData.MAX_AGE;
            appUser.createdAt = Calendar.getInstance().getTime();
            settings.gender = SettingsGenderType.getDefault().getGenderTypeName();
            settings.nuisanceTypeCode = SettingsNuisanceType.getDefault().getNuisanceTypeCode();

            appUser.appUserSettings = settings;
        }

        AppUserToken token = TokenGenerator.GenerateToken();

        appUser.userAuthToken = token;
        appUser.lastLoginDate = Calendar.getInstance().getTime();
        appUserRepository.save(appUser);

        SignupSocialResultVO tokenResult = new SignupSocialResultVO();
        tokenResult.isFirstLogin = isFirstLogin;
        tokenResult.isRegisteredUser = isRegisteredUser;
        tokenResult.hasProfilePhoto = hasProfilePhoto;
        tokenResult.authToken = token.token;

        return SuccessResult(tokenResult);
    }

    //TODO : Check for auth token on request parameters
    @ApiOperation(value = "Login services should be used to save user's google data and get 'authtoken' to be used in 'login' service, only 'signupfacebook' service does not require a token, rest of the services will require'token' in request headers", response = SignupSocialResultVO.class)
    @RequestMapping(value = "/signupgoogle", method = RequestMethod.PUT)
    @ResponseBody
    public ServiceResponseVO signUpGoogle(
            @ApiParam(value = "JSON Object which is returned from google after google signup")
            @RequestParam(value = "googleinfo") JSONObject googleInfo) throws Exception {

        if(googleInfo == null) {
            throw new Exception("NULL parameter");
        }

        String email = null;
        if(googleInfo.has("email")) {
            email = googleInfo.getString("email");
        }

        if(email == null || email == "") {
            throw new MissingServletRequestParameterException("email", String.class.getTypeName());
        }

        AppUser appUser = appUserRepository.findByEmail(email);
        Boolean isFirstLogin = true;
        Boolean isRegisteredUser = false;
        Boolean hasProfilePhoto = false;
        AppUserSettings settings = null;
        if(appUser != null) {
            //TODO : DO LOGIN & SEND CREDENTIALS!
            isFirstLogin = false;
            isRegisteredUser = appUser.getRegisteredUser() == null ? false : appUser.getRegisteredUser();
            settings = appUser.getAppUserSettings();
            hasProfilePhoto = (appUser.profileImageUrl == null || appUser.profileImageUrl == "") ? false : true;
        }
        else {
            isFirstLogin = true;
            isRegisteredUser = false;

            appUser = new AppUser();
            appUser.email = email;

            //TODO : NUISANCE TYPE_ID
            //SETTINGS
            settings = new AppUserSettings();
            settings.minAge = SettingsStaticData.MIN_AGE;
            settings.maxAge = SettingsStaticData.MAX_AGE;
            appUser.createdAt = Calendar.getInstance().getTime();
            settings.gender = SettingsGenderType.getDefault().getGenderTypeName();
            settings.nuisanceTypeCode = SettingsNuisanceType.getDefault().getNuisanceTypeCode();

            appUser.appUserSettings = settings;
        }

        DBObject googleInfoDBObj = (DBObject)JSON.parse(googleInfo.toString());

        AppUserToken token = TokenGenerator.GenerateToken();

        appUser.userAuthToken = token;
        appUser.googleInfo = googleInfoDBObj;
        appUser.lastLoginDate = Calendar.getInstance().getTime();
        appUserRepository.save(appUser);

        SignupSocialResultVO tokenResult = new SignupSocialResultVO();
        tokenResult.isFirstLogin = isFirstLogin;
        tokenResult.isRegisteredUser = isRegisteredUser;
        tokenResult.hasProfilePhoto = hasProfilePhoto;
        tokenResult.authToken = token.token;

        return SuccessResult(tokenResult);
    }

    //TODO : Check for auth token on request parameters
    @ApiOperation(value = "Login services should be used to save user's facebook data and get 'authtoken' to be used in 'login' service, only 'signupfacebook' service does not require a token, rest of the services will require'token' in request headers", response = SignupSocialResultVO.class)
    @RequestMapping(value = "/signupfacebook", method = RequestMethod.PUT)
    @ResponseBody
    public ServiceResponseVO signUpFacebook(
            @ApiParam(value = "JSON Object which is returned from facebook after facebook signup")
            @RequestParam(value = "facebookinfo") JSONObject facebookInfo) throws Exception {

        if(facebookInfo == null) {
            throw new Exception("NULL parameter");
        }

        String email = null;
        if(facebookInfo.has("email")) {
            email = facebookInfo.getString("email");
        }

        if(email == null || email == "") {
            throw new MissingServletRequestParameterException("email", String.class.getTypeName());
        }

        AppUser appUser = appUserRepository.findByEmail(email);
        Boolean isFirstLogin = true;
        Boolean isRegisteredUser = false;
        Boolean hasProfilePhoto = false;
        AppUserSettings settings = null;
        if(appUser != null) {
            //TODO : DO LOGIN & SEND CREDENTIALS!
            isFirstLogin = false;
            isRegisteredUser = appUser.getRegisteredUser() == null ? false : appUser.getRegisteredUser();
            settings = appUser.getAppUserSettings();
            hasProfilePhoto = (appUser.profileImageUrl == null || appUser.profileImageUrl == "") ? false : true;
        }
        else {
            isFirstLogin = true;
            isRegisteredUser = false;

            appUser = new AppUser();
            appUser.email = email;

            //TODO : NUISANCE TYPE_ID
            //SETTINGS
            settings = new AppUserSettings();
            settings.minAge = SettingsStaticData.MIN_AGE;
            settings.maxAge = SettingsStaticData.MAX_AGE;
            appUser.createdAt = Calendar.getInstance().getTime();
            settings.gender = SettingsGenderType.getDefault().getGenderTypeName();
            settings.nuisanceTypeCode = SettingsNuisanceType.getDefault().getNuisanceTypeCode();

            appUser.appUserSettings = settings;
        }

        DBObject facebookInfoDBObj = (DBObject)JSON.parse(facebookInfo.toString());

        AppUserToken token = TokenGenerator.GenerateToken();

        appUser.userAuthToken = token;
        appUser.facebookInfo = facebookInfoDBObj;
        appUser.lastLoginDate = Calendar.getInstance().getTime();
        appUserRepository.save(appUser);

        SignupSocialResultVO tokenResult = new SignupSocialResultVO();
        tokenResult.isFirstLogin = isFirstLogin;
        tokenResult.isRegisteredUser = isRegisteredUser;
        tokenResult.hasProfilePhoto = hasProfilePhoto;
        tokenResult.authToken = token.token;

        return SuccessResult(tokenResult);
    }

    @ApiOperation(value = "Saves user's user name, birthdate and gender", response = Boolean.class)
    @RequestMapping(value = "/signup", method = RequestMethod.PUT)
    @ResponseBody
    public ServiceResponseVO signup(
            @ApiParam(value = "User name")
            @RequestParam(value = "username") String userName,
            @ApiParam(value = "dd/MM/yyyy formatted date string")
            @RequestParam(value = "birthdate") String birthDate,
            @ApiParam(value = "User's gender, can be 'male' or 'female'", example = "male")
            @RequestParam(value = "gender") String gender) throws Exception {

        AppUser updateUser = getRequestUser();
        if(updateUser == null) {
            FailResult();
        }

        updateUser.userName = userName;
        updateUser.birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(birthDate);
        updateUser.gender = gender;
        updateUser.isRegisteredUser = true;
        updateUser.isFirstTimeLogin = true;
        updateUser.isPrivateModeEnabled = false;
        updateUser.notificationsEnabled = true;
        updateUser.createdAt = Calendar.getInstance().getTime();
        updateUser.lastLoginDate = Calendar.getInstance().getTime();

        appUserRepository.save(updateUser);

        return SuccessResult(true);
    }

    @ApiOperation(value = "Saves user's location with latitude & longitude values", response = Boolean.class)
    @RequestMapping(value = "/savelocation", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO saveLocation(
            @ApiParam(value = "Latitude of user")
            @RequestParam(value = "lat") double latitude,
            @ApiParam(value = "Longitude of user")
            @RequestParam(value = "lon") double longitude) throws Exception {

        System.out.println("saveLocation.INIT");

        AppUser appUser = getRequestUser();
        if(appUser == null) {
            FailResult();
        }

        appUser.latitude = latitude;
        appUser.longitude = longitude;

        appUserRepository.save(appUser);

        return SuccessResult();
    }

    @ApiOperation(value = "Returns Request Token for the rest of the services", response = HandShakeVO.class)
    @RequestMapping(value = "/handshake", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO handshake(
            @ApiParam(value = "New authentication token which was acquired from 'login' request")
            @RequestParam(value = "authtoken") String authToken) throws Exception {

        System.out.println("handshake.starting");

        AppUser appUser = appUserRepository.findByAuthTokenTemp(authToken);
        if(appUser == null) {
            return FailResult();
        }

        AppUserToken authTokenSaved = appUserRepository.setNewAuthToken(appUser.id);
        if(authTokenSaved == null) {
            throw new OperationsException();
        }

        AppUserToken accessToken = TokenGenerator.GenerateToken();
        AppUserToken newAccessToken = appUserRepository.setNewAccessToken(appUser.id, accessToken);

        if(newAccessToken == null || (accessToken.token != newAccessToken.token)) {
            throw new OperationsException();
        }

        appUserRepository.setLastLoginDate(appUser.id);
        matchPotRepository.deleteByAppUserId(appUser.id);
        notificationTempHolderRepository.deleteByAppUserId(appUser.id);

        HandShakeVO resultVO = new HandShakeVO();
        resultVO.accessToken = newAccessToken.token;
        resultVO.isRegisteredUser = appUser.getRegisteredUser() != null ? appUser.getRegisteredUser() : false;
        resultVO.hasProfileImage = (appUser.getProfileImageUrl() != null && appUser.getProfileImageUrl() != "") ? true : false;

        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Returns Auth Token for the handshake request", response = LoginVO.class)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO login(
            @ApiParam(value = "Authentication token. Can be acquired through 'login' request")
            @RequestParam(value = "authtoken") String authToken,
            @ApiParam(value = "Device id")
            @RequestParam(value = "deviceid") String deviceId) throws Exception {

        System.out.println("login.starting");

        AppUser appUser = appUserRepository.findByAuthToken(authToken);
        if(appUser == null) {
            System.out.println("login.failResult");
            return FailResult();
        }

        AppUserToken authTokenTemp = TokenGenerator.GenerateToken();
        AppUserToken savedToken = appUserRepository.setNewAuthTokenTemp(appUser.id, authTokenTemp);

        //TODO : Remove if unneccessary
        String newDeviceId = appUserRepository.setNewDeviceId(appUser.id, deviceId);
        try {
            appUserRepository.updateLatestIp(appUser.id, getClientIp());
        }
        catch (Exception e) {
            System.out.println("updateLatestIp.EX");
        }

        if(authTokenTemp == null || (authTokenTemp.token != savedToken.token)) {
            System.out.println("login.operationsException");
            throw new OperationsException();
        }

        LoginVO resultVO = new LoginVO();
        resultVO.authToken = savedToken.token;

        return SuccessResult(resultVO);
    }

    //TODO : FIX IT!
    /*@RequestMapping(value = "/refreshtoken", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO refreshToken() throws Exception {

        System.out.println("refreshToken.starting");

        AppUser appUser = getRequestUser();
        if(appUser == null) {
            System.out.println("login.failResult");
            return FailResult();
        }

        AppUserToken newToken = TokenGenerator.GenerateToken();
        AppUserToken savedToken = appUserRepository.setNewAuthToken(appUser.id, newToken);

        if(newToken == null || savedToken == null || (newToken.token != savedToken.token)) {
            System.out.println("refreshToken.operationsException");
            throw new OperationsException();
        }

        LoginVO resultVO = new LoginVO();
        resultVO.authToken = savedToken.token;

        return SuccessResult(resultVO);
    }*/

    /*@RequestMapping(value = "/testsrv", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO testsrv() throws Exception {

        System.out.println("testsrv.starting");

        TestVO tvo = new TestVO();
        tvo.testBool = true;
        tvo.testStr = "Success";

        AppSocketClientManager.getInstance().pingToClients();
        return SuccessResult();
    }*/

    @RequestMapping(value = "/servicesversion", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO servicesVersion() throws Exception {

        ServicesVersionVO resultVO = new ServicesVersionVO();
        resultVO.version = "1.1.2";

        return SuccessResult(resultVO);
    }
}
