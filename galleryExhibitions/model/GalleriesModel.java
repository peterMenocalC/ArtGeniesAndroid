package com.art.genies.galleryExhibitions.model;

import android.content.Context;

import com.art.genies.App;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.pojo.gallery.GalleryData;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GalleriesModel extends BaseDataModel {

    private BaseDataModel.CallBack<GalleryData> mCallback;

    public GalleriesModel(BaseDataModel.CallBack<GalleryData> callBack, Context context) {
        super(context);
        mCallback = callBack;
    }

    public void loadGallery() {
        App.getApi().getGalleries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<GalleryData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(GalleryData galleryData) {
                        mCallback.onDataLoaded(galleryData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCallback.onError(e);
                    }
                });
    }

}
