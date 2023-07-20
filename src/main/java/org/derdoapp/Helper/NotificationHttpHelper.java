package org.derdoapp.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationHttpHelper {

    private static final String AUTH_URL = "https://iid.googleapis.com/iid/v1:batchImport";
    private static final String APP_PACKAGE = "com.beeapplab.derdo";
    private static final String AUTH_KEY = "29HFPUUDJ3";

    //
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String KEY_PREFIX_PART = "key=AAAAuhDCHSk:APA91bEvMov7NI8MIiUCo-e3xlubwZkbxTJhtypEYIBGX4rakZNJZMM_jPee0iAaKbffsOsQxeJ7E7xirqaLOTjXfhVoEUOqCYdul0qMV6HVxxzwNGx22yPGll3AG1ACBSOB4v62oF4e";
    private static final String SENDER_ID = "799145073961";

    private JSONObject authorize(String token) throws Exception {
        URL url = new URL(AUTH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("accept", "application/json");

        connection.setRequestProperty ("Content-Type", "application/json");
        connection.setRequestProperty ("Authorization", KEY_PREFIX_PART);
        connection.setDoOutput(true);

        /*{
          "application": "com.company.app", (YOUR_APP_PACKAGE)
          "sandbox":true,
          "apns_tokens":[
              "7c6811bfa1e89c739c5862122aa7ab68fc4972dea7372242f74276a5326f...."
           ]
         }*/

        JSONArray ja = new JSONArray();
        ja.put(token);

        JSONObject sendData = new JSONObject();
        sendData.put("application", APP_PACKAGE);
        sendData.put("sandbox", false);
        sendData.put("apns_tokens", ja);

        String dataStr = sendData.toString();

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = dataStr.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        StringBuilder response = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            System.out.println("AUTH.RESPONSE : " + response.toString());
        }

        JSONObject result = new JSONObject(response.toString());
        return result;
    }

    public void sendHttp(String token, NotificationMessage notMessage) throws Exception {

        JSONObject authResponse = authorize(token);
        JSONArray results = authResponse.getJSONArray("results");
        JSONObject resultF = results.getJSONObject(0);
        String notificationToToken = resultF.getString("registration_token");

        URL url = new URL(FCM_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("accept", "application/json");

        connection.setRequestProperty ("Content-Type", "application/json");
        connection.setRequestProperty ("Authorization", KEY_PREFIX_PART);
        connection.setDoOutput(true);

        JSONObject dataObj = new JSONObject();
        dataObj.put("title", notMessage.messageTitle);
        dataObj.put("body", notMessage.messageContent);

        JSONObject sendData = new JSONObject();
        sendData.put("priority", "HIGH");
        sendData.put("notification", dataObj);
        sendData.put("to", notificationToToken);

        String dataStr = sendData.toString();

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = dataStr.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            System.out.println("NOTIFICATION.RESPONSE : " + response.toString());
        }
    }

    public class MessageGenerator {

        public static final String GREET_TITLE = "Derdo!";
        public static final String GREET_CONTENT_TEMP = "Sana selam veren birileri var!";
        //
        public static final String MESSAGE_TITLE = "Derdo!";
        public static final String MESSAGE_CONTENT_TEMP = "Yeni mesajın var!";
        //

        public static final String RE_GREET_TITLE = "Derdo!";
        public static final String RE_GREET_CONTENT_TEMP = "Yeni bir derdon var!";
    }

    public enum NotificationType {
        MESSAGE,
        GREET,
        RE_GREET,
    }

    public NotificationMessage generateGreetContent() {
        String title = MessageGenerator.GREET_TITLE;
        //String content = MessageGenerator.GREET_CONTENT_TEMP.replace("{User_Name}", userName);
        String content = MessageGenerator.GREET_CONTENT_TEMP;

        NotificationMessage result = new NotificationMessage();
        result.messageTitle = title;
        result.messageContent = content;

        return result;
    }

    public NotificationMessage generateReGreetContent() {
        String title = MessageGenerator.RE_GREET_TITLE;
        //String content = MessageGenerator.GREET_CONTENT_TEMP.replace("{User_Name}", userName);
        String content = MessageGenerator.RE_GREET_CONTENT_TEMP;

        NotificationMessage result = new NotificationMessage();
        result.messageTitle = title;
        result.messageContent = content;

        return result;
    }

    public NotificationMessage generateMessageContent() {
        String title = MessageGenerator.MESSAGE_TITLE;
        //String content = MessageGenerator.MESSAGE_CONTENT_TEMP.replace("{Message_Content}", messageContent);
        String content = MessageGenerator.MESSAGE_CONTENT_TEMP;

        NotificationMessage result = new NotificationMessage();
        result.messageTitle = title;
        result.messageContent = content;

        return result;
    }

}
