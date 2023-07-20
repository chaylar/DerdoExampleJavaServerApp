package org.derdoapp.Controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserBase;
import org.derdoapp.DataModel.PlatformVersion;
import org.derdoapp.Helper.PlatformVersionHelper;
import org.derdoapp.Repository.PlatformRepository;
import org.derdoapp.VO.ServiceResponseVO;
import org.derdoapp.VO.SimpleResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.OperationsException;
import java.util.List;

@RestController
@RequestMapping(value ="/platformVersion")
public class PlatformVersionController extends BaseController {

    @Autowired
    protected PlatformRepository platformVersionRepository;

    @ApiOperation(value = "Sets platform attribute for the user on database, parameters can be 'ios', 'android'", response = Boolean.class)
    @RequestMapping(value = "/setplatform", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO setPlatform(
            @ApiParam(value = "Platform of the client", example = "android")
            @RequestParam(value = "platform") String platform) throws Exception {

        AppUserBase requestUserBase = getRequestUserBase();
        if(requestUserBase == null) {
            throw new OperationsException();
        }

        appUserRepository.updatePlatform(requestUserBase.id, platform);
        return SuccessResult();
    }

    @ApiOperation(value = "Sets application version for the user on database", response = Boolean.class)
    @RequestMapping(value = "/setversion", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO setVersion(
            @ApiParam(value = "Version of the client")
            @RequestParam(value = "version") String version) throws Exception {

        AppUserBase requestUserBase = getRequestUserBase();
        if(requestUserBase == null) {
            throw new OperationsException();
        }

        appUserRepository.updateVersion(requestUserBase.id, version);
        return SuccessResult();
    }

    @ApiOperation(value = "Checks if the requested version is greater or equals to saved application's latest version in database according to the requested platform, returns false and latest version in description as result ", response = SimpleResultVO.class)
    @RequestMapping(value = "/checkversion", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO checkVersion(
            @ApiParam(value = "Requested version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "Requested platform", example = "android")
            @RequestParam(value = "platform") String platform) throws Exception {

        List<PlatformVersion> platformVersions = platformVersionRepository.findByPlatform(platform);
        PlatformVersion latest = null;
        for(int i = 0; i < platformVersions.size(); i++) {
            PlatformVersion cVersion = platformVersions.get(i);
            if(latest == null) {
                latest = cVersion;
            }

            Boolean isGreater = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion(cVersion.version, latest.version);
            if(isGreater) {
                latest = cVersion;
            }
        }

        Boolean checkResult = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion(version, latest.version);
        SimpleResultVO srvo = new SimpleResultVO();
        srvo.value = checkResult;
        if(checkResult == false) {
            srvo.description = latest.version;
        }

        return SuccessResult(srvo);
    }

    @ApiOperation(value = "Checks if the version set for the user in /setversion service, is greater or equals to saved application's latest version in database, returns false and latest version in description as result ", response = SimpleResultVO.class)
    @RequestMapping(value = "/checkuserversion", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO checkUserVersion() throws Exception {

        AppUser appUser = getRequestUser();
        if(appUser == null) {
            throw new OperationsException();
        }

        List<PlatformVersion> platformVersions = platformVersionRepository.findByPlatform(appUser.platform);
        PlatformVersion latest = null;
        for(int i = 0; i < platformVersions.size(); i++) {
            PlatformVersion cVersion = platformVersions.get(i);
            if(latest == null) {
                latest = cVersion;
            }

            Boolean isGreater = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion(cVersion.version, latest.version);
            if(isGreater) {
                latest = cVersion;
            }
        }

        Boolean checkResult = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion(latest.version, appUser.version);
        SimpleResultVO srvo = new SimpleResultVO();
        srvo.value = checkResult;
        if(checkResult == false) {
            srvo.description = latest.version;
        }

        return SuccessResult(srvo);
    }

    @ApiOperation(value = "Gets latest version in database according to the requested platform", response = PlatformVersion.class)
    @RequestMapping(value = "/getlatestversion", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getLatestVersion(
            @ApiParam(value = "Requested platform", example = "android")
            @RequestParam(value = "platform") String platform) throws Exception {

        List<PlatformVersion> platformVersions = platformVersionRepository.findByPlatform(platform);
        PlatformVersion latest = null;
        for(int i = 0; i < platformVersions.size(); i++) {
            PlatformVersion cVersion = platformVersions.get(i);
            if(latest == null) {
                latest = cVersion;
            }

            Boolean isGreater = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion(cVersion.version, latest.version);
            if(isGreater) {
                latest = cVersion;
            }
        }

        return SuccessResult(latest);
    }

}
