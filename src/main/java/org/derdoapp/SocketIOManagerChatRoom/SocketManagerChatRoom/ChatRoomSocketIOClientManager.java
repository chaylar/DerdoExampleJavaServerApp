package org.derdoapp.SocketIOManagerChatRoom.SocketManagerChatRoom;

import io.socket.engineio.parser.Packet;
import io.socket.engineio.server.EngineIoSocket;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomSocketIOClientManager {

    private static ChatRoomSocketIOClientManager instance;
    private ConcurrentHashMap<String, ChatRoomIOSocket> socketClients;

    private ChatRoomSocketIOClientManager() {
        socketClients = new ConcurrentHashMap<>();
    }

    public static ChatRoomSocketIOClientManager getInstance() {
        if(instance == null) {
            instance = new ChatRoomSocketIOClientManager();
        }

        return instance;
    }

    public void openClientSocket(String clientId, String relatedUserId, EngineIoSocket clientSocket) throws IOException {
        if(socketClients.containsKey(clientId)) {
            ChatRoomIOSocket closeSocket = socketClients.get(clientId);
            if(closeSocket != null && closeSocket.socket != null) {
                closeSocket.socket.close();
                closeSocket = null;
            }
        }

        ChatRoomIOSocket crs = new ChatRoomIOSocket();
        crs.socket = clientSocket;
        crs.toUserId = relatedUserId;

        socketClients.put(clientId, crs);
    }

    public void closeClientSocket(String clientId) throws IOException {
        if(socketClients.containsKey(clientId)) {
            ChatRoomIOSocket closeSocket = socketClients.get(clientId);
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

    public EngineIoSocket getClient(String clientId, String relatedUserId) {
        if(socketClients.containsKey(clientId)) {
            ChatRoomIOSocket crs = socketClients.get(clientId);
            if(crs == null) {
                System.out.println("No Chat Room Client With Id : " + clientId);
                return null;
            }

            if(!crs.toUserId.equals(relatedUserId)) {
                System.out.println("No Room With RelatedId : " + relatedUserId);
                return null;
            }

            EngineIoSocket socket = crs.socket;
            //TODO :
            /*if(socket.getReadyState() != ReadyState.OPEN && socket.getReadyState() != ReadyState.OPENING) {
                System.out.println("RemovingClientFromSocket.isClosed : " + clientId);
                if(socket != null) {
                    socket.close();
                }

                socketClients.remove(clientId);
                return null;
            }*/

            return socket;
        }

        return null;
    }

    public Boolean messageToClient(String clientId, String relatedUserId, JSONObject messageObject) {
        try {
            EngineIoSocket socket = getClient(clientId, relatedUserId);
            if (socket == null) {
                return false;
            }

            socket.send(new Packet<>(Packet.MESSAGE, messageObject.toString()));
            System.out.println("sent.message : " + messageObject.toString());
            return true;
        }
        catch (Exception ex) {
            System.out.println("AppSocketServer.Error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
        }

        return false;
    }

}
