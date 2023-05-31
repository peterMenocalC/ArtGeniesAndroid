package com.art.genies.galleryExhibitions.discover;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.apis.response.Timing;
import com.art.genies.common.Constants;
import com.art.genies.common.GlideApp;
import com.art.genies.galleryExhibitions.pojo.exhibitions.Exhibitions;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.art.genies.utils.Utils;
import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GalleryExhibitionAdapter extends RecyclerView.Adapter<GalleryExhibitionAdapter.MyViewHolder> {

    private List<Gallery> mGalleryList;
    private List<Exhibitions> mExhibitions;
    private FragmentCallback mCallBack;

    public GalleryExhibitionAdapter(List<Gallery> galleries, List<Exhibitions> exhibitionsList, FragmentCallback callback) {
        mGalleryList = galleries;
        mCallBack = callback;
        mExhibitions = exhibitionsList;
    }

    @NonNull
    @Override
    public GalleryExhibitionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryExhibitionAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryExhibitionAdapter.MyViewHolder holder, int position) {
        Date currentTime = Calendar.getInstance().getTime();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime);
        String hour = (String) DateFormat.format("h", currentTime);
        String min = (String) DateFormat.format("mm", currentTime);
        int hourMin = Integer.parseInt(hour + min);
        if (!mGalleryList.isEmpty()) {
            Gallery gallery = mGalleryList.get(position);
            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(holder.imageView.getContext());
            progressDrawable.setStrokeWidth(5f);
            progressDrawable.setCenterRadius(30f);
            progressDrawable.setColorFilter(ContextCompat.getColor(holder.imageView.getContext(), R.color.blue_read_more), PorterDuff.Mode.SRC_IN);
            progressDrawable.start();
            GlideApp.with(holder.imageView.getContext())
                    .load(gallery.thumb_path)
                    .placeholder(progressDrawable)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                            Target<Drawable> target, boolean isFirstResource) {
                            progressDrawable.stop();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                            Target<Drawable> target, DataSource dataSource,
                            boolean isFirstResource) {
                            progressDrawable.stop();
                            return false;
                        }
                    })
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
            holder.itemView.setOnClickListener(view -> mCallBack.changeFragment(Constants.GALLERY_DETAILS, gallery._id, null));
        }

        if (!mExhibitions.isEmpty()) {
            Exhibitions gallery = mExhibitions.get(position);
            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(holder.imageView.getContext());
            progressDrawable.setStrokeWidth(5f);
            progressDrawable.setCenterRadius(30f);
            progressDrawable.setColorFilter(
                ContextCompat.getColor(holder.imageView.getContext(), R.color.blue_read_more), PorterDuff.Mode.SRC_IN);
            progressDrawable.start();
            GlideApp.with(holder.imageView.getContext())
                    .load(gallery.thumb_path)
                    .placeholder(progressDrawable)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                            Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                            Target<Drawable> target, DataSource dataSource,
                            boolean isFirstResource) {
                            progressDrawable.stop();
                            return false;
                        }
                    })
                    .into(holder.imageView);
            holder.galleyName.setText(Utils.getNameLanguage(gallery.name));
            long tsLong = System.currentTimeMillis();
            if (tsLong < gallery.end_date && tsLong > gallery.start_date) {
                holder.txtOpenClose.setText(R.string.ongoing);
                holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_green));
            } else if (tsLong <  gallery.start_date)  {
                holder.txtOpenClose.setText(R.string.upcoming);
                holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_green));
            } else {
                holder.txtOpenClose.setText(R.string.completed);
                holder.txtOpenClose.setTextColor(holder.itemView.getResources().getColor(R.color.color_red));
            }
            holder.itemView.setOnClickListener(view -> mCallBack.changeFragment(Constants.EXHIBITION_DETAILS, gallery._id, null));
        }
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.zoom_in));
    }

    @Override
    public int getItemCount() {
        if (!mGalleryList.isEmpty()) {
            return mGalleryList.size();
        }
        if (!mExhibitions.isEmpty()) {
            return mExhibitions.size();
        }
        return 0;
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView galleyName, txtOpenClose;

         MyViewHolder(@NonNull View itemView) {
            super(itemView);
            galleyName = itemView.findViewById(R.id.galleyName);
            txtOpenClose = itemView.findViewById(R.id.txtOpenClose);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
