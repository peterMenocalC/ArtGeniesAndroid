package com.art.genies.galleryExhibitions.pojo;

import com.art.genies.galleryExhibitions.pojo.exhibitions.Exhibitions;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GalleryExhibitionNearBy {
    @SerializedName("response_code")
    private int responseCode;
    @SerializedName("response_message")
    private String responseMessage;
    @SerializedName("data")
    public NearByData data;

    public class NearByData {
        @SerializedName("galleries")
        public List<Gallery> galleries;
        @SerializedName("exhibitions")
        public List<Exhibitions> exhibitions;
    }
}
