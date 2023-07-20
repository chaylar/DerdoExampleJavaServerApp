package org.derdoapp.Helper;

import org.derdoapp.DataModel.AppUser;
import org.derdoapp.DataModel.NotificationTemp;
import org.derdoapp.Repository.NotificationTempHolderRepository;

public class NotificationSender extends Thread {

    private NotificationTempHolderRepository notificationTempHolderRepository;
    //
    private NotificationMessage notificationMessage;
    private NotificationHttpHelper.NotificationType notificationType;
    //private String deviceToken;
    private AppUser toAppUser;
    private NotificationHttpHelper notHttpHelper;

    public NotificationSender(NotificationHttpHelper.NotificationType nt, AppUser toAppUser, NotificationTempHolderRepository notificationTempHolderRepository) {        super();

        notHttpHelper = new NotificationHttpHelper();
        NotificationMessage nm = null;
        if(nt == NotificationHttpHelper.NotificationType.MESSAGE) {
            nm = notHttpHelper.generateMessageContent();
        }
        else if(nt == NotificationHttpHelper.NotificationType.GREET) {
            nm = notHttpHelper.generateGreetContent();
        }
        else if(nt == NotificationHttpHelper.NotificationType.RE_GREET) {
            nm = notHttpHelper.generateReGreetContent();
        }

        if(nm != null) {
            this.notificationTempHolderRepository = notificationTempHolderRepository;
            this.notificationType = nt;
            this.notificationMessage = nm;
            //deviceToken = token;
            this.toAppUser = toAppUser;
        }
    }

    public void run() {
        try {
            System.out.println("NotificationSender.StartingThread");

            NotificationTemp nTemp = notificationTempHolderRepository.getByCreads(toAppUser.id, notificationType.name());
            if(nTemp == null && toAppUser.notificationsEnabled) {
                notHttpHelper.sendHttp(toAppUser.deviceId, notificationMessage);
                //TODO : Check for result of notification!
                notificationTempHolderRepository.saveTemp(toAppUser.id, notificationType.name());
            }
            System.out.println("NotificationSender.EndingThread");
        }
        catch (Exception ex) {
            System.out.println("NotificationSender.SenderThread.Error : " + ex.getMessage() != null ? ex.getMessage() : "MESSAGE_WAS_NULL");
        }
    }

}
