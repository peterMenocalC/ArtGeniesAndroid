package com.art.genies.galleryExhibitions.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.BaseFragment;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.common.BaseDataModel;
import com.art.genies.common.Constants;
import com.art.genies.galleryExhibitions.model.GalleryExhibitionsModel;
import com.art.genies.galleryExhibitions.pojo.exhibitions.Exhibitions;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.art.genies.utils.Ui;

import java.util.ArrayList;
import java.util.List;

public class GalleryExhibitionsListFragment extends BaseFragment implements BaseDataModel.CallBack<GalleryExhibitionsModel.GalleryExhibitions> {
    private LinearLayout mGalleryLayout;
    private LinearLayout mExhibitionLayout;
    private TextView mGallerySeeAllText;
    private TextView mExhibitionsSeeAllText;
    private GalleryExhibitionsModel mGalleryExhibitionsModel;
    private List<Gallery> mGalleries = new ArrayList<>();
    private List<Exhibitions> mExhibitions = new ArrayList<>();
    private GalleryExhibitionAdapter mGalleryAdapter;
    private GalleryExhibitionAdapter mExhibitionAdapter;
    private static final int MAX_ITEMS = 4;
    private RelativeLayout progressLayout;

    public GalleryExhibitionsListFragment(FragmentCallback callback) {
        super(callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exhibition_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mGalleryLayout = view.findViewById(R.id.galleryLayout);
        progressLayout = view.findViewById(R.id.progressLayout);
        mExhibitionLayout = view.findViewById(R.id.exhibitionsLayout);
        mGallerySeeAllText = view.findViewById(R.id.gallerySeeAllText);
        mGallerySeeAllText.setOnClickListener(view12 -> {
            callback.changeFragment(Constants.GALLERY_LIST, "", null);
        });

        RecyclerView mGalleryRecyclerView = view.findViewById(R.id.galleryRecyclerView);
        mExhibitionsSeeAllText = view.findViewById(R.id.exhibitionsSeeAllText);
        mExhibitionsSeeAllText.setOnClickListener(view13 -> {
            callback.changeFragment(Constants.EXHIBITION_LIST, "", null);
        });

        RecyclerView mExhibitionsRecyclerView = view.findViewById(R.id.exhibitionsRecyclerView);
        mGalleryAdapter = new GalleryExhibitionAdapter(mGalleries, new ArrayList<>(), callback);
        mExhibitionAdapter = new GalleryExhibitionAdapter(new ArrayList<>(), mExhibitions, callback);
        view.findViewById(R.id.map).setOnClickListener(view1 -> {
            try {
                callback.backPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        mGalleryRecyclerView.setLayoutManager(linearLayoutManager);
        GridLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 2);
        mExhibitionsRecyclerView.setLayoutManager(linearLayoutManager1);
        mGalleryRecyclerView.setAdapter(mGalleryAdapter);
        mExhibitionsRecyclerView.setAdapter(mExhibitionAdapter);
        mGalleryExhibitionsModel = new GalleryExhibitionsModel(this, getActivity());
        Ui.showSpinner(progressLayout);
        mGalleryExhibitionsModel.loadData();
    }

    @Override
    public void onDataLoaded(GalleryExhibitionsModel.GalleryExhibitions data) {
        mGalleries.addAll(data.galleryData.data);
        mExhibitions.addAll(data.exhibitionsData.data);
        if (mGalleries.isEmpty()) {
            mGalleryLayout.setVisibility(View.GONE);
        } else {
            if (mGalleries.size() > MAX_ITEMS) {
                mGalleries.clear();
                mGalleries.addAll(data.galleryData.data.subList(0, MAX_ITEMS));
                mGallerySeeAllText.setVisibility(View.VISIBLE);
            }
            mGalleryAdapter.notifyDataSetChanged();
        }
        if (mExhibitions.isEmpty()) {
            mExhibitionLayout.setVisibility(View.GONE);
        } else {
            if (mExhibitions.size() > MAX_ITEMS) {
                mExhibitions.clear();
                mExhibitions.addAll(data.exhibitionsData.data.subList(0, MAX_ITEMS));
                mExhibitionsSeeAllText.setVisibility(View.VISIBLE);
            }
            mExhibitionAdapter.notifyDataSetChanged();
        }
        Ui.hideSpinner(progressLayout);
        mGalleryExhibitionsModel.clean();
    }


    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getActivity(), mGalleryExhibitionsModel.handle(throwable), Toast.LENGTH_LONG).show();
        Ui.hideSpinner(progressLayout);
        mGalleryExhibitionsModel.clean();
    }
}
