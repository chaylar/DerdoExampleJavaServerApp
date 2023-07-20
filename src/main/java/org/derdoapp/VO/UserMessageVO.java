package org.derdoapp.VO;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class UserMessageVO implements Message<String> {

    public String content;

    public String toUser;

    public String fromUserToken;//TODO : remove if unnecessary!!!

    @Override
    public String getPayload() {
        return this.content;
    }

    @Override
    public MessageHeaders getHeaders() {
        return null;
    }
}
