package org.derdoapp.DataModel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="appUserMatchPot")
public class AppUserMatchPot {

    @Id
    public String id;

    public String appUserId1;

    public String appUserId2;

}
