package org.derdoapp.VO;

public class MessageMatchVO {

    public MessageMatchVO(MatchUserVO matchUserVO, MessageVO messageVO) {
        this.matchInfo = matchUserVO;
        this.message = messageVO;
    }

    public MatchUserVO matchInfo;

    public MessageVO message;

}
