package org.derdoapp.SocketManagerChatRoom;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomClientManager {

    private static ChatRoomClientManager instance;
    private ConcurrentHashMap<String, ChatRoomSocket> socketClients;

    private ChatRoomClientManager() {
        socketClients = new ConcurrentHashMap<>();
    }

    public static ChatRoomClientManager getInstance() {
        if(instance == null) {
            instance = new ChatRoomClientManager();
        }

        return instance;
    }

    public void openClientSocket(String clientId, String relatedUserId, Socket clientSocket) throws IOException {
        if(socketClients.containsKey(clientId)) {
            ChatRoomSocket closeSocket = socketClients.get(clientId);
            if(closeSocket != null && closeSocket.socket != null) {
                closeSocket.socket.close();
                closeSocket = null;
            }
        }

        ChatRoomSocket crs = new ChatRoomSocket();
        crs.socket = clientSocket;
        crs.toUserId = relatedUserId;

        socketClients.put(clientId, crs);
    }

    public void closeClientSocket(String clientId) throws IOException {
        if(socketClients.containsKey(clientId)) {
            ChatRoomSocket closeSocket = socketClients.get(clientId);
            if(closeSocket != null && closeSocket.socket != null) {
                closeSocket.socket.close();
                closeSocket = socketClients.remove(clientId);
                if(closeSocket != null) {
                    closeSocket = null;
                }
            }
        }
    }

    public void closeAll() throws IOException {
        ArrayList<String> keys = Collections.list(socketClients.keys());
        for(int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            closeClientSocket(key);
        }
    }

    public Socket getClient(String clientId, String relatedUserId) {
        if(socketClients.containsKey(clientId)) {
            ChatRoomSocket crs = socketClients.get(clientId);
            if(crs == null) {
                System.out.println("No Chat Room Client With Id : " + clientId);
                return null;
            }

            if(!crs.toUserId.equals(relatedUserId)) {
                System.out.println("No Room With RelatedId : " + relatedUserId);
                return null;
            }

            Socket socket = crs.socket;
            if(socket.isClosed() || !socket.isConnected()) {
                System.out.println("RemovingClientFromSocket.isClosed : " + clientId);
                socketClients.remove(clientId);
                return null;
            }

            return socket;
        }

        return null;
    }

    public Boolean messageToClient(String clientId, String relatedUserId, JSONObject messageObject) {
        try {
            Socket socket = getClient(clientId, relatedUserId);
            if (socket == null) {
                return false;
            }

            PrintWriter out;
            out = new PrintWriter(socket.getOutputStream(), true);

            //TODO : Add Sender Id to jsonObject
            out.println(messageObject);
            return true;
        }
        catch (Exception ex) {
            System.out.println("AppSocketServer.Error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
        }

        return false;
    }

}
