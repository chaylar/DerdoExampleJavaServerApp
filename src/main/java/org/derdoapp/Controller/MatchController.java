package org.derdoapp.Controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bson.types.ObjectId;
import org.derdoapp.DataManager.DateTimeManager;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserMatch;
import org.derdoapp.DataModel.AppUserMatchPot;
import org.derdoapp.DataModel.AppUserMatchStatus;
import org.derdoapp.Helper.NotificationHttpHelper;
import org.derdoapp.Helper.NotificationSender;
import org.derdoapp.Repository.AppUserMatchRepository;
import org.derdoapp.Repository.NotificationTempHolderRepository;
import org.derdoapp.ServerConfig.MatchConfig;
import org.derdoapp.VO.MatchGetInfoVO;
import org.derdoapp.VO.MatchUserVO;
import org.derdoapp.VO.SendInteractionVO;
import org.derdoapp.VO.ServiceResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.OperationsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value ="/match")
public class MatchController extends BaseController {

    @Autowired
    protected AppUserMatchRepository appUserMatchRepository;

    @Autowired
    protected MatchConfig matchConfig;

    @Autowired
    protected NotificationTempHolderRepository notificationTempHolderRepository;

    //BACKUP
    /*private int fetchRemainingGreetingsCouunt(String appUserId) {
        LocalDateTime lastMidnightDate = DateTimeManager.getDateTodayMidnight();
        int todayGreetingsCount = appUserMatchRepository.getGreetingsFromDate(appUserId, lastMidnightDate);
        //TODO : GET USER PRIVILAGE FOR REMAINING COUNT
        int result = matchConfig.GetDefaultCount() - todayGreetingsCount;

        return result;
    }*/

    private int fetchRemainingGreetingsCouunt(String appUserId) {
        LocalDateTime lastMidnightDate = DateTimeManager.getDateTodayMidnight();
        int todayGreetingsCount = appUserMatchRepository.getGreetingsFromDate(appUserId, lastMidnightDate);
        //TODO : GET USER PRIVILAGE FOR REMAINING COUNT
        int result = matchConfig.GetDefaultCount() - todayGreetingsCount;

        if(result <= 0) {
            result = 0;
        }

        return result;
    }

    private MatchGetInfoVO fetchNextMatch(int fetchCount) throws OperationsException {
        AppUser appUser = getRequestUser();
        if(appUser == null) {
            throw new OperationsException();
        }

        int greetingsLeft = fetchRemainingGreetingsCouunt(appUser.id);

        MatchGetInfoVO matchGetInfoVO = new MatchGetInfoVO();
        matchGetInfoVO.greetingsLeft = greetingsLeft;

        if(greetingsLeft <= 0) {
            matchGetInfoVO.isSubscriptionRequired = true;
        }
        else {
            matchGetInfoVO.isSubscriptionRequired = false;
        }

        matchGetInfoVO.isPrivateModeOn = appUser.isPrivateModeEnabled;
        if(appUser.isPrivateModeEnabled) {
            List<MatchUserVO> emptyMuList = new ArrayList<>();
            matchGetInfoVO.matchUsers = emptyMuList;
            return matchGetInfoVO;
        }

        List<AppUserMatch> passedTillNow = appUserMatchRepository.getPassedUsers(appUser.id);
        List<String> passedIds = passedTillNow.stream().map(x -> x.appUserId2).collect(Collectors.toList());

        //POT USERS
        List<AppUserMatchPot> potUsers = matchPotRepository.getSent(appUser.id);
        List<String> potUserIds = potUsers.stream().map(x -> x.appUserId2).collect(Collectors.toList());
        if(potUserIds != null) {
            passedIds.addAll(potUserIds);
        }

        //NOTE: TO PREVENT SELF MATCH
        passedIds.add(appUser.id);
        //List<AppUser> ungreetedUsers = appUserRepository.getGreetUsersWithExcludeList(appUser.appUserSettings, passedIds, fetchCount);
        List<AppUser> ungreetedUsers = appUserRepository.getGreetUsersWithExcludeListByGenderConditions(appUser.gender, appUser.appUserSettings, passedIds, fetchCount);
        List<MatchUserVO> muVos = ungreetedUsers.stream().map(x -> new MatchUserVO(x, appUser.latitude, appUser.longitude)).collect(Collectors.toList());
        matchGetInfoVO.matchUsers = muVos;
        matchGetInfoVO.resetTimerValue = DateTimeManager.getSecondsToNextMidnight();

        List<AppUserMatchPot> newPots = new ArrayList<>();
        for(int i = 0; i < muVos.size(); i++) {
            MatchUserVO muVo = muVos.get(i);

            AppUserMatchPot mp = new AppUserMatchPot();
            mp.appUserId1 = appUser.id;
            mp.appUserId2 = muVo.id;
            newPots.add(mp);
        }

        matchPotRepository.sendPots(newPots);

        return matchGetInfoVO;
    }

