package com.art.genies.galleryExhibitions.pojo.gallery;

import com.art.genies.apis.response.artist.ArtistCollectionData;

import java.util.List;

public class MergeGalleryData {
    private  SingleGalleryResponse galleryResponse;
    private List<ArtistCollectionData> artistCollection;

    public SingleGalleryResponse getGalleryResponse() {
        return galleryResponse;
    }

    public void setGalleryResponse(SingleGalleryResponse galleryResponse) {
        this.galleryResponse = galleryResponse;
    }

    public List<ArtistCollectionData> getArtistCollection() {
        return artistCollection;
    }

    public void setArtistCollection(List<ArtistCollectionData> artistCollection) {
        this.artistCollection = artistCollection;
    }
}
