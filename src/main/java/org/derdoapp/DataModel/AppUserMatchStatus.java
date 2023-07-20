package org.derdoapp.DataModel;

public enum AppUserMatchStatus {

    //NOTHING(-1),
    ACCEPT(1),
    REJECT(2),
    BLOCK(3),

    //TODO : ?? WILL 'WAITING' BE USED???
    //WAITING(3)
    ;

    private int numVal;

    AppUserMatchStatus(int numVal) {
        this.numVal = numVal;
    }
}
