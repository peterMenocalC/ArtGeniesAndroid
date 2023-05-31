package com.art.genies.galleryExhibitions.model;

import android.content.Context;

import com.art.genies.App;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.pojo.exhibitions.ExhibitionsData;
import com.art.genies.galleryExhibitions.pojo.gallery.GalleryData;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GalleryExhibitionsModel extends BaseDataModel {

    private BaseDataModel.CallBack<GalleryExhibitions> mCallback;

    public GalleryExhibitionsModel(BaseDataModel.CallBack<GalleryExhibitions> callback, Context context) {
        super(context);
        mCallback = callback;
    }


    public class GalleryExhibitions {
        public GalleryData galleryData;
        public ExhibitionsData exhibitionsData;
    }


    public void loadData() {
        Single<GalleryData> getGalleryData = App.getApi().getGalleries();
        Single<ExhibitionsData> getExhibitionData = App.getApi().getExhibitions();

        Single.zip(getGalleryData, getExhibitionData, (galleryData, exhibitionsData) -> {
            GalleryExhibitions galleryExhibitions = new GalleryExhibitions();
            galleryExhibitions.galleryData = galleryData;
            galleryExhibitions.exhibitionsData = exhibitionsData;
            return galleryExhibitions;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<GalleryExhibitions>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                            disposable = d;
                    }

                    @Override
                    public void onSuccess(GalleryExhibitions galleryExhibitions) {
                        mCallback.onDataLoaded(galleryExhibitions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCallback.onError(e);
                    }
                });
    }
}
