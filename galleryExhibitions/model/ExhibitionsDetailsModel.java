package com.art.genies.galleryExhibitions.model;

import android.content.Context;

import com.art.genies.App;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.pojo.exhibitions.ExhibitionData;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExhibitionsDetailsModel extends BaseDataModel {
    private BaseDataModel.CallBack<ExhibitionData> mCallback;

    public ExhibitionsDetailsModel(BaseDataModel.CallBack<ExhibitionData> callback, Context context) {
        super(context);
        mCallback = callback;
    }

    public void loadData(String exhibitionsID) {
        App.getApi().getExhibition(exhibitionsID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<ExhibitionData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(ExhibitionData exhibitionData) {
                        mCallback.onDataLoaded(exhibitionData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCallback.onError(e);
                    }
                });
    }
}
