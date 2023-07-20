package org.derdoapp.Repository;

import org.derdoapp.DataModel.AppUserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AppUserMessageRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    //TODO : Optimize
    public List<AppUserMessage> findUserIdsOfMessages(String appUserId) {

        /*MatchOperation matchStage = Aggregation.match(
                new Criteria("fromAppUserId").is(appUserId)
                        .orOperator(new Criteria("toAppUserId").is(appUserId))
        );

        ProjectionOperation projectStage = Aggregation.project("fromAppUserId", "toAppUserId");

        Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage);
        AggregationResults<AppUserMessage> output = mongoTemplate.aggregate(aggregation, "appUserMessage", AppUserMessage.class);

        return output.getMappedResults();*/

        /*Aggregation agg = new Aggregation(
                project()
                        .andExpression("year(timeCreated)").as("year")
                        .andExpression("month(timeCreated)").as("month")
                        .andExpression("dayOfMonth(timeCreated)").as("day"),
                group(fields().and("year").and("month").and("day"))
                        .sum("blabla").as("blabla")
        );*/

        Query query = new Query();
        query.addCriteria(
                new Criteria().orOperator(
                        Criteria.where("toAppUserId").is(appUserId),
                        Criteria.where("fromAppUserId").is(appUserId)
                )
        );

        return mongoTemplate.find(query, AppUserMessage.class);
    }

    public AppUserMessage findById(String messageId) {
        AppUserMessage result = mongoTemplate.findById(messageId, AppUserMessage.class);
        return result;
    }

    public List<AppUserMessage> findUnreadBySentTo(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toAppUserId").is(appUserId));
        query.addCriteria(Criteria.where("isRead").ne(true));
        return mongoTemplate.find(query, AppUserMessage.class);
    }

    public List<AppUserMessage> findUnreadMessagesFromUser(String meUserId, String appUserId2) {
        Query query = new Query();

        Criteria fromUser = Criteria.where("fromAppUserId").is(appUserId2);
        Criteria toMe = Criteria.where("toAppUserId").is(meUserId);

        Criteria toMeFromUser = new Criteria().andOperator(toMe, fromUser);

        query.addCriteria(toMeFromUser);
        query.addCriteria(Criteria.where("isRead").ne(true));

        query = query.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));
        return mongoTemplate.find(query, AppUserMessage.class);
    }

    public List<AppUserMessage> findMessagesRelatedToMeAndUser(String appUserId, String appUserId2, int start, int limit) {
        Query query = new Query();

        Criteria fromMe = Criteria.where("fromAppUserId").is(appUserId);
        Criteria toUser = Criteria.where("toAppUserId").is(appUserId2);

        Criteria fromUser = Criteria.where("fromAppUserId").is(appUserId2);
        Criteria toMe = Criteria.where("toAppUserId").is(appUserId);

        Criteria toMeFromUser = new Criteria().andOperator(toMe, fromUser);
        Criteria fromMeToUser = new Criteria().andOperator(fromMe, toUser);

        Criteria fullQuery = new Criteria().orOperator(toMeFromUser, fromMeToUser);

        query.addCriteria(fullQuery);
        query = query.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));
        return mongoTemplate.find(query.skip(start).limit(limit), AppUserMessage.class);
    }

    public List<AppUserMessage> findMessagesRelatedToMe(String appUserId) {
        Query query = new Query();

        Criteria toMe = Criteria.where("toAppUserId").is(appUserId);
        Criteria fromMe = Criteria.where("fromAppUserId").is(appUserId);

        Criteria fullQuery = new Criteria().orOperator(toMe, fromMe);

        query.addCriteria(fullQuery);
        query = query.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));
        return mongoTemplate.find(query, AppUserMessage.class);
    }

    public List<AppUserMessage> findMessagesToMe(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toAppUserId").is(appUserId));
        query = query.with(Sort.by(Sort.Direction.DESC, "CreatedAt"));
        return mongoTemplate.find(query, AppUserMessage.class);
    }

    public void deleteByIds(String toUserId, List<String> messageIds) {
        Query query = new Query();

        Criteria toMe = Criteria.where("toAppUserId").is(toUserId);
        Criteria fromMe = Criteria.where("fromAppUserId").is(toUserId);

        Criteria fullQuery = new Criteria().orOperator(toMe, fromMe);
        query.addCriteria(fullQuery);

        query.addCriteria(Criteria.where("id").in(messageIds));
        mongoTemplate.remove(query, AppUserMessage.class);
    }

    public void deleteByUserIds(String myUserId, String toUserId) {
        Query query = new Query();

        Criteria toMe = Criteria.where("toAppUserId").is(myUserId);
        Criteria fromUser = Criteria.where("fromAppUserId").is(toUserId);

        Criteria toUser = Criteria.where("toAppUserId").is(toUserId);
        Criteria fromMe = Criteria.where("fromAppUserId").is(myUserId);

        Criteria fromUserToMe = new Criteria().andOperator(toMe, fromUser);
        Criteria fromMeToUser = new Criteria().andOperator(toUser, fromMe);

        Criteria fullQuery = new Criteria().orOperator(fromUserToMe, fromMeToUser);
        query.addCriteria(fullQuery);

        mongoTemplate.remove(query, AppUserMessage.class);
    }

    public void markAsReadSingular(String toUserId, List<String> messageIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toAppUserId").is(toUserId));
        query.addCriteria(Criteria.where("id").in(messageIds));

        List<AppUserMessage> updateList =  mongoTemplate.find(query, AppUserMessage.class);

        for (AppUserMessage aum : updateList) {
            aum.isRead = true;
            mongoTemplate.save(aum);
        }
    }

    public void markAsReadByLatestDate(String toUserId, String fromUserId, LocalDateTime nowDateTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toAppUserId").is(toUserId));
        query.addCriteria(Criteria.where("fromAppUserId").is(fromUserId));
        query.addCriteria(Criteria.where("createdAt").lte(nowDateTime));
        query.addCriteria(Criteria.where("isRead").ne(true));

        Update update = new Update();
        update.set("isRead", true);

        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AppUserMessage.class);
        bulkOperations.updateMulti(query, update).execute();
    }

    public void markAsReadSingularByLatest(String toUserId, String messageId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toAppUserId").is(toUserId));
        query.addCriteria(Criteria.where("id").is(messageId));

        AppUserMessage latestMessage =  mongoTemplate.findOne(query, AppUserMessage.class);
        if(latestMessage == null) {
            return;
        }

        query.addCriteria(Criteria.where("fromAppUserId").is(latestMessage.fromAppUserId));
        query.addCriteria(Criteria.where("createdAt").lte(latestMessage.createdAt));
        query.addCriteria(Criteria.where("isRead").ne(true));

        Update update = new Update();
        update.set("isRead", true);

        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AppUserMessage.class);
        bulkOperations.updateMulti(query, update).execute();

        //TODO : !
        /*List<AppUserMessage> updateList =  mongoTemplate.find(query, AppUserMessage.class);
        for (AppUserMessage aum : updateList) {
            aum.isRead = true;
            mongoTemplate.save(aum);
        }*/
    }

    public void markAsRead(String toUserId, List<String> messageIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("toAppUserId").is(toUserId));
        query.addCriteria(Criteria.where("id").in(messageIds));

        Update update = new Update();
        update.set("isRead", true);

        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, AppUserMessage.class);
        bulkOperations.updateMulti(query, update).execute();
    }

    public AppUserMessage save(AppUserMessage appUserMessage) {
        return mongoTemplate.save(appUserMessage);
    }
}
