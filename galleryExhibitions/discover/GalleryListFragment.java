package com.art.genies.galleryExhibitions.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.BaseFragment;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.apis.ICallBack;
import com.art.genies.common.BaseDataModel;
import com.art.genies.common.Constants;
import com.art.genies.galleryExhibitions.model.GalleriesModel;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.art.genies.galleryExhibitions.pojo.gallery.GalleryData;
import com.art.genies.utils.Ui;

import java.util.ArrayList;
import java.util.List;

public class GalleryListFragment extends BaseFragment implements ICallBack, BaseDataModel.CallBack<GalleryData> {
    private RecyclerView recyclerView;
    private List<Gallery> mGalleryList;
    private GalleryListAdapter mGalleryListAdapter;
    private ImageView mBack;
    private RelativeLayout progressLayout;
    private GalleriesModel galleriesModel;

    public GalleryListFragment(FragmentCallback callback) {
        super(callback);
        mGalleryList = new ArrayList<>();
        mGalleryListAdapter = new GalleryListAdapter(mGalleryList, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBack = view.findViewById(R.id.imgBack);
        progressLayout = view.findViewById(R.id.progressLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mGalleryListAdapter);
        mBack.setOnClickListener(view1 -> {
            try {
                callback.backPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loadGallery();
    }

    private void loadGallery() {
        Ui.showSpinner(progressLayout);
        galleriesModel = new GalleriesModel(this, getActivity());
        galleriesModel.loadGallery();
    }

    @Override
    public void run(String id) {
        callback.changeFragment(Constants.GALLERY_DETAILS, id, null);
    }

    @Override
    public void onDataLoaded(GalleryData data) {
        mGalleryList.addAll(data.data);
        mGalleryListAdapter.notifyDataSetChanged();
        Ui.hideSpinner(progressLayout);
    }

    @Override
    public void onError(Throwable throwable) {
        Ui.hideSpinner(progressLayout);
        String errorMsg = galleriesModel.handle(throwable);
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
