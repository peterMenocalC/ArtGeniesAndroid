package com.art.genies.galleryExhibitions.discover.gallery_art;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.BaseFragment;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.apis.response.artist.ArtistCollectionData;
import com.art.genies.apis.response.artist.ArtistData;
import com.art.genies.apis.response.artist.MergeArtistData;
import com.art.genies.common.BaseDataModel;
import com.art.genies.common.GlideApp;
import com.art.genies.databinding.FragmentArtistDetailBinding;
import com.art.genies.ui.landing_page.scan.art_details.MySpannable;
import com.art.genies.ui.landing_page.scan.artist_detail.ArtistDetailFragmentViewModel;
import com.art.genies.ui.landing_page.scan.artist_detail.ArtistWorkAdapter;
import com.art.genies.utils.Ui;
import com.art.genies.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

public class GalleryArtistDetailFragment extends BaseFragment implements BaseDataModel.CallBack<MergeArtistData> {
    private FragmentArtistDetailBinding binding;
    private ArtistDetailFragmentViewModel viewModel;
    private RelativeLayout progressLayout;
    private String mId;

    public GalleryArtistDetailFragment(FragmentCallback callback, String id) {
        super(callback);
        mId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_detail, container, false);
        viewModel = new ArtistDetailFragmentViewModel(this, getActivity());
        binding.imgBack.setOnClickListener(v -> {
            callback.backPressed();
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressLayout = view.findViewById(R.id.progressLayout);
        Ui.showSpinner(progressLayout);
        viewModel.getArtistDetails(mId);
    }

    private void initRecycler(List<ArtistCollectionData> artistCollectionData) {
        ArtistWorkAdapter adapter =
                new ArtistWorkAdapter(artistCollectionData, callback);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataLoaded(MergeArtistData data1) {
        ArtistData data = data1.getArtistData();
        if (data != null) {
            if (!data.getFullPath().isEmpty()) {
                GlideApp.with(binding.imgArtist.getContext())
                        .load(data.getFullPath())
                        .into(binding.imgArtist);
            }
            binding.imgArtist.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));

            binding.txtArtistName.setText(Utils.getNameLanguage(data.getName()));
            if (!Utils.getBioLanguage(data.getBio()).isEmpty()) {
                binding.txtReadMore.setText(Utils.fromHtml(Utils.getBioLanguage(data.getBio())));
                binding.txtReadMore.post(new Runnable() {
                    @Override
                    public void run() {
                        int lineCount = binding.txtReadMore.getLineCount();
                        if (lineCount > 3) {
                            makeTextViewResizable(binding.txtReadMore, 3,
                                    getString(R.string.read_more), true);
                        }
                    }
                });
            }
            else {
                binding.txtReadMore.setVisibility(View.GONE);
            }
        }
        if (data1.getArtistCollectionData()!=null && data1.getArtistCollectionData().size()>0)
        {
            initRecycler(data1.getArtistCollectionData());
        }
        else {
            binding.workByArtistLayout.setVisibility(View.GONE);
        }
        Ui.hideSpinner(progressLayout);
        viewModel.clean();
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false) {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#0895e2"));
                }

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "Read Less", false);
                    } else {
                        makeTextViewResizable(tv, 3, "Read More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getContext(), "" + viewModel.handle(throwable), Toast.LENGTH_SHORT).show();
        Ui.hideSpinner(progressLayout);
        viewModel.clean();
    }
}
