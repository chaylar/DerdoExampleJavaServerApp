package org.derdoapp.DerdoAppServer;

import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.RequestParams;
import org.derdoapp.Repository.AppUserMatchRepository;
import org.derdoapp.VO.UserMessageVO;
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
public class ChatControllerTests extends BaseControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppUserMatchRepository matchRepository;

    @Test
    public void sendMessageTest() throws Exception {

        AppUser testAppUser = generateTestUsers(1).get(0);

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser toMessageAppUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, testAppUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UserMessageVO umvo = new UserMessageVO();
        umvo.content = "testMessage";
        umvo.toUser = toMessageAppUser.id;
        //umvo.fromUserToken = testAppUser.userAccessToken.token;

        //chatController.send(umvo);
    }

}
