package org.derdoapp.Helper;

import org.json.JSONObject;

public class SocketMessageHelper {

    public static JSONObject createMessageObject(String messageId, String fromUserId, String messageContent) {
        JSONObject result = new JSONObject();
        result.put("id", messageId);
        result.put("fromUserId", fromUserId);
        result.put("content", messageContent);
        result.put("type", SocketMessageType.MESSAGE.getCode());

        return result;
    }

    public static JSONObject createGreetingObject(String fromUserId) {
        JSONObject result = new JSONObject();
        result.put("fromUserId", fromUserId);
        result.put("type", SocketMessageType.GREETING.getCode());

        return result;
    }
}
