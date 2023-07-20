package org.derdoapp.SocketManager;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class AppSocketClientManager {

    private static AppSocketClientManager instance;
    private ConcurrentHashMap<String, Socket> socketClients;

    private AppSocketClientManager() {
        socketClients = new ConcurrentHashMap<>();
    }

    public static AppSocketClientManager getInstance() {
        if(instance == null) {
            instance = new AppSocketClientManager();
        }

        return instance;
    }

    public void openClientSocket(String clientId, Socket clientSocket) throws IOException {
        if(socketClients.containsKey(clientId)) {
            Socket closeSocket = socketClients.get(clientId);
            if(closeSocket != null) {
                closeSocket.close();
                closeSocket = null;
            }
        }

        socketClients.put(clientId, clientSocket);
    }

    /*public void openClientSocket(String clientId) throws IOException {
        Socket clientSocket = null;
        if(socketClients.containsKey(clientId)) {
            clientSocket = socketClients.get(clientId);
            if(clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
        }

        clientSocket = AppSocketServer.getInstance().getServer().accept();
        socketClients.put(clientId, clientSocket);
    }*/

    public void closeClientSocket(String clientId) throws IOException {
        if(socketClients.containsKey(clientId)) {
            Socket clientSocket = socketClients.get(clientId);
            if(clientSocket != null) {
                clientSocket.close();
                clientSocket = socketClients.remove(clientId);
                if(clientSocket != null) {
                    clientSocket = null;
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

    public Socket getClient(String clientId) {
        if(socketClients.containsKey(clientId)) {
            Socket socket = socketClients.get(clientId);
            if(socket == null) {
                return null;
            }

            if(socket.isClosed() || !socket.isConnected()) {
                System.out.println("RemovingClientFromSocket.isClosed : " + clientId);
                socketClients.remove(clientId);
                return null;
            }

            return socket;
        }

        return null;
    }

    public Boolean messageToClient(String clientId, JSONObject messageObject) {
        try {
            Socket socket = getClient(clientId);
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

    public void pingToClients() {
        Enumeration<String> keys = socketClients.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            JSONObject pingObj = new JSONObject();
            pingObj.put("ping", key);

            messageToClient(key, pingObj);
        }
    }

    /*private void listenClient(Socket clientSocket) throws IOException {
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String message = in.readLine();
        System.out.println("message : " + message);
    }*/

    /*public void listenAllTest() throws IOException {
        System.out.println("listenAllTest");
        ArrayList<String> keys = Collections.list(socketClients.keys());
        for(int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            System.out.println("listenAllTest.key : " + key);
            listenClient(socketClients.get(key));
        }
    }*/
}
