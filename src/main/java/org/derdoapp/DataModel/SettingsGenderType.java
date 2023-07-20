package org.derdoapp.DataModel;

import java.util.Arrays;
import java.util.List;

public enum SettingsGenderType {

    NO_PREFERENCE(1,"no_preference", true),
    FEMALE(2, "female"),
    MALE(3, "male"),
    ;

    private int genderTypeCode;
    private String genderTypeName;
    private Boolean isDefaultSelected;

    public int getGenderTypeCode() {
        return genderTypeCode;
    }

    public String getGenderTypeName() {
        return genderTypeName;
    }

    public Boolean getDefaultSelected() {
        return isDefaultSelected;
    }

    SettingsGenderType(int typeCode, String typeName, Boolean isDefault) {
        this.genderTypeCode = typeCode;
        this.genderTypeName = typeName;
        this.isDefaultSelected = isDefault;
    }

    SettingsGenderType(int typeCode, String typeName) {
        this.genderTypeCode = typeCode;
        this.genderTypeName = typeName;
        this.isDefaultSelected = false;
    }

    public static SettingsGenderType getByTypeName(String typeName) {
        List<SettingsGenderType> gendersList = getValues();
        for (int i = 0; i < gendersList.size(); i++) {
            SettingsGenderType sgt = gendersList.get(i);
            if(sgt.genderTypeName == typeName) {
                return sgt;
            }
        }

        return null;
    }

    public static List<SettingsGenderType> getValues() {
        return Arrays.asList(SettingsGenderType.values());
    }

    public static SettingsGenderType getDefault() {
        SettingsGenderType[] gTypes = SettingsGenderType.values();
        for(int i = 0; i < gTypes.length; i++) {
            if(gTypes[i].isDefaultSelected) {
                return gTypes[i];
            }
        }

        return null;
    }
}
