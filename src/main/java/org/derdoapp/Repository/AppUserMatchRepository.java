package org.derdoapp.Repository;

import org.derdoapp.DataManager.DateTimeManager;
import org.derdoapp.DataModel.AppUserMatch;
import org.derdoapp.DataModel.AppUserMatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AppUserMatchRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<AppUserMatch> getAcceptedMe(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId2").is(appUserId));
        query.addCriteria(Criteria.where("matchStatus").is(AppUserMatchStatus.ACCEPT));

        return mongoTemplate.find(query, AppUserMatch.class);
    }

    public AppUserMatch getAcceptedMe(String myUserId, String appUserId2) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId2").is(myUserId));
        query.addCriteria(Criteria.where("appUserId1").is(appUserId2));
        query.addCriteria(Criteria.where("matchStatus").is(AppUserMatchStatus.ACCEPT));

        return mongoTemplate.findOne(query, AppUserMatch.class);
    }

    public AppUserMatch getRelation(String myUserId, String appUserId2) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId2").is(myUserId));
        query.addCriteria(Criteria.where("appUserId1").is(appUserId2));
        query.addCriteria(Criteria.where("matchStatus").is(AppUserMatchStatus.ACCEPT));

        return mongoTemplate.findOne(query, AppUserMatch.class);
    }

    public List<AppUserMatch> getAcceptedByMe(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId1").is(appUserId));
        query.addCriteria(Criteria.where("matchStatus").is(AppUserMatchStatus.ACCEPT));

        return mongoTemplate.find(query, AppUserMatch.class);
    }

    public int getGreetingsFromDate(String appUserId, LocalDateTime fromDate, List<String> excludeIds) {

        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId1").is(appUserId));
        query.addCriteria(Criteria.where("createdAt").gte(fromDate));
        query.addCriteria(Criteria.where("matchStatus").is(AppUserMatchStatus.ACCEPT));

        query.addCriteria(Criteria.where("appUserId2").nin(excludeIds));

        return (int)mongoTemplate.count(query, AppUserMatch.class);
    }

    public int getGreetingsFromDate(String appUserId, LocalDateTime fromDate) {

        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId1").is(appUserId));
        query.addCriteria(Criteria.where("createdAt").gte(fromDate));
        query.addCriteria(Criteria.where("matchStatus").is(AppUserMatchStatus.ACCEPT));

        //My Responses not counting!
        query.addCriteria(Criteria.where("isGreetRespond").ne(true));
        //

        return (int)mongoTemplate.count(query, AppUserMatch.class);
    }

    public int getAcceptedFromDate(String appUserId, LocalDateTime fromDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId2").is(appUserId));
        query.addCriteria(Criteria.where("matchStatus").is(AppUserMatchStatus.ACCEPT));
        query.addCriteria(Criteria.where("createdAt").is(fromDate));

        return (int)mongoTemplate.count(query, AppUserMatch.class);
    }

    public List<AppUserMatch> getPassedUsers(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId1").is(appUserId));

        return mongoTemplate.find(query, AppUserMatch.class);
    }

    public AppUserMatch sendGreet(String appUserId1, String appUserId2, int myNuisanceTypeCode, Boolean isGreetResponse) {

        AppUserMatch appUserMatch = new AppUserMatch();
        appUserMatch.appUserId1 = appUserId1;
        appUserMatch.appUserId2 = appUserId2;
        appUserMatch.nuisanceTypeCode = myNuisanceTypeCode;
        appUserMatch.matchStatus = AppUserMatchStatus.ACCEPT;
        appUserMatch.isGreetRespond = isGreetResponse;
        appUserMatch.createdAt = DateTimeManager.getNow();

        return mongoTemplate.save(appUserMatch);
    }

    public AppUserMatch sendReject(String appUserId1, String appUserId2) {

        AppUserMatch appUserMatch = new AppUserMatch();
        appUserMatch.appUserId1 = appUserId1;
        appUserMatch.appUserId2 = appUserId2;
        appUserMatch.matchStatus = AppUserMatchStatus.REJECT;
        appUserMatch.createdAt = DateTimeManager.getNow();

        return mongoTemplate.save(appUserMatch);
    }

    public AppUserMatch updateMatch(AppUserMatch appUserMatch) {
        return mongoTemplate.save(appUserMatch);
    }
}
