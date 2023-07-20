package org.derdoapp.SocketManager;

import org.derdoapp.DataModel.AppUser;
import org.derdoapp.Repository.AppUserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

//@Component
public class AppSocketServer {

    //@Autowired
    protected AppUserRepository appUserRepository;

    private int socketPort = 6868;
    private ServerSocket serverSocket;

    public void startServer() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(socketPort);

        while(true) {

            Socket socket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            try {
                String token = in.readLine();
                System.out.println("incoming.fetch.token : " + token);
                AppUser appUser = appUserRepository.findByAccessToken(token);
                if (appUser == null) {
                    System.out.println("incoming.refused.token : " + token);
                    continue;
                }

                System.out.println("incoming.accepted.token : " + token + " | clientId : " + appUser.id);
                AppSocketClientManager.getInstance().openClientSocket(appUser.id, socket);
            }
            catch (Exception ex) {
                System.out.println("AppSocketServer.Ex : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
            }
        }
    }
}