    @ApiOperation(value = "Returns waiting user for the greets screen (Selam Verenler)", response = MatchGetInfoVO.class)
    @RequestMapping(value = "/getwaiting", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getWaiting() throws Exception {
        AppUser appUser = getRequestUser();
        int greetingsLeft = fetchRemainingGreetingsCouunt(appUser.id);
        MatchGetInfoVO matchGetInfoVO = new MatchGetInfoVO();
        matchGetInfoVO.greetingsLeft = greetingsLeft;

        List<AppUserMatch> acceptedMe = appUserMatchRepository.getAcceptedMe(appUser.id);
        if(acceptedMe == null) {
            matchGetInfoVO.matchUsers = new ArrayList<>();
            return SuccessResult(matchGetInfoVO);
        }

        for(int i = 0; i < acceptedMe.size(); i++) {
            System.out.println(acceptedMe.get(i).appUserId1 + " | " + String.valueOf(acceptedMe.get(i).createdAt));
        }

        List<AppUserMatch> iPassed = appUserMatchRepository.getPassedUsers(appUser.id);
        List<AppUserMatch> waitingList = acceptedMe.stream().filter(x -> !iPassed.stream().map(s -> s.appUserId2).collect(Collectors.toList()).contains(x.appUserId1)).collect(Collectors.toList());

        List<AppUser> waitingListUsers = null;
        if(waitingList != null) {
            List<ObjectId> fetchUserIds = waitingList.stream().map(x -> new ObjectId(x.appUserId1)).collect(Collectors.toList());
            waitingListUsers = appUserRepository.getUsersByIds(fetchUserIds);
        }

        List<MatchUserVO> resultList = new ArrayList<>();
        if(waitingListUsers != null && waitingListUsers.size() > 0) {
            resultList = waitingListUsers.stream().map(x -> new MatchUserVO(x, waitingList.stream().filter(y -> y.appUserId1.equals(x.id)).findFirst().orElse(null), appUser.latitude, appUser.longitude)).collect(Collectors.toList());
        }

        Collections.sort(resultList, Collections.reverseOrder());
        matchGetInfoVO.matchUsers = resultList;

        //System.out.println("resultList.size : " + resultList.size());

        notificationTempHolderRepository.deleteByAppUserIdNType(appUser.id, NotificationHttpHelper.NotificationType.GREET.name());
        notificationTempHolderRepository.deleteByAppUserIdNType(appUser.id, NotificationHttpHelper.NotificationType.RE_GREET.name());

        return SuccessResult(matchGetInfoVO);
    }

    @ApiOperation(value = "Returns next users for the match screen", response = MatchGetInfoVO.class)
    @RequestMapping(value = "/getnext", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getNext(
            @ApiParam(value = "Count of users to fetch, optional parameter, default value is 1 if not sent")
            @RequestParam(value = "fetchCount", required = false, defaultValue = "1") int fetchCount) throws Exception {
        System.out.println("getMatches.INIT");
        MatchGetInfoVO matchGetInfoVO = fetchNextMatch(fetchCount);
        return SuccessResult(matchGetInfoVO);
    }

    @ApiOperation(value = "Sends greet/accept to match user (Selam Ver)", response = SendInteractionVO.class)
    @RequestMapping(value = "/sendgreet", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO sendGreet(
            @ApiParam(value = "User Id of the greeted/accepted user")
            @RequestParam(value = "appuserid") String appUserId) throws Exception {

        SendInteractionVO resultVO = new SendInteractionVO();
        AppUser appUser = getRequestUser();
        int greetingsLeft = fetchRemainingGreetingsCouunt(appUser.id);
        AppUserMatch aum = appUserMatchRepository.getAcceptedMe(appUser.id, appUserId);
        Boolean isRespond = false;
        if(aum != null) {
            isRespond = true;
        }

        int myNuisanceTypeCode = appUser.appUserSettings.nuisanceTypeCode;
        appUserMatchRepository.sendGreet(appUser.id, appUserId, myNuisanceTypeCode, isRespond);

        greetingsLeft = isRespond ? greetingsLeft : (greetingsLeft - 1);
        Boolean isSubscriptionRequired = false;
        if(greetingsLeft <= 0) {
            greetingsLeft = 0;
            isSubscriptionRequired = true;
        }

        resultVO.greetingsLeft = greetingsLeft;
        resultVO.isSubscrptionRequired = isSubscriptionRequired;
        resultVO.resetTimerValue = DateTimeManager.getSecondsToNextMidnight();

        //JSONObject greetToUser = SocketMessageHelper.createGreetingObject(appUser.id);
        //Boolean sentToClient = AppSocketClientManager.getInstance().messageToClient(appUserId, greetToUser);

        /*AppUser relatedUser = appUserRepository.findById(appUserId);
        NotificationHelper nh = new NotificationHelper(NotificationHelper.NotificationType.GREET, relatedUser.deviceId);
        nh.Send();*/

        NotificationHttpHelper.NotificationType nt = isRespond ? NotificationHttpHelper.NotificationType.RE_GREET : NotificationHttpHelper.NotificationType.GREET;
        AppUser relatedUser = appUserRepository.findById(appUserId);
        NotificationSender ns = new NotificationSender(nt, relatedUser, notificationTempHolderRepository);
        ns.run();

        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Sends reject for the current match user on match screen (Reddet)", response = SendInteractionVO.class)
    @RequestMapping(value = "/sendreject", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO sendReject(
            @ApiParam(value = "User Id of the rejected user")
            @RequestParam(value = "appuserid") String appUserId) throws Exception {

        AppUser appUser = getRequestUser();
        appUserMatchRepository.sendReject(appUser.id, appUserId);

        SendInteractionVO resultVO = new SendInteractionVO();
        int greetingsLeft = fetchRemainingGreetingsCouunt(appUser.id);
        resultVO.greetingsLeft = greetingsLeft;
        resultVO.isSubscrptionRequired = false;
        resultVO.resetTimerValue = DateTimeManager.getSecondsToNextMidnight();
        if(greetingsLeft <= 0) {
            resultVO.isSubscrptionRequired = true;
        }

        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Blocks a friend", response = Boolean.class)
    @RequestMapping(value = "/sendblock", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO sendBlock(
            @ApiParam(value = "Id of the user in friends list to block")
            @RequestParam(value = "appuserid") String appUserId) throws Exception {
        System.out.println("sendBlock.appuserid : " + appUserId);
        AppUser appUser = getRequestUser();
        List<AppUserMatch> iPassed = appUserMatchRepository.getPassedUsers(appUser.id);
        AppUserMatch updateMatch = null;
        for (int i = 0; i < iPassed.size(); i++) {
            AppUserMatch relatedMatch = iPassed.get(i);
            if(relatedMatch.appUserId1.equals(appUser.id) && relatedMatch.appUserId2.equals(appUserId)) {
                updateMatch = relatedMatch;
                break;
            }
        }

        if(updateMatch != null) {
            System.out.println("sendBlock.updateMatch : " + updateMatch.id);
            System.out.println("sendBlock.updateMatch : " + updateMatch.id);

            updateMatch.matchStatus = AppUserMatchStatus.BLOCK;
            appUserMatchRepository.updateMatch(updateMatch);
        }
        //TODO : Response??!!
        return SuccessResult();
    }

    @ApiOperation(value = "Gets reset timer value until the next reset, specifically the time in seconds till midnight", response = SendInteractionVO.class)
    @RequestMapping(value = "/getresettimervalue", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getResetTimerValue() throws Exception {
        return SuccessResult(DateTimeManager.getSecondsToNextMidnight());
    }
}
