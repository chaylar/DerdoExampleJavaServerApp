package org.derdoapp.DataModel;

import java.util.Arrays;
import java.util.List;

public enum MessageType {

    TEXT_MESSAGE("text"),
    VOICE_MESSAGE("voice"),
    ;

    private String typeName;

    public String getTypeName() {
        return this.typeName;
    }

    MessageType(String typeName) {
        this.typeName = typeName;
    }

    public static List<MessageType> getValues() {
        return Arrays.asList(MessageType.values());
    }

    public static MessageType getByType(String typeNameSelect) {
        List<MessageType> nuisanceList = getValues();
        for (int i = 0; i < nuisanceList.size(); i++) {
            MessageType sgt = nuisanceList.get(i);
            if(sgt.typeName.equals(typeNameSelect)) {
                return sgt;
            }
        }

        //DEFAULT
        return MessageType.TEXT_MESSAGE;
    }
}
