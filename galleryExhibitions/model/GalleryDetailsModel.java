package com.art.genies.galleryExhibitions.model;

import android.content.Context;

import com.art.genies.App;
import com.art.genies.apis.response.artist.ArtistCollection;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.pojo.gallery.MergeGalleryData;
import com.art.genies.galleryExhibitions.pojo.gallery.SingleGalleryResponse;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GalleryDetailsModel extends BaseDataModel {

    private BaseDataModel.CallBack<MergeGalleryData> mCallback;

    public GalleryDetailsModel(BaseDataModel.CallBack<MergeGalleryData> callBack, Context context) {
        super(context);
        mCallback = callBack;
    }

    public void loadDetails(String galleryId) {
        Single.zip(getArts(galleryId), loadGalleryDetails(galleryId), (artistCollection, singleGalleryResponse) -> {
            MergeGalleryData mergeGalleryData = new MergeGalleryData();
            mergeGalleryData.setArtistCollection(artistCollection.getData());
            mergeGalleryData.setGalleryResponse(singleGalleryResponse);
            return mergeGalleryData;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<MergeGalleryData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(MergeGalleryData mergeGalleryData) {
                        mCallback.onDataLoaded(mergeGalleryData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCallback.onError(e);
                    }
                });
    }


    private Single<SingleGalleryResponse> loadGalleryDetails(String galleryId) {
        return App.getApi().getGallery(galleryId);
    }


    private Single<ArtistCollection> getArts(String galleryId) {
        return App.getApi().getGalleryArtistCollection(galleryId);
    }
}
