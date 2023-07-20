package org.derdoapp.DerdoAppServer;

import org.assertj.core.api.Assertions;
import org.derdoapp.Controller.SettingsController;
import org.derdoapp.DataModel.AppUser;
import org.derdoapp.VO.ServiceResponseVO;
import org.derdoapp.VO.SettingsVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

@SpringBootTest
public class SettingsControllerTests {

    @Autowired
    private SettingsController settingsController;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void getSettingsConstantsTest() throws Exception {

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("test@test1.com"));
        AppUser appUser = mongoTemplate.findOne(query, AppUser.class);
        Assert.notNull(appUser);

        ServiceResponseVO responseVO = settingsController.getSettingsConstants();
        Assert.notNull(responseVO);
        Assert.notNull(responseVO.data);

        Assertions.assertThat(responseVO.data.getClass()).isEqualTo(SettingsVO.class);
    }
}
