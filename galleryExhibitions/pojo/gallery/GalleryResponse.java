package com.art.genies.galleryExhibitions.pojo.gallery;

import com.google.gson.annotations.SerializedName;

public class GalleryResponse {
    @SerializedName("response_code")
    private int responseCode;
    @SerializedName("response_message")
    private String responseMessage;
    @SerializedName("data")
    private GalleryData data;


    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public GalleryData getData() {
        return data;
    }
}
