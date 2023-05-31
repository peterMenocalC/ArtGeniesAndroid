package com.art.genies.galleryExhibitions.pojo.gallery;

import com.art.genies.apis.response.Bio;
import com.art.genies.apis.response.Location;
import com.art.genies.apis.response.Name;
import com.art.genies.apis.response.Timing;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Gallery {
    @SerializedName("name")
    public Name name;
    @SerializedName("bio")
    public Bio bio;
    @SerializedName("_id")
    public String _id;
    @SerializedName("email")
    public String email;
    @SerializedName("phone")
    public String phone;
    @SerializedName("weblink")
    public String weblink;
    @SerializedName("videolink")
    public String videolink;
    @SerializedName("organisation")
    public String organisation;
    @SerializedName("full_path")
    public String full_path;
    @SerializedName("thumb_path")
    public String thumb_path;
    @SerializedName("owner")
    public List<Owner> owner;
    @SerializedName("location")
    public Location location;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("updatedAt")
    public String updatedAt;
    @SerializedName("artist")
    public List<Artist> artist;
    @SerializedName("full_address")
    public String full_address;
    @SerializedName("timing")
    public List<Timing> timing;
    @SerializedName("id")
    public String id;
}
