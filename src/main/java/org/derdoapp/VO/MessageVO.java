package org.derdoapp.VO;

import org.derdoapp.DataModel.AppUserMessage;
import org.derdoapp.DataModel.MessageType;
import org.derdoapp.Helper.EncryptionHelper;

import java.time.LocalDateTime;

public class MessageVO implements Comparable<MessageVO> {

    public MessageVO(MatchUserVO matchUserVO, AppUserMessage appUserMessage, String myUserId) {
        this.id = appUserMessage.id;
        this.fromUserId = appUserMessage.fromAppUserId;
        this.toUserId = appUserMessage.toAppUserId;
        this.isRead = appUserMessage.isRead;
        this.createdAt = appUserMessage.createdAt;
        this.fileUrl = appUserMessage.fileUrl;
        this.relatedUserInfo = matchUserVO;
        this.isMyMessage = (appUserMessage.fromAppUserId.equals(myUserId));

        if(appUserMessage.isEncrypted) {
            this.message = EncryptionHelper.decrypt(appUserMessage.message);
        }
        else {
            this.message = appUserMessage.message;
        }

        this.messageType = MessageType.getByType(appUserMessage.messageType).getTypeName();
    }

    public MessageVO(AppUserMessage appUserMessage, String myUserId) {
        this.id = appUserMessage.id;
        this.fromUserId = appUserMessage.fromAppUserId;
        this.toUserId = appUserMessage.toAppUserId;
        this.isRead = appUserMessage.isRead;
        this.fileUrl = appUserMessage.fileUrl;
        this.createdAt = appUserMessage.createdAt;

        this.isMyMessage = (appUserMessage.fromAppUserId.equals(myUserId));

        if(appUserMessage.isEncrypted) {
            this.message = EncryptionHelper.decrypt(appUserMessage.message);
        }
        else {
            this.message = appUserMessage.message;
        }

        this.messageType = MessageType.getByType(appUserMessage.messageType).getTypeName();
    }

    public String id;

    public String fromUserId;

    public String toUserId;

    public String message;

    public String fileUrl;

    public String messageType;

    public Boolean isRead;

    public LocalDateTime createdAt;

    public MatchUserVO relatedUserInfo;

    public Boolean isMyMessage;

    @Override
    public int compareTo(MessageVO o) {
        try {
            if (this.createdAt == null) {
                return -1;
            } else if (o.createdAt == null) {
                return 1;
            }

            return createdAt.compareTo(o.createdAt);
        }
        catch (Exception e) {
            System.out.println("MatchUserVO.compareTo.EX : " + e.getMessage() != null ? e.getMessage() : "MESSAGE_WAS_NULL");
        }

        return -1;
    }

}
