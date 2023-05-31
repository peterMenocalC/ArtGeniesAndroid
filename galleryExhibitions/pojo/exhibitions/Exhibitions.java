package com.art.genies.galleryExhibitions.pojo.exhibitions;

import com.art.genies.apis.response.Bio;
import com.art.genies.apis.response.InternalMap;
import com.art.genies.apis.response.Location;
import com.art.genies.apis.response.Locations;
import com.art.genies.apis.response.Name;
import com.art.genies.apis.response.Program;
import com.art.genies.apis.response.Timing;
import com.art.genies.galleryExhibitions.pojo.gallery.Artist;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.art.genies.galleryExhibitions.pojo.gallery.Owner;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Exhibitions {
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
    public Owner owner;
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
    @SerializedName("start_date")
    public long start_date;
    @SerializedName("end_date")
    public long end_date;
    @SerializedName("Galleries")
    public List<Gallery>galleries;
    @SerializedName("internalMap")
    public List<InternalMap>internalMap;
    @SerializedName("locations")
    public List<Locations> locations;
    @SerializedName("programs")
    public List<Program>programs;

}
