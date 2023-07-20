package org.derdoapp.DerdoAppServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.io.IOUtils;
import org.derdoapp.Controller.MatchController;
import org.derdoapp.Controller.MessageController;
import org.derdoapp.DataManager.DateTimeManager;
import org.derdoapp.DataModel.*;
import org.derdoapp.Helper.SocketMessageHelper;
import org.derdoapp.Repository.AppUserMatchRepository;
import org.derdoapp.Repository.AppUserMessageRepository;
import org.derdoapp.Repository.AppUserRepository;
import org.derdoapp.SocketManager.AppSocketClientManager;
import org.derdoapp.VO.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MessageControllerTests extends BaseControllerTest {

    @Autowired
    private MatchController matchController;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppUserMatchRepository matchRepository;

    @Autowired
    private AppUserMessageRepository messageRepository;

    @Autowired
    private MessageController messageController;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void socketMessageTest() throws Exception {
        List<AppUser> allTestUsers = appUserRepository.getAlltestUsers();
        for(int i = 0; i < allTestUsers.size(); i++) {
            AppUser appUser = allTestUsers.get(i);
            String token = appUser.userAccessToken != null ? appUser.userAccessToken.token : "TOKEN_NULL";
            JSONObject msg = SocketMessageHelper.createMessageObject(String.valueOf(i), "testUser", token);

            AppSocketClientManager.getInstance().messageToClient(appUser.id, msg);
        }
    }

    @Test
    public void removeMyUserMS() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        //query2.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        query2.addCriteria(Criteria.where("appUserId1").is(appUser.id));
        mongoTemplate.remove(query2, AppUserMatch.class);
    }

    @Test
    public void addUserMS() throws Exception {

        //List<AppUser> appUserList = generateTestUsers(100, false);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        //query2.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        query2.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        matchController.sendGreet(appUser2.id);

        MockHttpServletRequest requestTestUser = new MockHttpServletRequest();
        requestTestUser.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser2.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestTestUser));

        matchController.sendGreet(appUser.id);
    }

    //TODO : 
    @Test
    public void getMessagesTest() throws Exception {

        List<AppUser> appUserList = generateTestUsers(100, false);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        /*if(appUser == null) {
            appUser = appUserList.get(0);
        }*/

        String appUserId = appUser.id;
        List<String> testingAppUserIds = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            AppUser testAppUser = appUserList.get(i);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            matchController.sendGreet(testAppUser.id);

            MockHttpServletRequest requestTestUser = new MockHttpServletRequest();
            requestTestUser.addHeader(RequestParams.REQUEST_TOKEN_KEY, testAppUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestTestUser));

            matchController.sendGreet(appUser.id);
            testingAppUserIds.add(testAppUser.id);
        }

        for(int i = 0; i < (testingAppUserIds.size() - 4); i++) {
            String testAppUserId = testingAppUserIds.get(i);

            AppUserMessage appUserMessage = new AppUserMessage();
            appUserMessage.createdAt = DateTimeManager.getNow();
            appUserMessage.message = "testMessage " + i + " from " + testAppUserId;
            appUserMessage.fromAppUserId = testAppUserId;
            appUserMessage.toAppUserId = appUser.id;

            messageRepository.save(appUserMessage);
        }

        //
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getUnreadMessages();

        //Assertions
        Assert.notNull(messagesVO);
        Assert.isTrue(messagesVO.success);

        MessageListVO matchGetInfoVOData = (MessageListVO)messagesVO.data;
        Assert.notEmpty(matchGetInfoVOData.messageUsersList);
    }

    @Test
    public void getUserUnreadMessages2Users() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser2.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        for (int i = 0; i < 20; i++) {
            String testMessage = "testMessage" + i;
            messageController.sendMessage(testMessage, appUser.id);
        }

        {
            Query mQuery = new Query();
            mQuery.addCriteria(Criteria.where("toAppUserId").is(appUser.id));
            mQuery.addCriteria(Criteria.where("fromAppUserId").is(appUser2.id));
            mQuery.addCriteria(Criteria.where("isRead").ne(true));
            mQuery = mQuery.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));

            List<AppUserMessage> unreadMessages = mongoTemplate.find(mQuery, AppUserMessage.class);

            Assertions.assertNotEquals(unreadMessages.size(), 0);
        }

        //
        request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getUserUnreadMessages(appUser2.id);

        {
            Query mQuery = new Query();
            mQuery.addCriteria(Criteria.where("toAppUserId").is(appUser.id));
            mQuery.addCriteria(Criteria.where("fromAppUserId").is(appUser2.id));
            mQuery.addCriteria(Criteria.where("isRead").ne(true));
            mQuery = mQuery.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));

            List<AppUserMessage> unreadMessages = mongoTemplate.find(mQuery, AppUserMessage.class);

            Assertions.assertEquals(0, unreadMessages.size());
        }
    }

    @Test
    public void getLatestMessages2User() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser2.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        for (int i = 0; i < 5; i++) {
            String testMessage = "latestTestMessage" + i;
            messageController.sendMessage(testMessage, appUser.id);
        }

        //
        request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getLatestMessages();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(messagesVO);
        System.out.println(jsonString);
    }

    @Autowired
    protected AppUserMessageRepository appUserMessageRepository;

    @Test
    public void getUserMessagesTestOne() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        List<AppUserMessage> messages = appUserMessageRepository.findMessagesRelatedToMeAndUser(appUser2.id, appUser.id, 0, 10);
        List<AppUserMessage> messages2 = appUserMessageRepository.findMessagesRelatedToMeAndUser(appUser2.id, appUser.id, 10, 10);

        for(int i = 0; i < messages.size(); i++) {
            AppUserMessage aum = messages.get(i);

            for(int mi = 0; mi < messages2.size(); mi++) {
                AppUserMessage mim = messages2.get(i);
                if(aum.id.equals(mim.id)) {
                    System.out.println("mim.id" + mim.id + " | messages.index : " + i + " | messages2.index : " + mi);
                }
            }
        }

        System.out.println("ENDED!");
    }

    @Test
    public void getUserMessages2UserOne() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser2.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO1 = messageController.getUserMessages(1, appUser.id);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(messagesVO1);
        System.out.println(jsonString);

        ServiceResponseVO messagesVO2 = messageController.getUserMessages(2, appUser.id);
        ObjectMapper mapper2 = new ObjectMapper();
        String jsonString2 = mapper2.writeValueAsString(messagesVO2);
        System.out.println(jsonString2);

        ServiceResponseVO messagesVO3 = messageController.getUserMessages(3, appUser.id);
        ObjectMapper mapper3 = new ObjectMapper();
        String jsonString3 = mapper3.writeValueAsString(messagesVO3);
        System.out.println(jsonString3);
    }

    @Test
    public void getUserMessages2Users() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser2.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        for (int i = 0; i < 20; i++) {
            String testMessage = "testMessage" + i;
            messageController.sendMessage(testMessage, appUser.id);
        }

        {
            Query mQuery = new Query();
            mQuery.addCriteria(Criteria.where("toAppUserId").is(appUser.id));
            mQuery.addCriteria(Criteria.where("fromAppUserId").is(appUser2.id));
            mQuery.addCriteria(Criteria.where("isRead").ne(true));
            mQuery = mQuery.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));

            List<AppUserMessage> unreadMessages = mongoTemplate.find(mQuery, AppUserMessage.class);

            Assertions.assertNotEquals(unreadMessages.size(), 0);
        }

        //
        request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getUserMessages(1, appUser2.id);

        {
            Query mQuery = new Query();
            mQuery.addCriteria(Criteria.where("toAppUserId").is(appUser.id));
            mQuery.addCriteria(Criteria.where("fromAppUserId").is(appUser2.id));
            mQuery.addCriteria(Criteria.where("isRead").ne(true));
            mQuery = mQuery.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));

            List<AppUserMessage> unreadMessages = mongoTemplate.find(mQuery, AppUserMessage.class);

            Assertions.assertEquals(0, unreadMessages.size());
        }
    }

    @Test
    public void getUserMessagesEncryptedTest() throws Exception {

        //List<AppUser> appUserList = generateTestUsers(1, false);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        List<AppUser> appUserList = new ArrayList<>();
        appUserList.add(appUser2);

        String testMessage = "testMessage1";
        String appUserId = appUser.id;
        List<String> testingAppUserIds = new ArrayList<>();
        for (int i = 0; i < appUserList.size(); i++) {
            AppUser testAppUser = appUserList.get(i);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            matchController.sendGreet(testAppUser.id);

            MockHttpServletRequest requestTestUser = new MockHttpServletRequest();
            requestTestUser.addHeader(RequestParams.REQUEST_TOKEN_KEY, testAppUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestTestUser));


            matchController.sendGreet(appUser.id);
            messageController.sendMessage(testMessage, appUser.id);

            testingAppUserIds.add(testAppUser.id);
            //testMessage += i;
        }

        //
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getUserMessages(1, testingAppUserIds.get(0));

        //Assertions
        Assert.notNull(messagesVO);
        Assert.isTrue(messagesVO.success);

        MessageListVO matchGetInfoVOData = (MessageListVO)messagesVO.data;
        Assert.notEmpty(matchGetInfoVOData.messageUsersList);
        //Assert.notEmpty(matchGetInfoVOData.messageUsersList);

        //
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(matchGetInfoVOData);
        System.out.println(jsonString);
    }

    @Test
    public void getUserMessages() throws Exception {

        List<AppUser> appUserList = generateTestUsers(100, false);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        String appUserId = appUser.id;
        List<String> testingAppUserIds = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            AppUser testAppUser = appUserList.get(i);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            matchController.sendGreet(testAppUser.id);

            MockHttpServletRequest requestTestUser = new MockHttpServletRequest();
            requestTestUser.addHeader(RequestParams.REQUEST_TOKEN_KEY, testAppUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestTestUser));

            matchController.sendGreet(appUser.id);
            testingAppUserIds.add(testAppUser.id);
        }

        for(int i = 0; i < (testingAppUserIds.size() - 4); i++) {
            String testAppUserId = testingAppUserIds.get(i);

            AppUserMessage appUserMessage = new AppUserMessage();
            appUserMessage.createdAt = DateTimeManager.getNow();
            appUserMessage.message = "testMessage " + i + " from " + testAppUserId;
            appUserMessage.fromAppUserId = testAppUserId;
            appUserMessage.toAppUserId = appUser.id;
            appUserMessage.isRead = (i % 2) == 0;

            messageRepository.save(appUserMessage);
        }

        //
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getUserMessages(1, testingAppUserIds.get(0));

        //Assertions
        Assert.notNull(messagesVO);
        Assert.isTrue(messagesVO.success);

        MessageListVO matchGetInfoVOData = (MessageListVO)messagesVO.data;
        Assert.notEmpty(matchGetInfoVOData.messageUsersList);

        //
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(matchGetInfoVOData.messageUsersList);
        System.out.println(jsonString);
    }

    @Test
    public void getUserUnreadMessages() throws Exception {

        List<AppUser> appUserList = generateTestUsers(100, false);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        String appUserId = appUser.id;
        List<String> testingAppUserIds = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            AppUser testAppUser = appUserList.get(i);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            matchController.sendGreet(testAppUser.id);

            MockHttpServletRequest requestTestUser = new MockHttpServletRequest();
            requestTestUser.addHeader(RequestParams.REQUEST_TOKEN_KEY, testAppUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestTestUser));

            matchController.sendGreet(appUser.id);
            testingAppUserIds.add(testAppUser.id);
        }

        for(int i = 0; i < (testingAppUserIds.size() - 4); i++) {
            String testAppUserId = testingAppUserIds.get(i);

            AppUserMessage appUserMessage = new AppUserMessage();
            appUserMessage.createdAt = DateTimeManager.getNow();
            appUserMessage.message = "testMessage " + i + " from " + testAppUserId;
            appUserMessage.fromAppUserId = testAppUserId;
            appUserMessage.toAppUserId = appUser.id;
            //appUserMessage.isRead = (i % 2) == 0;
            //appUserMessage.isRead = false;

            messageRepository.save(appUserMessage);
        }

        //
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getUserUnreadMessages(testingAppUserIds.get(0));

        //Assertions
        Assert.notNull(messagesVO);
        Assert.isTrue(messagesVO.success);

        MessageListVO matchGetInfoVOData = (MessageListVO)messagesVO.data;
        Assert.notEmpty(matchGetInfoVOData.messageUsersList);

        //
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(matchGetInfoVOData.messageUsersList);
        System.out.println(jsonString);
    }

    @Test
    public void getMessagesForAllTest() throws Exception {

        List<AppUser> appUserList = generateTestUsers(100, false);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        String appUserId = appUser.id;
        List<String> testingAppUserIds = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            AppUser testAppUser = appUserList.get(i);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            matchController.sendGreet(testAppUser.id);

            MockHttpServletRequest requestTestUser = new MockHttpServletRequest();
            requestTestUser.addHeader(RequestParams.REQUEST_TOKEN_KEY, testAppUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestTestUser));

            matchController.sendGreet(appUser.id);
            testingAppUserIds.add(testAppUser.id);
        }

        for(int i = 0; i < (testingAppUserIds.size() - 4); i++) {
            String testAppUserId = testingAppUserIds.get(i);

            AppUserMessage appUserMessage = new AppUserMessage();
            appUserMessage.createdAt = DateTimeManager.getNow();
            appUserMessage.message = "testMessage " + i + " from " + testAppUserId;
            appUserMessage.fromAppUserId = testAppUserId;
            appUserMessage.toAppUserId = appUser.id;
            appUserMessage.isRead = (i % 2) == 0;

            messageRepository.save(appUserMessage);
        }

        //
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messagesVO = messageController.getLatestMessages();

        //Assertions
        Assert.notNull(messagesVO);
        Assert.isTrue(messagesVO.success);

        MessageListVO matchGetInfoVOData = (MessageListVO)messagesVO.data;
        Assert.notEmpty(matchGetInfoVOData.messageUsersList);

        //
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(matchGetInfoVOData);
        System.out.println(jsonString);
    }

    @Test
    public void fillFriendMessages() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO friendsServiceResponseVO = messageController.getFriends();
        FriendsVO friendsVO = (FriendsVO)friendsServiceResponseVO.data;

        int iterator = 0;
        for(MatchUserVO muvo : friendsVO.matchUsers) {
            String testAppUserId = muvo.id;

            if(iterator >= (friendsVO.matchUsers.size() / 2)) {
                continue;
            }

            AppUserMessage appUserMessage = new AppUserMessage();
            appUserMessage.createdAt = DateTimeManager.getNow();
            appUserMessage.message = "testMessage " + iterator + " from " + testAppUserId + " 1 2 3 aaa bbbb ccccc dddddd";
            appUserMessage.fromAppUserId = testAppUserId;
            appUserMessage.toAppUserId = appUser.id;
            appUserMessage.isRead = false;

            messageRepository.save(appUserMessage);
            iterator++;
        }
    }

    @Test
    public void sendVoiceTest() throws Exception {

        //Required
        File file = new File("/Users/Cag/Downloads/peritune-spook4 (1).mp3");
        org.springframework.util.Assert.notNull(file);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        org.springframework.util.Assert.notNull(appUser);

        Query queryToTestUser = new Query();
        queryToTestUser.addCriteria(Criteria.where("email").is("e.reyge@gmail.com"));
        AppUser testUser = mongoTemplate.findOne(queryToTestUser, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "audio/mpeg", IOUtils.toByteArray(input));
        //

        ServiceResponseVO resultVO = messageController.sendVoice(multipartFile, testUser.id);
        String messageId = (String)resultVO.data;

        Assertions.assertNotNull(messageId);

        AppUserMessage aum = messageRepository.findById(messageId);
        Assertions.assertEquals(aum.messageType, MessageType.VOICE_MESSAGE.getTypeName());
        Assertions.assertNotNull(aum.fileUrl);
        Assertions.assertNull(aum.message);

        System.out.println("fileUrl : " + aum.fileUrl);
    }

    @Test
    public void deleteMessagesTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query queryToTestUser = new Query();
        queryToTestUser.addCriteria(Criteria.where("email").is("e.reyge@gmail.com"));
        AppUser testUser = mongoTemplate.findOne(queryToTestUser, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messageResponseVO = messageController.sendMessage("testMessage", testUser.id);
        String messageId = (String)messageResponseVO.data;

        Assertions.assertNotNull(messageId);

        ServiceResponseVO messageResponseVO2 = messageController.sendMessage("testMessage2", testUser.id);
        String messageId2 = (String)messageResponseVO2.data;

        Assertions.assertNotNull(messageId2);

        String ids = messageId + "," + messageId2;

        messageController.removeMessages(ids);

        AppUserMessage testMess1 = mongoTemplate.findById(messageId, AppUserMessage.class);
        AppUserMessage testMess2 = mongoTemplate.findById(messageId2, AppUserMessage.class);

        Assertions.assertNull(testMess1);
        Assertions.assertNull(testMess2);
    }

    @Test
    public void deleteMessageTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query queryToTestUser = new Query();
        queryToTestUser.addCriteria(Criteria.where("email").is("e.reyge@gmail.com"));
        AppUser testUser = mongoTemplate.findOne(queryToTestUser, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messageResponseVO = messageController.sendMessage("testMessage", testUser.id);
        String messageId = (String)messageResponseVO.data;

        Assertions.assertNotNull(messageId);

        messageController.removeMessages(messageId);

        AppUserMessage testMess1 = mongoTemplate.findById(messageId, AppUserMessage.class);

        Assertions.assertNull(testMess1);
    }

    @Test
    public void deleteMessagesByUserIdTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query queryToTestUser = new Query();
        queryToTestUser.addCriteria(Criteria.where("email").is("e.reyge@gmail.com"));
        AppUser testUser = mongoTemplate.findOne(queryToTestUser, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO messageResponseVO = messageController.sendMessage("testMessage", testUser.id);
        String messageId = (String)messageResponseVO.data;

        Assertions.assertNotNull(messageId);
        messageController.removeUserMessages(testUser.id);

        AppUserMessage testMess1 = mongoTemplate.findById(messageId, AppUserMessage.class);
        Assertions.assertNull(testMess1);
    }

    @Test
    public void markAsReadByLatestTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        List<String> messageIds = new ArrayList<>();
        {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser2.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            for(int i = 0; i < 10; i++) {
                ServiceResponseVO respV = messageController.sendMessage("test" + i, appUser.id);
                String messageId = (String)respV.data;
                messageIds.add(messageId);
            }
        }

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String latestId = messageIds.get(messageIds.size() - 1);
        ServiceResponseVO srv = messageController.markAsReadByLatest(latestId);


        Query mQuery = new Query();
        mQuery.addCriteria(Criteria.where("toAppUserId").is(appUser.id));
        mQuery.addCriteria(Criteria.where("id").is(latestId));

        AppUserMessage latestMessage =  mongoTemplate.findOne(mQuery, AppUserMessage.class);

        mQuery.addCriteria(Criteria.where("fromAppUserId").is(latestMessage.fromAppUserId));
        mQuery.addCriteria(Criteria.where("createdAt").lte(latestMessage.createdAt));
        mQuery.addCriteria(Criteria.where("isRead").ne(true));

        List<AppUserMessage> checkMessages =  mongoTemplate.find(mQuery, AppUserMessage.class);

        Assertions.assertTrue((checkMessages == null || checkMessages.size() < 1));
    }
}
