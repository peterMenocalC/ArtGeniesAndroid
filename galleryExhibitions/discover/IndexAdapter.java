package com.art.genies.galleryExhibitions.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.R;
import com.art.genies.apis.response.Description;
import com.art.genies.apis.response.Index;
import com.art.genies.utils.PrefHelper;

import java.util.List;

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.MyViewHolder> {

    private List<Index> nameList;

    public IndexAdapter(List<Index> nameList) {
        this.nameList = nameList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IndexAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.index_dialog_layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (!nameList.isEmpty()) {
            Index index = nameList.get(position);
            Description description = nameList.get(position).description;
            String text = PrefHelper.getLanguage().equals("English") ? description.getEnName() : description.getFrName();
            holder.name.setText(text);
            holder.number.setText(index.series_number);
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
        }
    }
}
