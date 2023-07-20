package org.derdoapp.SocketManagerChatRoom;

import org.derdoapp.DataModel.AppUser;
import org.derdoapp.Repository.AppUserRepository;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

//@Component
public class ChatRoomSocketServer {

    //@Autowired
    protected AppUserRepository appUserRepository;

    private int socketPort = 7272;
    private ServerSocket serverSocket;

    public void startServer() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(socketPort);

        while(true) {

            Socket socket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            try {
                String tokenNUserId = in.readLine();
                //tokenNUserId = URLEncoder.encode(tokenNUserId, StandardCharsets.UTF_8.toString());
                System.out.println("incoming.params : " + tokenNUserId);

                String keyToken = "token=";

                int subStart = tokenNUserId.indexOf(keyToken) + keyToken.length();
                String params = tokenNUserId.substring(subStart);
                System.out.println("params.firstcut : " + params);

                int subEnd = params.indexOf("&");
                String token = params.substring(0, subEnd);
                //

                String keyUserId = "userId=";
                int subStart2 = tokenNUserId.indexOf(keyUserId) + keyUserId.length();
                String params2 = tokenNUserId.substring(subStart2);
                System.out.println("params.secut : " + params2);

                int subEnd2 = params2.indexOf("&");
                String toUserId = params2.substring(0, subEnd2);

                //String seperator = "#";
                //String[] tokenNUserIdArr = params.split(seperator);
                //String token = tokenNUserIdArr[0];

                /*if(tokenNUserId == null || tokenNUserId.equals("")) {
                    System.out.println("incoming.refused.params : " + tokenNUserId);
                    continue;
                }

                //JSON PARAMS
                JSONObject jsonObject = new JSONObject(tokenNUserId);
                String paramsString = jsonObject.getString("params");

                String[] tokenNUserIdArr = paramsString.split(" ");
                String token = tokenNUserIdArr[0];
                System.out.println("incoming.fetch.token : " + token);*/

                //RAW STRING
                /*
                String[] tokenNUserIdArr = tokenNUserId.split(" ");
                String token = tokenNUserIdArr[0];
                System.out.println("incoming.fetch.token : " + token);
                */

                if(token == null || token.equals("")) {
                    System.out.println("incoming.token : " + token);
                    continue;
                }

                AppUser appUser = appUserRepository.findByAccessToken(token);
                if (appUser == null) {
                    System.out.println("incoming.refused.token : " + token);
                    continue;
                }

                //String toUserId = tokenNUserIdArr[1];
                System.out.println("incoming.accepted.token : " + token + " | clientId : " + appUser.id + " | toUserId : " + toUserId);
                ChatRoomClientManager.getInstance().openClientSocket(appUser.id, toUserId, socket);

                JSONObject okMessage = new JSONObject();
                okMessage.put("connection", "ok");
                ChatRoomClientManager.getInstance().messageToClient(appUser.id, toUserId, okMessage);
            }
            catch (Exception ex) {
                System.out.println("AppSocketServer.Ex : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
            }
        }
    }

}
