package com.art.genies.galleryExhibitions.pojo.exhibitions;

import com.google.gson.annotations.SerializedName;

public class ExhibitionData {
    @SerializedName("response_code")
    private int responseCode;
    @SerializedName("response_message")
    private String responseMessage;
    @SerializedName("data")
    public Exhibitions data;
}
