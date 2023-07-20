package org.derdoapp.DerdoAppServer;

import org.derdoapp.Controller.MatchController;
import org.derdoapp.Controller.MessageController;
import org.derdoapp.DataManager.DateTimeManager;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserMessage;
import org.derdoapp.DataModel.RequestParams;
import org.derdoapp.Repository.AppUserMatchRepository;
import org.derdoapp.Repository.AppUserMessageRepository;
import org.derdoapp.VO.FriendsVO;
import org.derdoapp.VO.MatchGetInfoVO;
import org.derdoapp.VO.MatchUserVO;
import org.derdoapp.VO.ServiceResponseVO;
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

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class HumanTestResourceGenerator extends BaseControllerTest {

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

    @Test
    public void fillResource() throws Exception {
        getWaitingTest();
        fillFriendMessages();
    }

    public void fillFriendMessages() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO friendsServiceResponseVO = messageController.getFriends();
        FriendsVO friendsVO = (FriendsVO) friendsServiceResponseVO.data;

        int iterator = 0;
        for (MatchUserVO muvo : friendsVO.matchUsers) {
            String testAppUserId = muvo.id;

            if (iterator >= (friendsVO.matchUsers.size() / 2)) {
                continue;
            }

            AppUserMessage appUserMessage = new AppUserMessage();
            appUserMessage.createdAt = DateTimeManager.getNow();
            appUserMessage.message = "testMessage " + iterator + " from " + testAppUserId;
            appUserMessage.fromAppUserId = testAppUserId;
            appUserMessage.toAppUserId = appUser.id;
            appUserMessage.isRead = false;

            messageRepository.save(appUserMessage);
            iterator++;
        }
    }

    public void getWaitingTest() throws Exception {

        List<AppUser> appUserList = generateTestUsers(4000);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        if(appUser == null) {
            appUser = appUserList.get(0);
        }

        List<String> testingAppUserIds = new ArrayList<>();
        for (int i = 1; i < 60; i++) {
            AppUser testAppUser = appUserList.get(i);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader(RequestParams.REQUEST_TOKEN_KEY, testAppUser.userAccessToken.token);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            matchController.sendGreet(appUser.id);
            testingAppUserIds.add(testAppUser.id);
        }

        //

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        {
            ServiceResponseVO matchesInfoVO = matchController.getWaiting();
            Assertions.assertNotNull(matchesInfoVO);
            Assertions.assertEquals(true, matchesInfoVO.success);

            MatchGetInfoVO mgivo = (MatchGetInfoVO) matchesInfoVO.data;
            Assertions.assertNotNull(mgivo.matchUsers);

            List<MatchUserVO> waitingUsers = mgivo.matchUsers;
            for (int i = 0; i < waitingUsers.size(); i++) {
                MatchUserVO testMatchUser = waitingUsers.get(i);
                Assertions.assertEquals(true, testingAppUserIds.contains(testMatchUser.id));
            }
        }

        {
            String testUserIdToGreet = testingAppUserIds.get(0);
            ServiceResponseVO serviceResponseVO = matchController.sendGreet(testUserIdToGreet);
        }
    }
}
