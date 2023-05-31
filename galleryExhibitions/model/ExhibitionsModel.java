package com.art.genies.galleryExhibitions.model;

import android.content.Context;

import com.art.genies.App;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.pojo.exhibitions.ExhibitionsData;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExhibitionsModel extends BaseDataModel {

    private BaseDataModel.CallBack<ExhibitionsData> mCallback;

    public ExhibitionsModel(BaseDataModel.CallBack<ExhibitionsData> callback, Context context) {
        super(context);
        mCallback = callback;
    }

    public void loadExhibitions() {
        App.getApi().getExhibitions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ExhibitionsData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(ExhibitionsData exhibitionsData) {
                        mCallback.onDataLoaded(exhibitionsData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCallback.equals(e);
                    }
                });
    }
}
