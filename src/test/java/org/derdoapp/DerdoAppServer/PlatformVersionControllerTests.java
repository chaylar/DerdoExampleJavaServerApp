package org.derdoapp.DerdoAppServer;

import io.jsonwebtoken.lang.Assert;
import org.derdoapp.Controller.PlatformVersionController;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.RequestParams;
import org.derdoapp.Helper.PlatformVersionHelper;
import org.derdoapp.VO.ServiceResponseVO;
import org.derdoapp.VO.SimpleResultVO;
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
public class PlatformVersionControllerTests extends BaseControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PlatformVersionController platformVersionController;

    @Test
    public void versionCheckerTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("chaylar@gmail.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestParams.REQUEST_TOKEN_KEY, appUser.userAccessToken.token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ServiceResponseVO responseVo = platformVersionController.checkVersion("0.1", "ios");
        Assert.notNull(responseVo);
        Assert.isTrue(responseVo.success);

        SimpleResultVO srvo = (SimpleResultVO)responseVo.data;
        Assert.isTrue(!srvo.value);
        Assert.notNull(srvo.description);
    }

    @Test
    public void versionCheckerHelperTests() {
        Boolean check1 = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion("1.0.1", "1.1");
        Assert.isTrue(!check1);

        check1 = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion("1.2.1", "1.1");
        Assert.isTrue(check1);

        check1 = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion("0.9", "1.1");
        Assert.isTrue(!check1);

        check1 = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion("3", "0.1");
        Assert.isTrue(check1);

        check1 = PlatformVersionHelper.checkFirstIsGreaterEqThanVersion("0.1", "3");
        Assert.isTrue(!check1);
    }


    /*@Test
    public void initFirst() throws Exception {
        initFirsdtPlatformVersions();
    }*/

}
