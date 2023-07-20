package org.derdoapp.Controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserBase;
import org.derdoapp.Helper.FileHelper;
import org.derdoapp.VO.ProfileInfoVO;
import org.derdoapp.VO.ServiceResponseVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.OperationsException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping(value ="/profile")
public class ProfileController extends BaseController {

    @ApiOperation(value = "Returns the profile info of the current user", response = ProfileInfoVO.class)
    @RequestMapping(value = "/getinfo", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getProfileInfo() throws Exception {

        System.out.println("ProfileController.getProfileInfo.INIT");

        AppUserBase appUserBase = getRequestUserBase();
        AppUser appUser = appUserRepository.findById(appUserBase.id);

        ProfileInfoVO pfvo = new ProfileInfoVO();
        SimpleDateFormat birthDateFormat = new SimpleDateFormat("dd.MM.yyyy");//TODO : MOVE THIS TO A GENERIC LOCATION
        pfvo.birthDate = birthDateFormat.format(appUser.birthDate);
        pfvo.userName = appUser.userName;
        pfvo.email = appUser.email;
        pfvo.gender = appUser.gender;
        pfvo.isFirstTimeLogin = appUser.isFirstTimeLogin;
        pfvo.isPrivateModeEnabled = appUser.isPrivateModeEnabled;
        pfvo.notificationsEnabled = appUser.notificationsEnabled;
        //pfvo.profilePictureUrl = new FileHelper().getFilePathByNameFromLocal(appUser.profileImageUrl);
        pfvo.profilePictureUrl = appUser.profileImageUrl;

        if(appUser.isFirstTimeLogin) {
            appUserRepository.updateFirstLoginStatus(appUser.id);
        }

        return SuccessResult(pfvo);
    }

    @ApiOperation(value = "Enables/Disables notifications and returns set value", response = Boolean.class)
    @RequestMapping(value = "/changenotificationsmode", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO changeNotificationsMode() throws Exception {

        AppUserBase appUserBase = getRequestUserBase();
        Boolean result = appUserRepository.changeNotificationsMode(appUserBase.id);

        return SuccessResult(result);
    }

    @ApiOperation(value = "Enables/Disables private mode and returns set value", response = Boolean.class)
    @RequestMapping(value = "/changeprivatemode", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO changePrivateMode() throws Exception {

        AppUserBase appUserBase = getRequestUserBase();
        Boolean result = appUserRepository.changePrivateMode(appUserBase.id);

        return SuccessResult(result);
    }

    @ApiOperation(value = "Deletes user's account", response = Boolean.class)
    @RequestMapping(value = "/deleteaccount", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO deleteAccount() throws Exception {

        AppUserBase appUserBase = getRequestUserBase();
        Boolean result = appUserRepository.deleteAccount(appUserBase.id);

        return SuccessResult(result);
    }

    @ApiOperation(value = "Deletes the profile image of the current user", response = Boolean.class)
    @RequestMapping(value = "/deleteprofileimage", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO deleteProfileImage() throws Exception {

        System.out.println("deleteProfileImage.INIT");

        AppUserBase requestUserBase = getRequestUserBase();
        if(requestUserBase == null) {
            throw new OperationsException();
        }

        appUserRepository.setProfileImage(requestUserBase.id, null);
        return SuccessResult();
    }

    @ApiOperation(value = "Uploads a picture for user's profile image, returns saved file name", response = String.class)
    @RequestMapping(value = "/uploadprofileimage", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO uploadProfileImage(
            @ApiParam(value = "Image multipart file data")
            @RequestParam(value = "ppfile") MultipartFile file) throws Exception {

        System.out.println("uploadProfileImage.INIT");

        if(file == null || file.getSize() <= 0) {
            System.out.println("uploadFile NULL");
            return FailResult();
        }

        AppUserBase requestUserBase = getRequestUserBase();
        if(requestUserBase == null) {
            throw new OperationsException();
        }

        FileHelper fileHelper = new FileHelper();
        String savedFileName = fileHelper.uploadFile(file);

        //NOTE : WILL BE DELETED IF RESIZE WORKS OK
        appUserRepository.setProfileImage(requestUserBase.id, savedFileName);
        //

        return SuccessResult(savedFileName);

        /*
        ImageResizer resizer = new ImageResizer();
        BufferedImage bufferedImage = resizer.resizeFile(file);

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String resizedProfileImageName = fileHelper.uploadResizedImageToBlob(bufferedImage, fileExtension);

        String resizedImageUrl = resizedProfileImageName != null ? resizedProfileImageName : savedFileName;
        appUserRepository.setProfileImageAndOriginalReturnProfileImageUrl(requestUserBase.id, savedFileName, resizedProfileImageName);

        return SuccessResult(resizedImageUrl);
        */
    }

    //TODO : ONLY TEMPORARY SOLUTION TO IMAGE FETCH! REMOVE AFTER CLOUD INTEGRATION
    /*@RequestMapping(value = "/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity image(@RequestParam(value = "name") String imageName) throws Exception {

        String filePath = new FileHelper().getFilePathByNameFromLocal(imageName);

        Path path = Paths.get(filePath);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
    }*/
}
