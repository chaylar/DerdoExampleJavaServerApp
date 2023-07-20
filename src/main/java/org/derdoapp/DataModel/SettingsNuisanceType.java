package org.derdoapp.DataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SettingsNuisanceType {

    UNEMPLOYMENT(1, "İşsizlik"),
    JOB_RELATED(2, "İş Hayatı"),
    LOVE_RELATED(3, "Gönül İşleri"),
    SICKNESS(4, "Aile"),
    EXAM_STRESS(5, "Okul Hayatı"),
    OTHER(6, "Depresyon", true),
    ;

    private int nuisanceTypeCode;
    private String nuisanceTypeName;
    private Boolean isDefaultSelected;

    public int getNuisanceTypeCode() {
        return nuisanceTypeCode;
    }

    public String getNuisanceTypeName() {
        return nuisanceTypeName;
    }

    public Boolean getDefaultSelected() {
        return isDefaultSelected;
    }

    SettingsNuisanceType(int typeCode, String typeName, Boolean isDefaultSelected) {
        this.nuisanceTypeCode = typeCode;
        this.nuisanceTypeName = typeName;
        this.isDefaultSelected = isDefaultSelected;
    }

    SettingsNuisanceType(int typeCode, String typeName) {
        this.nuisanceTypeCode = typeCode;
        this.nuisanceTypeName = typeName;
        this.isDefaultSelected = false;
    }

    public static SettingsNuisanceType getByTypeCode(int typeCode) {
        List<SettingsNuisanceType> nuisanceList = getValues();
        for (int i = 0; i < nuisanceList.size(); i++) {
            SettingsNuisanceType sgt = nuisanceList.get(i);
            if(sgt.nuisanceTypeCode == typeCode) {
                return sgt;
            }
        }

        return null;
    }

    public static List<SettingsNuisanceType> getValues() {
        return Arrays.asList(SettingsNuisanceType.values());
    }

    public static SettingsNuisanceType getDefault() {
        SettingsNuisanceType[] nTypes = SettingsNuisanceType.values();
        for (int i = 0; i < nTypes.length; i++) {
            SettingsNuisanceType snt = nTypes[i];
            if(snt.isDefaultSelected) {
                return snt;
            }
        }

        return null;
    }

    public static List<NuisanceType> getAsNuisanceTypes() {
        List<SettingsNuisanceType> valuesList = getValues();
        List<NuisanceType> resultList = new ArrayList<>();
        for (int i = 0; i < valuesList.size(); i++) {
            SettingsNuisanceType snt = valuesList.get(i);
            NuisanceType nt = new NuisanceType(snt.nuisanceTypeCode, snt.nuisanceTypeName);
            resultList.add(nt);
        }

        return resultList;
    }
}
