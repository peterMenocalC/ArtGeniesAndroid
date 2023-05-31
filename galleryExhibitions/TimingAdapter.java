package com.art.genies.galleryExhibitions;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.R;
import com.art.genies.apis.response.Timing;
import com.art.genies.databinding.ExpandTimingItemBinding;

import java.text.DecimalFormat;
import java.util.List;

public class TimingAdapter extends RecyclerView.Adapter<TimingAdapter.MyViewHolder> {

    private List<Timing> timings;

    public TimingAdapter(List<Timing> timings) {
        this.timings = timings;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.expand_timing_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Timing timing = timings.get(position);
        float opening = Float.parseFloat(timing.opening)/100;
        int openingInt = Integer.parseInt(timing.opening)/100;
        float closing = Float.parseFloat(timing.closing)/100;
        int closingInt = Integer.parseInt(timing.closing)/100;
        holder.binding.dayOfWeak.setText(String.format("%s",timing.day_of_week));
        holder.binding.closing.setText(String.format("%s", closingInt > 12 ? /*Float.valueOf(df.format(closing))*/String.format("%.02f",closing) +" PM" : String.format("%.02f",closing) +" AM"));
        holder.binding.opening.setText(String.format("%s", openingInt > 12 ?  /*Float.valueOf(df.format(opening))*/String.format("%.02f",opening)+ " PM" : String.format("%.02f",opening) + " AM"));
    }

    @Override
    public int getItemCount() {
        return timings.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ExpandTimingItemBinding binding;

        MyViewHolder(ExpandTimingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
