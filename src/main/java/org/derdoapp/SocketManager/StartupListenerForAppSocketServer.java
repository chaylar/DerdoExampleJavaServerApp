package org.derdoapp.SocketManager;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

//@Component
public class StartupListenerForAppSocketServer implements ApplicationListener<ContextRefreshedEvent> {

    //@Autowired
    //private AppSocketServer socketServer;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        StarterThread sThread = new StarterThread();
        sThread.start();
    }

    class StarterThread extends Thread {
        public void run() {
            try {
                System.out.println("onApplicationEvent");
                //socketServer.startServer();
            }
            catch (Exception ex) {
                System.out.println("AppSocketServer.Error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
            }
        }
    }

}
