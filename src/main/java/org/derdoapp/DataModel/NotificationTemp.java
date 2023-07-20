package org.derdoapp.DataModel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="notificationTempHolder")
public class NotificationTemp {

    @Id
    public String id;

    public String appUserId;

    public String notificationType;

}
