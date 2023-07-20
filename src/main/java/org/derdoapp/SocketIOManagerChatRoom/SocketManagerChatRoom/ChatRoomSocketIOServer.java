package org.derdoapp.SocketIOManagerChatRoom.SocketManagerChatRoom;

import io.socket.emitter.Emitter;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoSocket;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
public class ChatRoomSocketIOServer {

    private static ChatRoomSocketIOServer instance;
    private EngineIoServer mEngineIoServer = new EngineIoServer();

    public static ChatRoomSocketIOServer getInstance() {
        if(instance == null) {
            instance = new ChatRoomSocketIOServer();
        }

        return instance;
    }

    private ChatRoomSocketIOServer() {
    }

    public void onServletRequest(HttpServletRequest request, HttpServletResponse response, String appUserId, String toUserId) throws Exception {
        System.out.println("ChatRoomSocketIOServer.onRequest");

        mEngineIoServer.handleRequest(request, response);
        mEngineIoServer.on("connection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    System.out.println("mEngineIoServer.on.Connect");
                    EngineIoSocket socket = (EngineIoSocket) args[0];
                    for(int i = 0; i < args.length; i++) {
                        System.out.println("arg[" + i + "] : " + args[i].toString());
                    }

                    JSONObject okMessage = new JSONObject();
                    okMessage.put("connection", "ok_1");
                    socket.send(new Packet<>(Packet.MESSAGE, okMessage.toString()));

                    System.out.println("incoming.accepted | clientId : " + appUserId + " | toUserId : " + toUserId);
                    ChatRoomSocketIOClientManager.getInstance().openClientSocket(appUserId, toUserId, socket);

                    JSONObject ok2Mess = new JSONObject();
                    ok2Mess.put("connection", "ok_2");
                    ChatRoomSocketIOClientManager.getInstance().messageToClient(appUserId, toUserId, ok2Mess);
                }
                catch (Exception ex) {
                    System.out.println("ChatRoomSocketIOServer.on.Connect.Ex : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_NULL");
                }
            }
        });
    }
}
