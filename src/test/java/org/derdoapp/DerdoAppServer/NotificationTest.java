package org.derdoapp.DerdoAppServer;

import org.derdoapp.DataModel.AppUser;
import org.derdoapp.Helper.NotificationHttpHelper;
import org.derdoapp.Helper.NotificationMessage;
import org.derdoapp.Repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void sendTest() throws Exception {
        AppUser relatedUser = appUserRepository.findByEmail("tugrayazbahar@gmail.com");

        NotificationHttpHelper notHttpHelper = new NotificationHttpHelper();

        NotificationMessage nm = new NotificationMessage();
        nm.messageContent = "testContent";
        nm.messageContent = "testTitle";

        notHttpHelper.sendHttp(relatedUser.deviceId, nm);

        //NotificationHelper nh = new NotificationHelper(NotificationHelper.NotificationType.MESSAGE, relatedUser.deviceId);
        //nh.sendNotification("testTitle", "testBody", "66db304f61d6732ea29198524783133a5cc3aca505f4b676c12f634fef6ab9ae");
        //nh.sendNotification_ack(relatedUser.deviceId);
        //nh.Send();
    }
}
