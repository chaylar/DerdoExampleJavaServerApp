package org.derdoapp.DataModel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection="appUserMessage")
public class AppUserMessage {

    @Id
    public String id;

    public String fromAppUserId;

    public String toAppUserId;

    public String message;

    public String messageType;

    public String fileUrl;

    public Boolean isRead;

    public Boolean isEncrypted;

    public LocalDateTime createdAt;

}
