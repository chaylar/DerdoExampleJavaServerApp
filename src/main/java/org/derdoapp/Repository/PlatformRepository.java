package org.derdoapp.Repository;

import org.derdoapp.DataModel.PlatformVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlatformRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PlatformVersion findLastEntryByPlatform(String platform) {
        Query query = new Query();
        query.addCriteria(Criteria.where("platform").is(platform)).with(Sort.by(Sort.Direction.DESC, "id"));
        return mongoTemplate.findOne(query, PlatformVersion.class);
    }

    public List<PlatformVersion> findByPlatform(String platform) {
        Query query = new Query();
        query.addCriteria(Criteria.where("platform").is(platform));
        return mongoTemplate.find(query, PlatformVersion.class);
    }

    public PlatformVersion save(PlatformVersion platformVersion) {
        return mongoTemplate.save(platformVersion);
    }

}
