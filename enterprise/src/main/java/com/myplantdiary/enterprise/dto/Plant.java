package com.myplantdiary.enterprise.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

public @Data
class Plant {

    @SerializedName("id")
    private String id;
    @SerializedName("genus")
    private String genus;
    @SerializedName("species")
    private String species;
    @SerializedName("cultivar")
    private String cultivar;
    @SerializedName("common")
    private String common;
    @SerializedName("wetSoil")
    private Boolean wetSoil;
    @SerializedName("drySoil")
    private boolean drySoil;
    @SerializedName("rainGarden")
    private Boolean rainGarden;
    @SerializedName("fullSun")
    private Integer fullSun;
    @SerializedName("partShade")
    private Integer partShade;
    @SerializedName("deepShade")
    private Integer deepShade;

    @Override
    public String toString(){
        return genus + " " + species + " " + cultivar + " " + id;
    }
}
