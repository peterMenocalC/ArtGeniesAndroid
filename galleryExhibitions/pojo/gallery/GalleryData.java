package com.art.genies.galleryExhibitions.pojo.gallery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GalleryData {
    @SerializedName("response_code")
    private int responseCode;
    @SerializedName("response_message")
    private String responseMessage;
    @SerializedName("data")
    public List<Gallery> data;
}
