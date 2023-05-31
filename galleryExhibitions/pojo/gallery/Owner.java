package com.art.genies.galleryExhibitions.pojo.gallery;

import com.art.genies.apis.response.Name;
import com.google.gson.annotations.SerializedName;

public class Owner {
    @SerializedName("name")
    public Name name;
    @SerializedName("id")
    public String id;
    @SerializedName("thumb_path")
    public String thumb_path;
}
