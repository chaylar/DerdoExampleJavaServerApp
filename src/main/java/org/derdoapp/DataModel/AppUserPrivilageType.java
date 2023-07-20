package org.derdoapp.DataModel;

public enum AppUserPrivilageType {

    PREMIUM(2),

    ;

    private int privilageTypeCode;

    AppUserPrivilageType(int typeCode) {
        privilageTypeCode = typeCode;
    }

}
