package org.derdoapp.Controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bson.types.ObjectId;
import org.derdoapp.DataManager.DateTimeManager;
import org.derdoapp.DataModel.*;
import org.derdoapp.Helper.EncryptionHelper;
import org.derdoapp.Helper.FileHelper;
import org.derdoapp.Helper.NotificationHttpHelper;
import org.derdoapp.Helper.NotificationSender;
import org.derdoapp.Repository.AppUserMatchRepository;
import org.derdoapp.Repository.AppUserMessageRepository;
import org.derdoapp.Repository.NotificationTempHolderRepository;
import org.derdoapp.ServerConfig.MessageConfig;
import org.derdoapp.VO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value ="/message")
public class MessageController extends BaseController {

    @Autowired
    protected AppUserMatchRepository appUserMatchRepository;

    @Autowired
    protected AppUserMessageRepository appUserMessageRepository;

    @Autowired
    protected MessageConfig messageConfig;

    @Autowired
    protected NotificationTempHolderRepository notificationTempHolderRepository;

    @ApiOperation(value = "Returns user's friends", response = FriendsVO.class)
    @RequestMapping(value = "/getfriends", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getFriends() throws Exception {
        AppUser appUser = getRequestUser();
        FriendsVO resultVO = new FriendsVO();

        List<AppUserMatch> acceptedMe = appUserMatchRepository.getAcceptedMe(appUser.id);
        if(acceptedMe == null) {
            resultVO.matchUsers = new ArrayList<>();
            return SuccessResult(resultVO);
        }

        List<AppUserMatch> iAccepted = appUserMatchRepository.getAcceptedByMe(appUser.id);
        List<AppUserMatch> mutualList = new ArrayList<>();
        for (int i = 0; i < iAccepted.size(); i++) {
            AppUserMatch relatedMatch = iAccepted.get(i);
            AppUserMatch mutualMatch = acceptedMe.stream().filter(x -> x.appUserId1.equals(relatedMatch.appUserId2)).findFirst().orElse(null);
            if(mutualMatch == null) {
                continue;
            }

            mutualList.add(mutualMatch);
        }

        List<MatchUserVO> matchUsers = new ArrayList<>();
        if(mutualList != null) {
            List<ObjectId> fetchUserIds = mutualList.stream().map(x -> new ObjectId(x.appUserId1)).collect(Collectors.toList());
            matchUsers = appUserRepository.getUsersByIds(fetchUserIds).stream().map(x -> new MatchUserVO(x, appUser.latitude, appUser.longitude)).collect(Collectors.toList());
        }

        for(int i = 0; i < matchUsers.size(); i++) {
            MatchUserVO friedVO = matchUsers.get(i);
            AppUserMatch iAcc = iAccepted.stream().filter(x -> x.appUserId1.equals(friedVO.id) || x.appUserId2.equals(friedVO.id)).findFirst().orElse(null);
            AppUserMatch acMe = acceptedMe.stream().filter(x -> x.appUserId1.equals(friedVO.id) || x.appUserId2.equals(friedVO.id)).findFirst().orElse(null);
            int nuisanceTypeCode = -1;
            if(iAcc != null && acMe != null) {
                if(iAcc.createdAt.compareTo(acMe.createdAt) > 0) {
                    nuisanceTypeCode = acMe.nuisanceTypeCode;
                }
                else {
                    nuisanceTypeCode = iAcc.nuisanceTypeCode;
                }
            }

            friedVO.nuisanceTypeCode = nuisanceTypeCode;
            SettingsNuisanceType nt = SettingsNuisanceType.getByTypeCode(nuisanceTypeCode);
            if(nt != null) {
                friedVO.nuisanceTypeName = nt.getNuisanceTypeName();
            }
            else {
                friedVO.nuisanceTypeName = "";
            }
        }

        //System.out.println("resultList.size : " + matchUsers.size());
        resultVO.matchUsers = matchUsers;

        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Returns unread messages for the user", response = MessageListVO.class)
    @RequestMapping(value = "/getunreadmessages", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getUnreadMessages() throws Exception {
        AppUser appUser = getRequestUser();
        MessageListVO resultVO = new MessageListVO();
        List<AppUserMessage> messages = appUserMessageRepository.findUnreadBySentTo(appUser.id);

        /*if(messages == null || messages.size() <= 0) {
            resultVO.messageUsersList = new ArrayList<>();
            return SuccessResult(resultVO);
        }*/
        String myUserId = appUser.id;
        List<MessageVO> messageUsersList = messages == null ? new ArrayList<>() : messages.stream().map(x -> new MessageVO(x, myUserId)).collect(Collectors.toList());
        //resultVO.messageUsersList = messages.stream().map(x -> new MessageVO(x)).collect(Collectors.toList());
        resultVO.messageUsersList = messageUsersList;
        System.out.println("getUnreadMessages.result.size : " + resultVO.messageUsersList.size());

        //NonMessageFriends
        List<AppUserMatch> acceptedMe = appUserMatchRepository.getAcceptedMe(appUser.id);
        List<AppUserMatch> mutualList = new ArrayList<>();
        if(acceptedMe != null) {
            List<AppUserMatch> iAccepted = appUserMatchRepository.getAcceptedByMe(appUser.id);

            for (int i = 0; i < iAccepted.size(); i++) {
                AppUserMatch relatedMatch = iAccepted.get(i);
                AppUserMatch mutualMatch = acceptedMe.stream().filter(x -> x.appUserId1.equals(relatedMatch.appUserId2)).findFirst().orElse(null);
                if(mutualMatch == null) {
                    continue;
                }

                mutualList.add(mutualMatch);
            }
        }

        List<MatchUserVO> matchUsers = new ArrayList<>();
        if(mutualList != null) {
            List<ObjectId> fetchUserIds = mutualList.stream().map(x -> new ObjectId(x.appUserId1)).collect(Collectors.toList());
            matchUsers = appUserRepository.getUsersByIds(fetchUserIds).stream().map(x -> new MatchUserVO(x, appUser.latitude, appUser.longitude)).collect(Collectors.toList());
        }

        List<AppUserMessage> messageFriendIds = appUserMessageRepository.findUserIdsOfMessages(appUser.id);
        List<MatchUserVO> nonMessageMatches = new ArrayList<>();
        for(int i = 0; i < matchUsers.size(); i++) {
            MatchUserVO muvo = matchUsers.get(i);
            Boolean hasMessage = false;
            for(int mi = 0; mi < messageFriendIds.size(); mi++) {
                AppUserMessage mvo = messageFriendIds.get(mi);
                if(mvo.fromAppUserId.equals(muvo.id) || mvo.toAppUserId.equals(muvo.id)) {
                    hasMessage = true;
                    continue;
                }
            }

            if(!hasMessage) {
                nonMessageMatches.add(muvo);
            }
        }

        resultVO.noMessageFriendsList = nonMessageMatches;
        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Returns messages related to friend with userid, page starts from 1", response = MessageListVO.class)
    @RequestMapping(value = "/getusermessages", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getUserMessages(
            @ApiParam(value = "Page number (10 messages per page)")
            @RequestParam(value = "page") int page,
            @ApiParam(value = "User id of the friend")
            @RequestParam(value = "userid") String userId) throws Exception {

        int itemsPerPage = messageConfig.GetItemsPerPage();
        int start = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;

        LocalDateTime ldt = DateTimeManager.getNow();

        AppUser appUser = getRequestUser();
        MessageListVO resultVO = new MessageListVO();
        List<AppUserMessage> messages = appUserMessageRepository.findMessagesRelatedToMeAndUser(appUser.id, userId, start, limit);

        appUserMessageRepository.markAsReadByLatestDate(appUser.id, userId, ldt);

        String myUserId = appUser.id;
        List<MessageVO> messageUsersList = messages == null ? new ArrayList<>() : messages.stream().map(x -> new MessageVO(x, myUserId)).collect(Collectors.toList());

        AppUserMatch toMe = appUserMatchRepository.getAcceptedMe(appUser.id, userId);
        AppUserMatch toUser = appUserMatchRepository.getAcceptedMe(userId, appUser.id);

        int selectedNuisanceId = -1;
        if(toMe.createdAt.compareTo(toUser.createdAt) < 0) {
            selectedNuisanceId = toMe.nuisanceTypeCode;
        }
        else {
            selectedNuisanceId = toUser.nuisanceTypeCode;
        }

        SettingsNuisanceType nt = SettingsNuisanceType.getByTypeCode(selectedNuisanceId);
        if(nt != null) {
            resultVO.nuisanceName = nt.getNuisanceTypeName();
            resultVO.nuisanceTypeCode = nt.getNuisanceTypeCode();
        }

        Collections.sort(messageUsersList, Collections.reverseOrder());
        resultVO.messageUsersList = messageUsersList;

        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Returns latest unread messages related to friend with userid", response = MessageListVO.class)
    @RequestMapping(value = "/getuserunreadmessages", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getUserUnreadMessages(
            @ApiParam(value = "User id of the friend")
            @RequestParam(value = "userid") String userId) throws Exception {

        LocalDateTime ldt = DateTimeManager.getNow();

        AppUser appUser = getRequestUser();
        MessageListVO resultVO = new MessageListVO();
        List<AppUserMessage> messages = appUserMessageRepository.findUnreadMessagesFromUser(appUser.id, userId);

        appUserMessageRepository.markAsReadByLatestDate(appUser.id, userId, ldt);

        String myUserId = appUser.id;
        List<MessageVO> messageUsersList = messages == null ? new ArrayList<>() : messages.stream().map(x -> new MessageVO(x, myUserId)).collect(Collectors.toList());

        Collections.sort(messageUsersList, Collections.reverseOrder());
        resultVO.messageUsersList = messageUsersList;

        return SuccessResult(resultVO);
    }

    @ApiOperation(value = "Returns all latest messages from each chat of the user", response = LastMessagesListVO.class)
    @RequestMapping(value = "/getlatestmessages", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getLatestMessages() throws Exception {
        AppUser appUser = getRequestUser();
        LastMessagesListVO resultVO = new LastMessagesListVO();
        List<AppUserMessage> messages = appUserMessageRepository.findMessagesRelatedToMe(appUser.id);

        //MatchFriends
        List<AppUserMatch> acceptedMe = appUserMatchRepository.getAcceptedMe(appUser.id);
        List<AppUserMatch> mutualList = new ArrayList<>();
        if(acceptedMe != null) {
            List<AppUserMatch> iAccepted = appUserMatchRepository.getAcceptedByMe(appUser.id);

            for (int i = 0; i < iAccepted.size(); i++) {
                AppUserMatch relatedMatch = iAccepted.get(i);
                AppUserMatch mutualMatch = acceptedMe.stream().filter(x -> x.appUserId1.equals(relatedMatch.appUserId2)).findFirst().orElse(null);
                if(mutualMatch == null) {
                    continue;
                }

                mutualList.add(mutualMatch);
            }
        }

        List<MatchUserVO> matchUsers = new ArrayList<>();
        if(mutualList != null) {
            List<ObjectId> fetchUserIds = mutualList.stream().map(x -> new ObjectId(x.appUserId1)).collect(Collectors.toList());
            matchUsers = appUserRepository.getUsersByIds(fetchUserIds).stream().map(x -> new MatchUserVO(x, appUser.latitude, appUser.longitude)).collect(Collectors.toList());
        }

        List<LatestMessageVO> messageUsersList = new ArrayList<>();
        if(messages != null) {
            List<String> filterList = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                AppUserMessage aum = messages.get(i);
                String relatedUserId = null;
                if(aum.fromAppUserId.equals(appUser.id)) {
                    relatedUserId = aum.toAppUserId;
                }
                else {
                    relatedUserId = aum.fromAppUserId;
                }

                if(filterList.contains(relatedUserId) || relatedUserId == null) {
                    continue;
                }

                String filterVar = relatedUserId;
                MatchUserVO muvo = matchUsers.stream().filter(x -> x.id.equals(filterVar)).findFirst().orElse(null);
                if(muvo == null) {
                    continue;
                }

                final String finalRelatedUserId = relatedUserId;
                long unreadMessagesCount = messages.stream().filter(x -> !x.isRead && x.fromAppUserId.equals(finalRelatedUserId) && x.toAppUserId.equals(appUser.id)).count();

                LatestMessageVO addToResultMVO = new LatestMessageVO(muvo, aum, appUser.id);
                addToResultMVO.unreadMessageCount = unreadMessagesCount;
                messageUsersList.add(addToResultMVO);
                filterList.add(relatedUserId);
            }
        }

        List<AppUserMessage> messageFriendIds = messages == null ? new ArrayList<>() : messages;
        List<MatchUserVO> nonMessageMatches = new ArrayList<>();
        for(int i = 0; i < matchUsers.size(); i++) {
            MatchUserVO muvo = matchUsers.get(i);
            Boolean hasMessage = false;
            for(int mi = 0; mi < messageFriendIds.size(); mi++) {
                AppUserMessage mvo = messageFriendIds.get(mi);
                if(mvo.fromAppUserId.equals(muvo.id) || mvo.toAppUserId.equals(muvo.id)) {
                    hasMessage = true;
                    continue;
                }
            }

            if(!hasMessage) {
                nonMessageMatches.add(muvo);
            }
        }

        Collections.sort(messageUsersList, Collections.reverseOrder());
        resultVO.messageUsersList = messageUsersList;

        Collections.sort(nonMessageMatches, Collections.reverseOrder());
        resultVO.noMessageFriendsList = nonMessageMatches;

        notificationTempHolderRepository.deleteByAppUserIdNType(appUser.id, NotificationHttpHelper.NotificationType.MESSAGE.name());;

        return SuccessResult(resultVO);
    }

    /*
    //TODO : OPTIMIZE!
    @ApiOperation(value = "Returns all latest messages from each chat of the user", response = MessageListVO.class)
    @RequestMapping(value = "/getlatestmessages_bck", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponseVO getLatestMessages_bck() throws Exception {
        AppUser appUser = getRequestUser();
        MessageListVO resultVO = new MessageListVO();
        List<AppUserMessage> messages = appUserMessageRepository.findMessagesRelatedToMe(appUser.id);

        //MatchFriends
        List<AppUserMatch> acceptedMe = appUserMatchRepository.getAcceptedMe(appUser.id);
        List<AppUserMatch> mutualList = new ArrayList<>();
        if(acceptedMe != null) {
            List<AppUserMatch> iAccepted = appUserMatchRepository.getAcceptedByMe(appUser.id);

            for (int i = 0; i < iAccepted.size(); i++) {
                AppUserMatch relatedMatch = iAccepted.get(i);
                AppUserMatch mutualMatch = acceptedMe.stream().filter(x -> x.appUserId1.equals(relatedMatch.appUserId2)).findFirst().orElse(null);
                if(mutualMatch == null) {
                    continue;
                }

                mutualList.add(mutualMatch);
            }
        }

        List<MatchUserVO> matchUsers = new ArrayList<>();
        if(mutualList != null) {
            List<ObjectId> fetchUserIds = mutualList.stream().map(x -> new ObjectId(x.appUserId1)).collect(Collectors.toList());
            matchUsers = appUserRepository.getUsersByIds(fetchUserIds).stream().map(x -> new MatchUserVO(x, appUser.latitude, appUser.longitude)).collect(Collectors.toList());
        }

        List<MessageVO> messageUsersList = new ArrayList<>();
        if(messages != null) {
            List<String> filterList = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                AppUserMessage aum = messages.get(i);
                String relatedUserId = null;
                if(aum.fromAppUserId.equals(appUser.id)) {
                    relatedUserId = aum.toAppUserId;
                }
                else {
                    relatedUserId = aum.fromAppUserId;
                }

                if(filterList.contains(relatedUserId) || relatedUserId == null) {
                    continue;
                }

                String filterVar = relatedUserId;
                MatchUserVO muvo = matchUsers.stream().filter(x -> x.id.equals(filterVar)).findFirst().orElse(null);
                if(muvo == null) {
                    continue;
                }

                MessageVO addToResultMVO = new MessageVO(muvo, aum, appUser.id);
                messageUsersList.add(addToResultMVO);
                filterList.add(relatedUserId);
            }
        }

        List<AppUserMessage> messageFriendIds = messages == null ? new ArrayList<>() : messages;
        List<MatchUserVO> nonMessageMatches = new ArrayList<>();
        for(int i = 0; i < matchUsers.size(); i++) {
            MatchUserVO muvo = matchUsers.get(i);
            Boolean hasMessage = false;
            for(int mi = 0; mi < messageFriendIds.size(); mi++) {
                AppUserMessage mvo = messageFriendIds.get(mi);
                if(mvo.fromAppUserId.equals(muvo.id) || mvo.toAppUserId.equals(muvo.id)) {
                    hasMessage = true;
                    continue;
                }
            }

            if(!hasMessage) {
                nonMessageMatches.add(muvo);
            }
        }

        Collections.sort(messageUsersList, Collections.reverseOrder());
        resultVO.messageUsersList = messageUsersList;

        Collections.sort(nonMessageMatches, Collections.reverseOrder());
        resultVO.noMessageFriendsList = nonMessageMatches;

        notificationTempHolderRepository.deleteByAppUserIdNType(appUser.id, NotificationHttpHelper.NotificationType.MESSAGE.name());;

        return SuccessResult(resultVO);
    }*/

    @ApiOperation(value = "Sends user a message, user must be in the friends list. Returns id of the saved message.", response = String.class)
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO sendMessage(
            @ApiParam(value = "Content for the message")
            @RequestParam(value = "content") String content,
            @ApiParam(value = "User id of the friend to send message")
            @RequestParam(value = "userid") String toUserId) throws Exception {

        System.out.println("sendMessage.content : " + content + " | toUser : " + toUserId);

        AppUser appUser = getRequestUser();
        AppUserMatch acceptedUserMatch = appUserMatchRepository.getAcceptedMe(appUser.id, toUserId);
        if(acceptedUserMatch == null) {
            FailResult();
        }

        String encrypted = EncryptionHelper.encrypt(content);
        Boolean isEncrypted = false;
        if(encrypted != null && !encrypted.equals("")) {
            isEncrypted = true;
        }

        AppUserMessage appUserMessage = new AppUserMessage();
        appUserMessage.createdAt = DateTimeManager.getNow();
        if(isEncrypted) {
            appUserMessage.message = encrypted;
        }
        else {
            appUserMessage.message = content;
        }

        appUserMessage.fromAppUserId = appUser.id;
        appUserMessage.toAppUserId = toUserId;
        appUserMessage.isEncrypted = isEncrypted;
        appUserMessage.messageType = MessageType.TEXT_MESSAGE.getTypeName();
        appUserMessage.isRead = false;

        appUserMessage = appUserMessageRepository.save(appUserMessage);
        System.out.println("Message.Id : " + appUserMessage.id);

        //TODO : send message as JSONObject!
        //JSONObject messageToUser = SocketMessageHelper.createMessageObject(appUserMessage.id, appUserMessage.fromAppUserId, appUserMessage.message);
        //Boolean sentToClient = AppSocketClientManager.getInstance().messageToClient(appUserMessage.toAppUserId, messageToUser);
        //Boolean sentToChatRoom = ChatRoomClientManager.getInstance().messageToClient(appUserMessage.toAppUserId, appUser.id, messageToUser);
        //Boolean sentToChatRoomSocketIO = ChatRoomSocketIOClientManager.getInstance().messageToClient(appUserMessage.toAppUserId, appUser.id, messageToUser);

        //NOTIFICATION
        /*AppUser relatedUser = appUserRepository.findById(toUserId);
        NotificationHelper nh = new NotificationHelper(NotificationHelper.NotificationType.MESSAGE, relatedUser.deviceId);
        nh.Send();*/

        AppUser relatedUser = appUserRepository.findById(toUserId);
        NotificationSender ns = new NotificationSender(NotificationHttpHelper.NotificationType.MESSAGE, relatedUser, notificationTempHolderRepository);
        ns.run();

        return SuccessResult(appUserMessage.id);
    }

    @ApiOperation(value = "Sends user a message, user must be in the friends list. Returns id of the saved message.", response = String.class)
    @RequestMapping(value = "/sendvoice", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO sendVoice(
            @ApiParam(value = "Sound file")
            @RequestParam(value = "sfile") MultipartFile file,
            @ApiParam(value = "User id of the friend to send message")
            @RequestParam(value = "userid") String toUserId) throws Exception {

        System.out.println("sendVoice.content : toUser : " + toUserId);

        AppUser appUser = getRequestUser();
        AppUserMatch acceptedUserMatch = appUserMatchRepository.getAcceptedMe(appUser.id, toUserId);
        if(acceptedUserMatch == null) {
            FailResult();
        }

        if(file == null || file.getSize() <= 0) {
            System.out.println("uploadFile NULL");
            return FailResult();
        }

        FileHelper fileHelper = new FileHelper();
        String savedFileName = fileHelper.uploadFile(file);

        AppUserMessage appUserMessage = new AppUserMessage();
        appUserMessage.fileUrl = savedFileName;
        appUserMessage.createdAt = DateTimeManager.getNow();
        appUserMessage.fromAppUserId = appUser.id;
        appUserMessage.toAppUserId = toUserId;
        appUserMessage.isEncrypted = false;
        appUserMessage.messageType = MessageType.VOICE_MESSAGE.getTypeName();
        appUserMessage.isRead = false;

        appUserMessage = appUserMessageRepository.save(appUserMessage);
        System.out.println("Message.Id : " + appUserMessage.id);

        //NOTIFICATION
        /*AppUser relatedUser = appUserRepository.findById(toUserId);
        NotificationHelper nh = new NotificationHelper(NotificationHelper.NotificationType.MESSAGE, relatedUser.deviceId);
        nh.Send();*/

        AppUser relatedUser = appUserRepository.findById(toUserId);
        NotificationSender ns = new NotificationSender(NotificationHttpHelper.NotificationType.MESSAGE, relatedUser, notificationTempHolderRepository);
        ns.run();

        return SuccessResult(appUserMessage.id);
    }

    @ApiOperation(value = "Sends message ids to server to mark them as read on database", response = Boolean.class)
    @RequestMapping(value = "/markasread", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO markAsRead(
            @ApiParam(value = "Comma seperated ids of the messages")
            @RequestParam(value = "ids") String ids) throws Exception {

        /*System.out.println("markAsRead.ids : " + ids);

        AppUser appUser = getRequestUser();
        List<String> messageIds = Arrays.asList(ids.split(","));

        appUserMessageRepository.markAsReadSingular(appUser.id, messageIds);*/
        return SuccessResult(true);
    }

    @ApiOperation(value = "Sends one message id to server to mark messages before that date as read", response = Boolean.class)
    @RequestMapping(value = "/markasreadbylast", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO markAsReadByLatest(
            @ApiParam(value = "Id of the latest message which was fetched from server, marks all messages until that one as read")
            @RequestParam(value = "id") String id) throws Exception {

        /*AppUser appUser = getRequestUser();
        appUserMessageRepository.markAsReadSingularByLatest(appUser.id, id);*/
        return SuccessResult(true);
    }

    @ApiOperation(value = "Sends message ids to server to delete from database permanently", response = Boolean.class)
    @RequestMapping(value = "/removemessages", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO removeMessages(
            @ApiParam(value = "Comma seperated ids of the messages")
            @RequestParam(value = "ids") String ids) throws Exception {

        System.out.println("removeMessages.ids : " + ids);

        AppUser appUser = getRequestUser();
        List<String> messageIds = Arrays.asList(ids.split(","));
        appUserMessageRepository.deleteByIds(appUser.id, messageIds);
        return SuccessResult(true);
    }

    @ApiOperation(value = "Deletes messages related to my user and sent userid", response = Boolean.class)
    @RequestMapping(value = "/removeusermessages", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponseVO removeUserMessages(
            @ApiParam(value = "Userıd of friend user")
            @RequestParam(value = "userid") String id) throws Exception {

        System.out.println("removeMessages.userid : " + id);

        AppUser appUser = getRequestUser();
        appUserMessageRepository.deleteByUserIds(appUser.id, id);
        return SuccessResult(true);
    }
}
