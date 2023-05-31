package com.art.genies.galleryExhibitions.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.R;
import com.art.genies.apis.response.Program;

import java.util.ArrayList;
import java.util.List;

public class ProgramsRecyclerAdapter extends RecyclerView.Adapter<ProgramsRecyclerAdapter.MyViewHolder> {

    public List<ProgramType> programTypeList;
    private static final int MAX_ITEMS = 4;
    public ProgramsRecyclerAdapter(List<ProgramType> list) {
        programTypeList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exhibitions_programs_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProgramType programType = programTypeList.get(position);
        holder.eventName.setText(programType.type);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.itemView.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.mRecyclerView.setLayoutManager(linearLayoutManager);
        List<Program> programs = new ArrayList<>();
        if (programType.programList.size() > MAX_ITEMS) {
            programs.addAll(programType.programList.subList(0, MAX_ITEMS));
        } else {
            programs.addAll(programType.programList);
        }
        ProgramsAdapter exhibitionAdapter = new ProgramsAdapter(programs);
        holder.mRecyclerView.setAdapter(exhibitionAdapter);
        exhibitionAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return programTypeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView eventName, eventsSeeAllText;
        private RecyclerView mRecyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mRecyclerView = itemView.findViewById(R.id.eventsRecyclerView);
            eventName = itemView.findViewById(R.id.eventName);
            eventsSeeAllText = itemView.findViewById(R.id.eventsSeeAllText);
        }
    }
}
