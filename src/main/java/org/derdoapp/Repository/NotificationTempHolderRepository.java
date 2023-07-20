package org.derdoapp.Repository;

import org.derdoapp.DataModel.NotificationTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationTempHolderRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public NotificationTemp saveTemp(String appUserId, String notificationType) {

        NotificationTemp notificationTemp = new NotificationTemp();
        notificationTemp.appUserId = appUserId;
        notificationTemp.notificationType = notificationType;

        return mongoTemplate.save(notificationTemp);
    }

    public void deleteByAppUserId(String appUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId").is(appUserId));
        mongoTemplate.remove(query, NotificationTemp.class);
    }

    public void deleteByAppUserIdNType(String appUserId, String notificationType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId").is(appUserId));
        query.addCriteria(Criteria.where("notificationType").is(notificationType));
        mongoTemplate.remove(query, NotificationTemp.class);
    }

    public NotificationTemp getByCreads(String appUserId, String messageType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appUserId").is(appUserId));
        query.addCriteria(Criteria.where("notificationType").is(messageType));
        return mongoTemplate.findOne(query, NotificationTemp.class);
    }

}
