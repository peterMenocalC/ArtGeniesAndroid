package com.art.genies.galleryExhibitions.discover;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
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
import com.art.genies.common.Constants;
import com.art.genies.common.GlideApp;
import com.art.genies.galleryExhibitions.pojo.exhibitions.Exhibitions;
import com.art.genies.utils.Utils;
import com.bumptech.glide.Glide;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExhibitionsListAdapter extends RecyclerView.Adapter<ExhibitionsListAdapter.MyViewHolder> {

    private List<Exhibitions> mGalleryList;
    private FragmentCallback mExhibitionDetailsCallback;

    public ExhibitionsListAdapter(List<Exhibitions> galleries, FragmentCallback callback) {
       mGalleryList = galleries;
       mExhibitionDetailsCallback = callback;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Exhibitions gallery = mGalleryList.get(position);
        Date currentTime = Calendar.getInstance().getTime();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime);
        String hour = (String) DateFormat.format("h", currentTime);
        String min = (String) DateFormat.format("mm", currentTime);
        int hourMin = Integer.parseInt(hour + min);
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(holder.imageView.getContext());
        progressDrawable.setStrokeWidth(5f);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.setColorFilter(
            ContextCompat.getColor(holder.imageView.getContext(), R.color.blue_read_more), PorterDuff.Mode.SRC_IN);
        progressDrawable.start();

        GlideApp.with(holder.imageView.getContext())
                .asBitmap()
                .dontAnimate()
                .placeholder(progressDrawable)
                .load(gallery.full_path)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                        Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                        Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
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
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.zoom_in));
        holder.itemView.setOnClickListener(view -> mExhibitionDetailsCallback.changeFragment(Constants.EXHIBITION_DETAILS,gallery._id, null));
    }

    @Override
    public int getItemCount() {
        return mGalleryList.size();
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
