package com.art.genies.galleryExhibitions.model;

import android.content.Context;

import com.art.genies.App;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.pojo.GalleryExhibitionNearBy;
import com.art.genies.galleryExhibitions.pojo.NearBy;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GalleryExhibitionsNearByModel extends BaseDataModel {

    private BaseDataModel.CallBack<GalleryExhibitionNearBy> mCallback;

    public GalleryExhibitionsNearByModel(BaseDataModel.CallBack<GalleryExhibitionNearBy> callback, Context context) {
        super(context);
        mCallback = callback;
    }

    public void loadNearBy(NearBy nearBy) {
        App.getApi().getExhibitionGalleryNearBy(nearBy.Long, nearBy.Lat, nearBy.Radius)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<GalleryExhibitionNearBy>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(GalleryExhibitionNearBy galleryExhibitionNearBy) {
                        mCallback.onDataLoaded(galleryExhibitionNearBy);
                    }

                    @Override
                    public void onError(Throwable e) {
                     mCallback.onError(e);
                    }
                });
    }
}
