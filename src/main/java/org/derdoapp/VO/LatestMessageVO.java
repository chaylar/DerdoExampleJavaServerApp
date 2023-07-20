package org.derdoapp.VO;

import org.derdoapp.DataModel.AppUserMessage;

public class LatestMessageVO extends MessageVO {

    public long unreadMessageCount;

    public LatestMessageVO(MatchUserVO matchUserVO, AppUserMessage appUserMessage, String myUserId) {
        super(matchUserVO, appUserMessage, myUserId);
    }

    public LatestMessageVO(AppUserMessage appUserMessage, String myUserId) {
        super(appUserMessage, myUserId);
    }

}
