package org.derdoapp.DerdoAppServer;

import io.jsonwebtoken.lang.Assert;
import org.derdoapp.Controller.MatchController;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.AppUserMatch;
import org.derdoapp.DataModel.AppUserMatchStatus;
import org.derdoapp.DataModel.RequestParams;
import org.derdoapp.Repository.AppUserMatchRepository;
import org.derdoapp.VO.MatchGetInfoVO;
import org.derdoapp.VO.MatchUserVO;
import org.derdoapp.VO.SendInteractionVO;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class MatchControllerTests extends BaseControllerTest {

    @Autowired
    private MatchController matchController;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppUserMatchRepository matchRepository;

    @Test
    public void removeTestUsersNGen() throws Exception {

        //generateTestUsers(1000, false);
        removeTestUsersOnDB();
        //generateTestUsers(0, true);

    }

    @Test
    public void addUserMS1() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        //

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO serviceResponseVO = matchController.sendGreet(appUser2.id);
    }

    @Test
    public void addUserMS() throws Exception {

        //List<AppUser> appUserList = generateTestUsers(100, false);
        //List<AppUser> appUserList =

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser2 = mongoTemplate.findOne(query2, AppUser.class);

        List<AppUser> appUserList = new ArrayList<>();
        appUserList.add(appUser2);

        if(appUser == null) {
            appUser = appUserList.get(0);
        }

        List<String> testingAppUserIds = new ArrayList<>();
        for (int i = 0; i < appUserList.size(); i++) {
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
            Assertions.assertEquals(true, serviceResponseVO.success);
            SendInteractionVO sendInteractionVO = (SendInteractionVO)serviceResponseVO.data;
            Assertions.assertNotNull(sendInteractionVO);

            ServiceResponseVO matchesInfoVO = matchController.getWaiting();
            Assertions.assertNotNull(matchesInfoVO);
            Assertions.assertEquals(true, matchesInfoVO.success);

            MatchGetInfoVO mgivo = (MatchGetInfoVO) matchesInfoVO.data;
            Assertions.assertNotNull(mgivo.matchUsers);

            List<String> waitingUserIds = mgivo.matchUsers.stream().map(x -> x.id).collect(Collectors.toList());
            Assertions.assertEquals(false, waitingUserIds.contains(testUserIdToGreet));

            for (int i = 0; i < waitingUserIds.size(); i++) {
                String s = waitingUserIds.get(i);
                Assertions.assertEquals(true, testingAppUserIds.contains(s));
            }
        }
    }

    @Test
    public void getWaitingTest() throws Exception {

        List<AppUser> appUserList = generateTestUsers(100, false);

        Query query = new Query();
        //query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        //query.addCriteria(Criteria.where("email").is("derdoapp@gmail.com"));
        query.addCriteria(Criteria.where("email").is("tugrayazbahar@gmail.com"));
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
            Assertions.assertEquals(true, serviceResponseVO.success);
            SendInteractionVO sendInteractionVO = (SendInteractionVO)serviceResponseVO.data;
            Assertions.assertNotNull(sendInteractionVO);

            ServiceResponseVO matchesInfoVO = matchController.getWaiting();
            Assertions.assertNotNull(matchesInfoVO);
            Assertions.assertEquals(true, matchesInfoVO.success);

            MatchGetInfoVO mgivo = (MatchGetInfoVO) matchesInfoVO.data;
            Assertions.assertNotNull(mgivo.matchUsers);

            List<String> waitingUserIds = mgivo.matchUsers.stream().map(x -> x.id).collect(Collectors.toList());
            Assertions.assertEquals(false, waitingUserIds.contains(testUserIdToGreet));

            for (int i = 0; i < waitingUserIds.size(); i++) {
                String s = waitingUserIds.get(i);
                Assertions.assertEquals(true, testingAppUserIds.contains(s));
            }
        }
    }

    @Test
    public void matchGetNextTest() throws Exception {

        generateTestUsers();

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO matchesInfoVO = matchController.getNext(10);

        //Assertions
        Assert.notNull(matchesInfoVO);
        Assert.isTrue(matchesInfoVO.success);

        MatchGetInfoVO matchGetInfoVOData = (MatchGetInfoVO)matchesInfoVO.data;
        Assert.notEmpty(matchGetInfoVOData.matchUsers);
        Assertions.assertEquals(10, matchGetInfoVOData.matchUsers.size());

        MatchUserVO matchUserVO = matchGetInfoVOData.matchUsers.get(0);
        matchController.sendGreet(matchUserVO.id);

        ServiceResponseVO matchesInfoVO2 = matchController.getNext(10);

        MatchGetInfoVO matchGetInfoVOData2 = (MatchGetInfoVO)matchesInfoVO2.data;
        Assert.notEmpty(matchGetInfoVOData2.matchUsers);
        Assertions.assertEquals(10, matchGetInfoVOData2.matchUsers.size());

        MatchUserVO matchUserVO2 = matchGetInfoVOData2.matchUsers.get(0);

        Assertions.assertNotEquals(matchUserVO.id, matchUserVO2.id);
    }

    @Test
    public void matchesTestFirst() throws Exception {

        generateTestUsers();

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("test@test0.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO matchesInfoVO = matchController.getNext(10);

        //Assertions
        Assert.notNull(matchesInfoVO);
        Assert.isTrue(matchesInfoVO.success);

        MatchGetInfoVO matchGetInfoVOData = (MatchGetInfoVO)matchesInfoVO.data;
        Assert.notEmpty(matchGetInfoVOData.matchUsers);

        Assertions.assertEquals(10, matchGetInfoVOData.matchUsers.size());
    }

    @Test
    public void matchNotFirstTest() throws Exception {

        generateTestUsers();

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        if(appUser == null) {
            query = new Query();
            query.addCriteria(Criteria.where("email").is("test@test0.com"));
            appUser = mongoTemplate.findOne(query, AppUser.class);
        }

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO matchesInfoVO = matchController.getNext(10);

        //Assertions
        Assert.notNull(matchesInfoVO);
        Assert.isTrue(matchesInfoVO.success);

        MatchGetInfoVO matchGetInfoVOData = (MatchGetInfoVO)matchesInfoVO.data;
        Assert.notEmpty(matchGetInfoVOData.matchUsers);

        Assertions.assertEquals(1, matchGetInfoVOData.matchUsers.size());
        MatchUserVO muvo = matchGetInfoVOData.matchUsers.get(0);

        Query testMatchUserQuery = new Query();
        testMatchUserQuery.addCriteria(Criteria.where("id").is(muvo.id));
        AppUser testMatchUser = mongoTemplate.findOne(testMatchUserQuery, AppUser.class);

        Assertions.assertEquals(appUser.appUserSettings.gender, testMatchUser.gender);
        Assertions.assertEquals(appUser.appUserSettings.nuisanceTypeCode, testMatchUser.appUserSettings.nuisanceTypeCode);
    }

    @Test
    public void matchSendMultipleGreetsTest() throws Exception {

        //generateTestUsers(20);

        List<AppUser> appUserList = generateTestUsers(100, false);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        for (int i = 0; i < appUserList.size(); i++) {
            AppUser appUserToGreet = appUserList.get(i);
            ServiceResponseVO matchesInfoVO = matchController.sendGreet(appUserToGreet.id);

            Assert.notNull(matchesInfoVO);
            Assert.isTrue(matchesInfoVO.success);

            SendInteractionVO siv = (SendInteractionVO)matchesInfoVO.data;

            System.out.println("siv.isSubReq : " + siv.isSubscrptionRequired + " | count : " + siv.greetingsLeft);
        }

        List<AppUserMatch> matches = matchRepository.getPassedUsers(appUser.id);
        Assert.notNull(matches);
        Assert.notEmpty(matches);

        System.out.println("matches.size : " + matches.size());
    }

    @Test
    public void matchSendGreetTest() throws Exception {

        //generateTestUsers(20);

        Query query = new Query();
        //query.addCriteria(Criteria.where("email").is("test@test0.com"));
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Query q2 = new Query();
        q2.addCriteria(Criteria.where("email").is("tekinbelek@gmail.com"));
        AppUser appUserToGreet = mongoTemplate.findOne(q2, AppUser.class);

        ServiceResponseVO matchesInfoVO = matchController.sendGreet(appUserToGreet.id);

        //Assertions
        Assert.notNull(matchesInfoVO);
        Assert.isTrue(matchesInfoVO.success);

        List<AppUserMatch> matches = matchRepository.getPassedUsers(appUser.id);
        Assert.notNull(matches);
        Assert.notEmpty(matches);

        Collections.sort(matches, new Comparator<AppUserMatch>() {
            public int compare(AppUserMatch o1, AppUserMatch o2) {
                return o2.createdAt.compareTo(o1.createdAt);
            }
        });

        AppUserMatch testMatch = matches.get(0);
        Assertions.assertEquals(testMatch.appUserId1, appUser.id);
        Assertions.assertEquals(testMatch.appUserId2, appUserToGreet.id);
        Assertions.assertEquals(testMatch.matchStatus, AppUserMatchStatus.ACCEPT);
    }

    @Test
    public void matchSendRejectTest() throws Exception {

        generateTestUsers(20);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("test@test0.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Query q2 = new Query();
        q2.addCriteria(Criteria.where("email").is("test@test1.com"));
        AppUser appUserToGreet = mongoTemplate.findOne(q2, AppUser.class);

        ServiceResponseVO matchesInfoVO = matchController.sendReject(appUserToGreet.id);

        //Assertions
        Assert.notNull(matchesInfoVO);
        Assert.isTrue(matchesInfoVO.success);

        List<AppUserMatch> matches = matchRepository.getPassedUsers(appUser.id);
        Assert.notNull(matches);
        Assert.notEmpty(matches);

        Collections.sort(matches, new Comparator<AppUserMatch>() {
            public int compare(AppUserMatch o1, AppUserMatch o2) {
                return o2.createdAt.compareTo(o1.createdAt);
            }
        });

        AppUserMatch testMatch = matches.get(0);
        Assertions.assertEquals(testMatch.appUserId1, appUser.id);
        Assertions.assertEquals(testMatch.appUserId2, appUserToGreet.id);
        Assertions.assertEquals(testMatch.matchStatus, AppUserMatchStatus.REJECT);
    }
}
