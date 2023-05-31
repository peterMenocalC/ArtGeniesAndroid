package com.art.genies.galleryExhibitions.discover;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.art.genies.R;
import com.art.genies.apis.response.InternalMap;
import com.art.genies.common.GlideApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ExhibitionMapAdapter extends RecyclerView.Adapter<ExhibitionMapAdapter.MyViewHolder> {
    private List<InternalMap> mInternalMap;
    public ExhibitionMapAdapter(List<InternalMap> internalMap) {
        mInternalMap = internalMap;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exhibition_map_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (!mInternalMap.isEmpty()) {
            InternalMap internalMap = mInternalMap.get(position);
            CircularProgressDrawable progressDrawable = new CircularProgressDrawable(holder.imageView.getContext());
            progressDrawable.setStrokeWidth(5f);
            progressDrawable.setCenterRadius(30f);
            progressDrawable.setColorFilter(ContextCompat.getColor(holder.imageView.getContext(), R.color.blue_read_more), PorterDuff.Mode.SRC_IN);
            progressDrawable.start();
            GlideApp.with(holder.imageView.getContext())
                    .load(internalMap.full_path)
                    .placeholder(progressDrawable)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mInternalMap.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
