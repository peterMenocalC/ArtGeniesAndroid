package com.art.genies.galleryExhibitions.discover;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.R;
import com.art.genies.apis.ICallBack;
import com.art.genies.apis.response.Timing;
import com.art.genies.common.GlideApp;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.art.genies.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.MyViewHolder> {

    private List<Gallery> mGalleryList;
    private ICallBack mICallBack;

    public GalleryListAdapter(List<Gallery> galleries, ICallBack iCallBack) {
        mGalleryList = galleries;
        mICallBack = iCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Gallery gallery = mGalleryList.get(position);
        Date currentTime = Calendar.getInstance().getTime();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime);
        String hour = (String) DateFormat.format("h", currentTime);
        String min = (String) DateFormat.format("mm", currentTime);
        int hourMin = Integer.parseInt(hour + min);
        GlideApp.with(holder.imageView.getContext())
                .asBitmap()
                .dontAnimate()
                .load(gallery.full_path)
                .into(holder.imageView);
        holder.galleyName.setText(Utils.getNameLanguage(gallery.name));
        if (gallery.timing != null && !gallery.timing.isEmpty()) {
            if(gallery.timing.contains(dayOfTheWeek)){
                for (Timing timing : gallery.timing) {
                    if (timing.day_of_week.equalsIgnoreCase(dayOfTheWeek)) {
                        if (hourMin > Integer.parseInt(timing.closing) || hourMin < Integer.parseInt(timing.opening)) {
                            holder.txtOpenClose.setText(R.string.closeNow);
                            holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_red));
                        } else {
                            holder.txtOpenClose.setText(R.string.openNow);
                            holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_green));
                        }
                    }
                }
            } else {
                holder.txtOpenClose.setText(R.string.closeNow);
                holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_red));
            }

        } else {
            holder.txtOpenClose.setVisibility(View.GONE);
        }
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.zoom_in));
        holder.itemView.setOnClickListener(view -> mICallBack.run(gallery._id));
    }

    @Override
    public int getItemCount() {
        return mGalleryList.size();
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
