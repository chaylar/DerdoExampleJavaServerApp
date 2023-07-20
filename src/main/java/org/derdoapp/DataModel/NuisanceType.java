package org.derdoapp.DataModel;

public class NuisanceType {

    public NuisanceType(int typeCode, String nuisanceName) {
        this.nuisanceName = nuisanceName;
        this.typeCode = typeCode;
    }

    public NuisanceType() {

    }

    public int typeCode;

    public String nuisanceName;

}
