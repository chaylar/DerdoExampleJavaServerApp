package org.derdoapp.Repository;

import org.derdoapp.DataModel.AppUserMatchPot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class AppUserMatchPotRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<AppUserMatchPot> getSent(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId1").is(appUserId));
        return mongoTemplate.find(query, AppUserMatchPot.class);
    }

    public void deleteByAppUserId(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId1").is(appUserId));
        mongoTemplate.remove(query, AppUserMatchPot.class);
    }

    public AppUserMatchPot sendPot(String appUserId1, String appUserId2) {

        AppUserMatchPot appUserMatchPot = new AppUserMatchPot();
        appUserMatchPot.appUserId1 = appUserId1;
        appUserMatchPot.appUserId2 = appUserId2;

        return mongoTemplate.save(appUserMatchPot);
    }

    public Collection<AppUserMatchPot> sendPots(List<AppUserMatchPot> pots) {
        return mongoTemplate.insertAll(pots);
    }
}
