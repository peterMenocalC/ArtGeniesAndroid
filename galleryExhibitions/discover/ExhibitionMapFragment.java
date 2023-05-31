package com.art.genies.galleryExhibitions.discover;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.BaseFragment;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.apis.response.Index;
import com.art.genies.apis.response.InternalMap;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.model.ExhibitionsDetailsModel;
import com.art.genies.galleryExhibitions.pojo.exhibitions.ExhibitionData;
import com.art.genies.utils.Ui;

import io.blushine.android.ui.showcase.MaterialShowcaseView;
import java.util.ArrayList;
import java.util.List;

public class ExhibitionMapFragment extends BaseFragment implements BaseDataModel.CallBack<ExhibitionData> {
    private RecyclerView exhibitionMapRecyclerView;
    private AppCompatImageView search;
    private ImageView imgBack;
    private ExhibitionMapAdapter exhibitionMapAdapter;
    private List<InternalMap> mInternalMap = new ArrayList<>();
    private List<Index> mIndexList = new ArrayList<>();
    private ExhibitionsDetailsModel mExhibitionsDetailsModel;
    private String mId;
    private RelativeLayout progressLayout;

    public ExhibitionMapFragment(FragmentCallback callback, String id) {
        super(callback);
        mId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exhibitions_map_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        exhibitionMapRecyclerView = view.findViewById(R.id.galleryRecyclerView);
        progressLayout = view.findViewById(R.id.progressLayout);

        search = view.findViewById(R.id.search);
        search.setOnClickListener(view12 -> {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            mIndexList.clear();
            if (!mInternalMap.isEmpty()) {
                Log.d("<<<","Size"+mInternalMap.get(position).index);
                mIndexList.addAll(mInternalMap.get(position).index);
            }
            Log.d("<<<","Pos"+position);
            DialogFragment dialogFragment = new IndexDialogFragment(mIndexList);
            dialogFragment.show(ft, "dialog");
        });
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view1 -> {
            try {
               callback.backPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        exhibitionMapAdapter = new ExhibitionMapAdapter(mInternalMap);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        exhibitionMapRecyclerView.setLayoutManager(linearLayoutManager);
        exhibitionMapRecyclerView.setAdapter(exhibitionMapAdapter);
        /*exhibitionMapRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                Log.d("<<<","Pos"+position);
                *//*mIndexList.clear();
                if (!mInternalMap.isEmpty()) {
                    mIndexList.addAll(mInternalMap.get(position).index);
                }*//*
            }
        });*/

        exhibitionMapRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    position = getCurrentItem();
                    //onPageChanged(position);
                }
            }
        });
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(exhibitionMapRecyclerView);


        mExhibitionsDetailsModel = new ExhibitionsDetailsModel(this, getActivity());
        Ui.showSpinner(progressLayout);
        mExhibitionsDetailsModel.loadData(mId);
        exhibitionMapShowCase();
    }

    int position;

    @Override
    public void onDataLoaded(ExhibitionData data) {
        if (!data.data.internalMap.isEmpty()) {
            mInternalMap.addAll(data.data.internalMap);
            exhibitionMapAdapter.notifyDataSetChanged();
            mIndexList.addAll(mInternalMap.get(0).index);
        }
        mExhibitionsDetailsModel.clean();
        Ui.hideSpinner(progressLayout);
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getActivity(), mExhibitionsDetailsModel.handle(throwable), Toast.LENGTH_SHORT).show();
        mExhibitionsDetailsModel.clean();
        Ui.hideSpinner(progressLayout);
    }
    private void exhibitionMapShowCase() {
        new MaterialShowcaseView.Builder(getActivity())
            .setTarget(search)
            .setTitleText(getString(R.string.find))
            .setDismissText(getString(R.string.done))
            .setContentText(getString(R.string.find_on_map))
            .setDelay(0) // optional but starting animations immediately in onCreate can make them choppy
            .setSingleUse(ExhibitionMapFragment.class.getName()) // provide a unique ID used to ensure it is only shown once
            .show();
    }


    //test implementation
    public boolean hasPreview() {
        return getCurrentItem() > 0;
    }

    public boolean hasNext() {
        return exhibitionMapRecyclerView.getAdapter() != null &&
                getCurrentItem() < (exhibitionMapRecyclerView.getAdapter().getItemCount()- 1);
    }

    public void preview() {
        int position = getCurrentItem();
        if (position > 0)
            setCurrentItem(position -1, true);
    }

    public void next() {
        RecyclerView.Adapter adapter = exhibitionMapRecyclerView.getAdapter();
        if (adapter == null)
            return;

        int position = getCurrentItem();
        int count = adapter.getItemCount();
        if (position < (count -1))
            setCurrentItem(position + 1, true);
    }

    private int getCurrentItem(){
        return ((LinearLayoutManager)exhibitionMapRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    private void setCurrentItem(int position, boolean smooth){
        if (smooth)
            exhibitionMapRecyclerView.smoothScrollToPosition(position);
        else
            exhibitionMapRecyclerView.scrollToPosition(position);
    }
}
