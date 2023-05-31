package com.art.genies.galleryExhibitions.discover;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.art.genies.R;
import com.art.genies.apis.response.Program;
import com.art.genies.common.GlideApp;
import com.art.genies.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.MyViewHolder> {

    private List<Program> mPrograms;

    public ProgramsAdapter(List<Program> programs) {
        mPrograms = programs;
    }

    @NonNull
    @Override
    public ProgramsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProgramsAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exhibition_program_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramsAdapter.MyViewHolder holder, int position) {
        Program program = mPrograms.get(position);
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(holder.imageView.getContext());
        progressDrawable.setStrokeWidth(5f);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.setColorFilter(ContextCompat.getColor(holder.imageView.getContext(), R.color.blue_read_more), PorterDuff.Mode.SRC_IN);
        progressDrawable.start();
        GlideApp.with(holder.imageView.getContext())
                .asBitmap()
                .placeholder(progressDrawable)
                .dontAnimate()
                .load(program.full_path)
                .into(holder.imageView);
        holder.galleyName.setText(Utils.getNameLanguage(program.name));
        long tsLong = System.currentTimeMillis();
        if (tsLong < program.end_time && tsLong > program.start_time) {
            holder.txtOpenClose.setText(R.string.ongoing);
            holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_green));
        } else if (tsLong <  program.start_time)  {
            holder.txtOpenClose.setText(R.string.upcoming);
            holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_green));
        } else {
            holder.txtOpenClose.setText(R.string.completed);
            holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_red));
        }
        //holder.txtOpenClose.setText("Open Now");
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.zoom_in));
    }

    @Override
    public int getItemCount() {
        return mPrograms.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView galleyName, txtOpenClose;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            galleyName = itemView.findViewById(R.id.galleyName);
            txtOpenClose = itemView.findViewById(R.id.txtOpenClose);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}

