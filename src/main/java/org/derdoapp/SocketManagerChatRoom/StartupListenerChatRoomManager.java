package org.derdoapp.SocketManagerChatRoom;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

//@Component
public class StartupListenerChatRoomManager implements ApplicationListener<ContextRefreshedEvent> {

    //@Autowired
    //private ChatRoomSocketServer socketServer;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        StartupListenerChatRoomManager.StarterThread sThread = new StartupListenerChatRoomManager.StarterThread();
        sThread.start();
    }

    class StarterThread extends Thread {
        public void run() {
            try {
                System.out.println("StartupListenerChatRoomManager.onApplicationEvent");
                //socketServer.startServer();
            }
            catch (Exception ex) {
                System.out.println("AppSocketServer.Error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
            }
        }
    }

}
