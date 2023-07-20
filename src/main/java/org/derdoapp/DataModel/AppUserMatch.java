package org.derdoapp.DataModel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;


@Document(collection="appUserMatch")
public class AppUserMatch {

    @Id
    public String id;

    @Indexed
    public String appUserId1;

    @Indexed
    public String appUserId2;

    public int nuisanceTypeCode;

    public AppUserMatchStatus matchStatus;

    public LocalDateTime createdAt;

    public Boolean isGreetRespond;

    //public List<AppUserMessage> messages;

    public Boolean hasCommuned;

    public Date updatedAt;

    public Boolean isTest;
}
