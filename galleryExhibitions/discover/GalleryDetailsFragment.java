package com.art.genies.galleryExhibitions.discover;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.art.genies.BaseFragment;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.apis.response.Timing;
import com.art.genies.apis.response.artist.ArtistCollectionData;
import com.art.genies.common.BaseDataModel;
import com.art.genies.common.GlideApp;
import com.art.genies.galleryExhibitions.TimingAdapter;
import com.art.genies.galleryExhibitions.model.GalleryDetailsModel;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.art.genies.galleryExhibitions.pojo.gallery.MergeGalleryData;
import com.art.genies.ui.landing_page.scan.art_details.MySpannable;
import com.art.genies.ui.landing_page.scan.art_details.ParallaxScrollView;
import com.art.genies.ui.landing_page.scan.artist_detail.GalleryArtWorkAdapter;
import com.art.genies.utils.CustomReadMoreOption;
import com.art.genies.utils.Ui;
import com.art.genies.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import com.bumptech.glide.request.target.Target;
import com.devs.readmoreoption.ReadMoreOption;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GalleryDetailsFragment extends BaseFragment implements BaseDataModel.CallBack<MergeGalleryData> {
    private ImageView mGalleryImage;
    private TextView mGalleryName;
    private TextView galleryStatus;
    private ImageView mGalleryIcon;
    private TextView mWebLink, txtReadMore;
    private LinearLayout mWebLinkLayout,rlParentArtist,galleryCollectionLayout;
    private RecyclerView mRecyclerView;
    private ImageView mImgBack;
    private String mId;
    private RecyclerView mTimingRecyclerView;
    private RelativeLayout progressLayout;
    private GalleryDetailsModel galleriesModel;
    private TextView txtNotFound;
    private ParallaxScrollView parallaxScrollView;
    private ProgressBar progressBar;
    private View webLinkView;

    public GalleryDetailsFragment(FragmentCallback callback, String id) {
        super(callback);
        mId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mGalleryIcon = view.findViewById(R.id.galleryIcon);
        progressLayout = view.findViewById(R.id.progressLayout);
        mGalleryImage = view.findViewById(R.id.galleryImage);
        mGalleryName = view.findViewById(R.id.galleryName);
        galleryStatus = view.findViewById(R.id.galleryStatus);
        mWebLink = view.findViewById(R.id.webLink);
        webLinkView = view.findViewById(R.id.webLinkView);
        mWebLinkLayout = view.findViewById(R.id.webLinkLayout);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        galleryCollectionLayout = view.findViewById(R.id.galleryCollectionLayout);
        mImgBack = view.findViewById(R.id.imgBack);
        txtReadMore = view.findViewById(R.id.txtReadMore);
        txtNotFound = view.findViewById(R.id.txtNotFound);
        parallaxScrollView = view.findViewById(R.id.parallax);
        progressBar = view.findViewById(R.id.progressBar);
        mImgBack.setOnClickListener(view1 -> {
            try {
                callback.backPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        rlParentArtist = view.findViewById(R.id.rlParentArtist);
        mTimingRecyclerView = view.findViewById(R.id.ohRecyclerView);
        mWebLinkLayout.setOnClickListener(v -> {
            String url = mWebLink.getText().toString();
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
        Ui.showSpinner(progressLayout);
        galleriesModel = new GalleryDetailsModel(this, getActivity());
        galleriesModel.loadDetails(mId);
    }

    private void initOHRecycler(List<Timing> timings) {
        if (timings != null && !timings.isEmpty()) {
            TimingAdapter adapter = new TimingAdapter(timings);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            mTimingRecyclerView.setLayoutManager(layoutManager);
            mTimingRecyclerView.setAdapter(adapter);
        }
    }

    private void initRecycler(List<ArtistCollectionData> artistCollection) {
        GalleryArtWorkAdapter adapter =
                new GalleryArtWorkAdapter(artistCollection, callback);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onDataLoaded(MergeGalleryData data) {
        Gallery gallery = data.getGalleryResponse().getData();
        if (gallery != null) {
            GlideApp.with(mGalleryIcon.getContext())
                    .load(gallery.full_path)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mGalleryIcon);
            progressBar.setVisibility(View.VISIBLE);
            GlideApp.with(mGalleryImage.getContext())
                    .load(gallery.full_path)
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
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(mGalleryImage);
            mGalleryImage.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
            mGalleryName.setText(Utils.getNameLanguage(gallery.name));

            if (gallery.weblink != null && !gallery.weblink.isEmpty())
                mWebLink.setText(gallery.weblink);
            else {
                mWebLinkLayout.setVisibility(View.GONE);
                webLinkView.setVisibility(View.GONE);
            }

            if (Utils.getBioLanguage(gallery.bio).equals("No About"))
            {
                initReadMoreText(Utils.fromHtml(getString(R.string.no_description)));
            }
            else {
                initReadMoreText(Utils.fromHtml(Utils.getBioLanguage(gallery.bio)));
            }
            /*txtReadMore.setText(Utils.getBioLanguage(gallery.bio));
            txtReadMore.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = txtReadMore.getLineCount();
                    if (lineCount > 4) {
                        Log.d("<<<","true count"+lineCount);
                        makeTextViewResizable(txtReadMore, 3, "Read More", true);
                    }
                }
            });*/

            if (data.getArtistCollection()!=null && data.getArtistCollection().size()>0)
            initRecycler(data.getArtistCollection());
            else {
                galleryCollectionLayout.setVisibility(View.GONE);
            }
            Date currentTime = Calendar.getInstance().getTime();
            String dayOfTheWeek = (String) DateFormat.format("EEEE", currentTime);
            String hour = (String) DateFormat.format("h", currentTime);
            String min = (String) DateFormat.format("mm", currentTime);
            int hourMin = Integer.parseInt(hour + min);
            if (gallery.timing != null && !gallery.timing.isEmpty()) {
                if (gallery.timing.contains(dayOfTheWeek)) {
                    for (Timing timing : gallery.timing) {
                        if (timing.day_of_week.equalsIgnoreCase(dayOfTheWeek)) {
                            if (hourMin > Integer.parseInt(timing.closing) || hourMin < Integer.parseInt(timing.opening)) {
                                galleryStatus.setText(R.string.closeNow);
                                galleryStatus.setTextColor(getResources().getColor(R.color.color_red));
                            } else {
                                galleryStatus.setText(R.string.openNow);
                                galleryStatus.setTextColor(getResources().getColor(R.color.color_green));
                            }
                        }
                    }
                } else {
                    galleryStatus.setText(R.string.closeNow);
                    galleryStatus.setTextColor(getResources().getColor(R.color.color_red));
                }

            } else {
                galleryStatus.setVisibility(View.GONE);
                rlParentArtist.setVisibility(View.GONE);

            }
            initOHRecycler(gallery.timing);
        }
        galleriesModel.clean();
        Ui.hideSpinner(progressLayout);
    }

    /*public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

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
*/
    @Override
    public void onError(Throwable throwable) {
        txtNotFound.setVisibility(View.VISIBLE);
        parallaxScrollView.setVisibility(View.GONE);
        Toast.makeText(getActivity(), galleriesModel.handle(throwable), Toast.LENGTH_SHORT).show();
        galleriesModel.clean();
        Ui.hideSpinner(progressLayout);
    }

    private void initReadMoreText(Spanned s) {
        CustomReadMoreOption readMoreOption = new CustomReadMoreOption.Builder(getContext())
            .textLength(3, ReadMoreOption.TYPE_LINE) // OR
            .moreLabel(getString(R.string.read_more))
            .lessLabel(getString(R.string.read_less))
            .labelUnderLine(false)
            .expandAnimation(true)
            .build();

        readMoreOption.addReadMoreTo(txtReadMore, s);

    }
}
