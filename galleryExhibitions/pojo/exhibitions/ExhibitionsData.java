package com.art.genies.galleryExhibitions.pojo.exhibitions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExhibitionsData {
    @SerializedName("response_code")
    private int responseCode;
    @SerializedName("response_message")
    private String responseMessage;
    @SerializedName("data")
    public List<Exhibitions> data;
}
