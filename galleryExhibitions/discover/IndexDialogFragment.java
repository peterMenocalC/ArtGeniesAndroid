package com.art.genies.galleryExhibitions.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.R;
import com.art.genies.apis.response.Index;

import java.util.List;

public class IndexDialogFragment extends DialogFragment {
    private RecyclerView indexRecyclerView;
    private ImageView close;
    private List<Index> mList;
    private IndexAdapter indexAdapter;

    public IndexDialogFragment(List<Index> list) {
        mList = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.index_dialog_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        indexRecyclerView = view.findViewById(R.id.indexRecyclerView);
        indexAdapter = new IndexAdapter(mList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        indexRecyclerView.setLayoutManager(linearLayoutManager);
        indexRecyclerView.setAdapter(indexAdapter);
        close = view.findViewById(R.id.close);
        close.setOnClickListener(view1 -> dismiss());
    }
}
