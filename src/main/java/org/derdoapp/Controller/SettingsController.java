package org.derdoapp.Controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.derdoapp.DataManager.SettingsStaticData;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserBase;
import org.derdoapp.DataModel.AppUserSettings;
import org.derdoapp.DataModel.SettingsNuisanceType;
import org.derdoapp.VO.AppUserSettingsVO;
import org.derdoapp.VO.ServiceResponseVO;
import org.derdoapp.VO.SettingsVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/settings")
public class SettingsController extends BaseController {

    @ApiOperation(value = "Gets constants for the settings page. Minimum age, maximum age and nuisances(Dertler)", response = SettingsVO.class)
    @RequestMapping(value = "/getsettingsconstants", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getSettingsConstants() throws Exception {

        System.out.println("getSettingsConstants.Init");

        SettingsVO settingsStaticDataResult = new SettingsVO();
        //TODO : turn gender list into object
        //settingsStaticDataResult.genderTypes = ;
        settingsStaticDataResult.maxAge = SettingsStaticData.MAX_AGE;
        settingsStaticDataResult.minAge = SettingsStaticData.MIN_AGE;
        settingsStaticDataResult.nuisanceTypes = SettingsNuisanceType.getAsNuisanceTypes();

        return SuccessResult(settingsStaticDataResult);
    }

    @ApiOperation(value = "Sets current user's settings, selected minimum age, selected maximum age and selected nuisance's type code", response = AppUserSettingsVO.class)
    @RequestMapping(value = "/setusersettings", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO setUserSettings(
            @ApiParam(value = "Nuisance type code")
            @RequestParam(value = "nuisance") int nuisanceTypeCode,
            @ApiParam(value = "Minimum age")
            @RequestParam(value = "minage") int minAge,
            @ApiParam(value = "Maximum age")
            @RequestParam(value = "maxage") int maxAge,
            @ApiParam(value = "Gender, can be set to 'male' or 'female'", example = "male")
            @RequestParam(value = "gender") String gender) throws Exception {

        System.out.println("nt : " + nuisanceTypeCode + " | min : " + minAge + " | max : " + maxAge + " | ender : " + gender);

        AppUserSettings appUserSettings = new AppUserSettings();
        appUserSettings.gender = gender;
        appUserSettings.maxAge = maxAge;
        appUserSettings.minAge = minAge;
        appUserSettings.nuisanceTypeCode = nuisanceTypeCode;

        AppUserBase appUserBase = getRequestUserBase();
        appUserSettings = appUserRepository.setAppUserSettings(appUserBase.id, appUserSettings);

        AppUserSettingsVO resultVO = new AppUserSettingsVO();
        resultVO.gender = appUserSettings.gender;
        resultVO.maxAge = appUserSettings.maxAge;
        resultVO.minAge = appUserSettings.minAge;
        resultVO.nuisanceTypeCode = appUserSettings.nuisanceTypeCode;

        matchPotRepository.deleteByAppUserId(appUserBase.id);

        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Gets current user's settings, selected minimum age to match, selected maximum age to match and selected nuisance's type code", response = AppUserSettingsVO.class)
    @RequestMapping(value = "/getusersettings", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getUserSettings() throws Exception {

        AppUser appUser = getRequestUser();
        AppUserSettings appUserSettings = appUser.appUserSettings;

        AppUserSettingsVO resultVO = new AppUserSettingsVO();
        resultVO.gender = appUserSettings.gender;
        resultVO.maxAge = appUserSettings.maxAge;
        resultVO.minAge = appUserSettings.minAge;
        resultVO.nuisanceTypeCode = appUserSettings.nuisanceTypeCode;
        resultVO.isFirstTimeLogin = appUser.isFirstTimeLogin;

        if(appUser.isFirstTimeLogin) {
            appUserRepository.updateFirstLoginStatus(appUser.id);
        }

        return SuccessResult(resultVO);
    }

}
