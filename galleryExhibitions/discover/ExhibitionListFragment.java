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
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.model.ExhibitionsModel;
import com.art.genies.galleryExhibitions.pojo.exhibitions.Exhibitions;
import com.art.genies.galleryExhibitions.pojo.exhibitions.ExhibitionsData;
import com.art.genies.utils.Ui;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;

public class ExhibitionListFragment extends BaseFragment implements BaseDataModel.CallBack<ExhibitionsData> {
    private Action mAction;
    private RecyclerView recyclerView;
    private List<Exhibitions> exhibitionsList;
    private ExhibitionsListAdapter mExhibitionAdapter;
    private ImageView mBack;
    private ExhibitionsModel exhibitionsModel;
    private RelativeLayout progressLayout;


    public ExhibitionListFragment(FragmentCallback callback) {
        super(callback);
        exhibitionsList = new ArrayList<>();
        mExhibitionAdapter = new ExhibitionsListAdapter(exhibitionsList, callback);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exhibition_list_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBack = view.findViewById(R.id.imgBack);
        progressLayout = view.findViewById(R.id.progressLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mExhibitionAdapter);
        mBack.setOnClickListener(view1 -> {
            try {
                callback.backPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        exhibitionsModel = new ExhibitionsModel(this, getActivity());
        Ui.showSpinner(progressLayout);
        exhibitionsModel.loadExhibitions();
    }

    @Override
    public void onDataLoaded(ExhibitionsData data) {
        exhibitionsList.addAll(data.data);
        mExhibitionAdapter.notifyDataSetChanged();
        Ui.hideSpinner(progressLayout);
        exhibitionsModel.clean();
    }

    @Override
    public void onError(Throwable throwable) {
        Ui.hideSpinner(progressLayout);
        exhibitionsModel.clean();
        String errorMsg = exhibitionsModel.handle(throwable);
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
