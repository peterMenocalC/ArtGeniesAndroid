package com.art.genies.galleryExhibitions.discover;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.BaseFragment;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.common.BaseDataModel;
import com.art.genies.galleryExhibitions.model.ExhibitionsDetailsModel;
import com.art.genies.galleryExhibitions.pojo.exhibitions.ExhibitionData;
import com.art.genies.utils.Ui;
import com.art.genies.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ExhibitionProgramsFragments extends BaseFragment implements BaseDataModel.CallBack<ExhibitionData> {
    private TextView exhibitionName;
    private String id;
    private List<ProgramType> programTypeList = new ArrayList<>();
    private ProgramsRecyclerAdapter programsRecyclerAdapter;
    private ExhibitionsDetailsModel exhibitionsDetailsModel;
    private RelativeLayout progressLayout;

    public ExhibitionProgramsFragments(String id, FragmentCallback fragmentCallback) {
        super(fragmentCallback);
        this.id = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exhibitions_programs_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        exhibitionName = view.findViewById(R.id.exhibitionName);
        view.findViewById(R.id.imgBack).setOnClickListener(view1 -> {
            try {
                callback.backPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        progressLayout = view.findViewById(R.id.progressLayout);
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        /*programsRecyclerAdapter = new ProgramsRecyclerAdapter(programTypeList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        eventsRecyclerView.setLayoutManager(linearLayoutManager);
        eventsRecyclerView.setAdapter(programsRecyclerAdapter);
        */
        Ui.showSpinner(progressLayout);
        exhibitionsDetailsModel = new ExhibitionsDetailsModel(this, getActivity());
        exhibitionsDetailsModel.loadData(id);
    }

    RecyclerView eventsRecyclerView;
    ProgramsAdapter exhibitionAdapter;
    @Override
    public void onDataLoaded(ExhibitionData data) {
        Log.d("<<<<","onExhibition"+ data.data.programs);
        exhibitionName.setText(Utils.getNameLanguage(data.data.name));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        eventsRecyclerView.setLayoutManager(linearLayoutManager);
        //programTypeList.add(new ProgramType("Event Day - 1", data.data.programs));
        ProgramsAdapter exhibitionAdapter = new ProgramsAdapter(data.data.programs);
        eventsRecyclerView.setAdapter(exhibitionAdapter);
        //exhibitionAdapter.notifyDataSetChanged();
        //programsRecyclerAdapter.notifyDataSetChanged();
        exhibitionsDetailsModel.clean();
        Ui.hideSpinner(progressLayout);
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getActivity(), exhibitionsDetailsModel.handle(throwable), Toast.LENGTH_SHORT).show();
        exhibitionsDetailsModel.clean();
        Ui.hideSpinner(progressLayout);
    }
}
