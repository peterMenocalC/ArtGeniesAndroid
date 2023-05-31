package com.art.genies.galleryExhibitions.discover.gallery_art;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.art.genies.BaseFragment;
import com.art.genies.BuildConfig;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.apis.response.art_action.ArtCountRequest;
import com.art.genies.apis.response.art_action.ArtDetailCount;
import com.art.genies.apis.response.art_action.CountArtDetail;
import com.art.genies.apis.response.art_action.CountData;
import com.art.genies.apis.response.art_action.CountGallery;
import com.art.genies.apis.response.art_action.CountUser;
import com.art.genies.apis.response.matchArt.AllArtDetails;
import com.art.genies.apis.response.matchArt.ArtCategories;
import com.art.genies.apis.response.matchArt.Exhibition;
import com.art.genies.apis.response.matchArt.MatchArtInfo;
import com.art.genies.apis.response.matchArt.Owner;
import com.art.genies.common.Constants;
import com.art.genies.common.GlideApp;
import com.art.genies.databinding.FragmentArtDetailBinding;
import com.art.genies.ui.landing_page.scan.art_details.ArtDetailFragment;
import com.art.genies.ui.landing_page.scan.art_details.ArtDetailFragmentViewModel;
import com.art.genies.ui.landing_page.scan.art_details.ExhibitionHistoryAdapter;
import com.art.genies.ui.landing_page.scan.art_details.IArtDetailApiCallback;
import com.art.genies.ui.landing_page.scan.art_details.MySpannable;
import com.art.genies.ui.landing_page.scan.art_details.OwnerHistoryAdapter;
import com.art.genies.ui.landing_page.scan.youtube.MyYouTubeActivity;
import com.art.genies.utils.BlurTransformation;
import com.art.genies.utils.PrefHelper;
import com.art.genies.utils.StringUtils;
import com.art.genies.utils.Ui;
import com.art.genies.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import com.github.florent37.expansionpanel.ExpansionLayout;
import io.blushine.android.ui.showcase.MaterialShowcaseSequence;
import io.blushine.android.ui.showcase.MaterialShowcaseView;
import io.blushine.android.ui.showcase.ShowcaseConfig;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;

import static android.app.Activity.RESULT_OK;

public class GalleryArtDetailFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener,
        IArtDetailApiCallback {

    public FragmentArtDetailBinding binding;
    private AllArtDetails allArtDetails;
    private ArtDetailFragmentViewModel viewModel;
    private Bitmap bitmap;
    private boolean isFavorite;
    private RelativeLayout progressLayout;
    private String mId;

    public GalleryArtDetailFragment(FragmentCallback callback, String id) {
        super(callback);
        mId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_art_detail, container, false);
        viewModel = ViewModelProviders.of(this).get(ArtDetailFragmentViewModel.class);
        viewModel.setNavigator(this);
        binding.rlParentArtist.setOnClickListener(v -> {
            callback.changeFragment(Constants.GALLERY_ARTIST, allArtDetails.getArtist().getId(), null);
        });

        binding.galleryName.setOnClickListener(v -> callback.changeFragment(Constants.GALLERY_DETAILS, allArtDetails.getGallery().getId(), null));
        binding.imgBack.setOnClickListener(v -> {
            callback.backPressed();
        });

        binding.webLayout.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(binding.webLink.getText().toString()));
            startActivity(browserIntent);
        });

        binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allArtDetails != null && allArtDetails.getFull_path() != null && !allArtDetails.getFull_path().isEmpty()) {
                    File file = null;
                    try {
                        file = createImageFile();
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.close();
                        Log.d("<<<<<", "Image Path" + file.getAbsolutePath());
                        countData("download");
                        Toast.makeText(context, getResources().getString(R.string.download_successfully) + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.error_while_downloading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Image file is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.imgShare.setOnClickListener(v -> shareLink());

        binding.fabFav.setOnClickListener(v -> {
            if (isFavorite) {
                binding.fabFav.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.layer_6));
                binding.imgFavoriteCount.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.layer_6));
                countData("dislike");
                isFavorite = false;
            } else {
                binding.fabFav.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.like_fill));
                binding.imgFavoriteCount.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.like_fill));
                countData("favorite");
                isFavorite = true;
            }

        });
        binding.imgFavoriteCount.setOnClickListener(v -> {
            if (isFavorite) {
                binding.fabFav.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.layer_6));
                binding.imgFavoriteCount.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.layer_6));
                countData("dislike");
                isFavorite = false;
            } else {
                binding.fabFav.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.like_fill));
                binding.imgFavoriteCount.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.like_fill));
                countData("favorite");
                isFavorite = true;
            }

        });
        binding.iconShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLink();
            }
        });
        binding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allArtDetails.getVideolink().isEmpty())
                {
                    Toast.makeText(context, getResources().getString(R.string.video_not_available), Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getContext(), MyYouTubeActivity.class);
                    intent.putExtra("url", allArtDetails.getVideolink());
                    startActivity(intent);
                }
            }
        });
        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allArtDetails != null && allArtDetails.getFull_path() != null && !allArtDetails.getFull_path().isEmpty()) {
                    File file = null;
                    try {
                        file = createImageFile();
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.close();
                        Log.d("<<<<<", "Image Path" + file.getAbsolutePath());
                        countData("download");
                        Toast.makeText(context, getResources().getString(R.string.download_successfully) + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, getResources().getString(R.string.error_while_downloading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.image_file_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allArtDetails != null
                        && allArtDetails.getFull_path() != null
                        && !allArtDetails.getFull_path().isEmpty()) {
                    File file = null;
                    try {
                        file = createImageFile();
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.close();
                        Log.d("<<<<<", "Image Path" + file.getAbsolutePath());
                        countData("download");
                        Toast.makeText(context,
                                getResources().getString(R.string.download_successfully)
                                        + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context,
                                getResources().getString(R.string.error_while_downloading),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.image_file_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.setNavigator(this);
        progressLayout = view.findViewById(R.id.progressLayout);
        binding.toolbar.setOnMenuItemClickListener(this);
        Ui.showSpinner(progressLayout);
        viewModel.getArtistDetailById(mId);
        binding.expansionLayout.addListener(new ExpansionLayout.Listener() {
            @Override
            public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {
                if (expanded) {
                    //binding.ohRecyclerView.smoothScrollToPosition(allArtDetails.getOwners().size());
                    binding.ohRecyclerView.requestFocus();
                    Log.d("<<<","Request focused"+expanded);
                }
            }
        });

        binding.OHexpansionLayout.addListener(new ExpansionLayout.Listener() {
            @Override
            public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {
                if (expanded) {
                    //binding.ohRecyclerView.smoothScrollToPosition(allArtDetails.getOwners().size());
                    binding.ehRecyclerView.requestFocus();
                    Log.d("<<<","Request ehRecyclerView focused"+expanded);
                }
            }
        });
    }

    private void makeTextViewResizable(final TextView tv, final int maxLine,
        final String expandText, final boolean viewMore) {

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

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
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
                        makeTextViewResizable(tv, -1, getString(R.string.read_less), false);
                        binding.autherDetail.setVisibility(View.VISIBLE);
                    } else {
                        makeTextViewResizable(tv, 3, getString(R.string.read_more), true);
                        binding.autherDetail.setVisibility(View.GONE);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }
    private void setArtistData() {
        artDetailShowCaseSequence();
        initOHRecycler();
        if (allArtDetails != null) {
            viewModel.getFavStatus(allArtDetails.id);
            if (allArtDetails.getFavoriteCount() >= 0) {
                binding.txtFavoriteCount.setText(String.valueOf(allArtDetails.getFavoriteCount()));
            }
            if (allArtDetails.getDownloadCount() >= 0) {
                binding.txtDownloadCount.setText(String.valueOf(allArtDetails.getDownloadCount()));
            }

            if (allArtDetails.getShareCount() >= 0) {
                binding.txtShareCount.setText(String.valueOf(allArtDetails.getShareCount()));
            }

            if (allArtDetails.full_path != null) {
                binding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(binding.artImage.getContext())
                        .asBitmap()
                        .load(allArtDetails.full_path)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource,
                                                        @Nullable Transition<? super Bitmap> transition) {
                                bitmap = resource;
                                binding.artImage.setImageBitmap(resource);
                                binding.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                binding.artImage.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
                GlideApp.with(binding.blurImage.getContext())
                        .load(allArtDetails.full_path)
                        .transform(new BlurTransformation(binding.artImage.getContext()))
                        .into(binding.blurImage);
            }

            if (allArtDetails.name != null) {
                if (allArtDetails.tagLine !=null)
                {
                    binding.artName.setText(Utils.getNameLanguage(allArtDetails.name)+" - "+Utils.getNameLanguage(allArtDetails.tagLine));
                }
                else binding.artName.setText(Utils.getNameLanguage(allArtDetails.name));
            }

            if (allArtDetails.price !=null && !allArtDetails.price.equals("0"))
            {
                binding.priceSymbol.setVisibility(View.VISIBLE);
                binding.price.setText(allArtDetails.price);
            }
            else {
                binding.price.setText("  -" + getString(R.string.not_avilable));
                binding.priceSymbol.setVisibility(View.GONE);
            }

            if (allArtDetails.getArtist() != null && allArtDetails.getArtist().getName() != null) {
                binding.txtArtist.setText(Utils.getNameLanguage(allArtDetails.getArtist().getName()));
            }

            if (allArtDetails != null
                    && allArtDetails.getDescriptions().size() > 0
                    && allArtDetails.getDescriptions().get(0) != null) {
                if (allArtDetails.getDescriptions().get(0).getDescription() != null) {

                    if (Utils.getNameLanguage(
                            allArtDetails.getDescriptions().get(0).getDescription()).equals("No Description"))
                    {
                        binding.txtReadMore.setText(getString(R.string.no_description));
                    }
                    else {
                        binding.txtReadMore.setText(Utils.fromHtml(Utils.getNameLanguage(
                                allArtDetails.getDescriptions().get(0).getDescription())));
                    }
                    binding.txtReadMore.post(new Runnable() {
                        @Override
                        public void run() {
                            int lineCount = binding.txtReadMore.getLineCount();
                            if (lineCount > 4) {
                                makeTextViewResizable(binding.txtReadMore, 3, getString(R.string.read_more), true);
                            } else {
                                binding.autherDetail.setVisibility(View.GONE);
                            }
                            // Use lineCount here
                        }
                    });
                    //makeTextViewResizable(binding.txtReadMore, 3, "Read More", true);
                }
                if (allArtDetails.getDescriptions().get(0).getAuthor() != null) {
                    binding.authorName.setText(Utils.getNameLanguage(allArtDetails.getDescriptions()
                            .get(0)
                            .getAuthor()
                            .getName()));
                }
                if (allArtDetails.getDescriptions().get(0).getAuthor() != null
                        && allArtDetails.getDescriptions().get(0).getAuthor().getThumbPath() != null) {
                    GlideApp.with(binding.imageWriter.getContext())
                            .load(allArtDetails.getDescriptions().get(0).getAuthor().getThumbPath())
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imageWriter);
                }
            }
            if (allArtDetails.getWeblink() != null && !allArtDetails.getWeblink().isEmpty()) {
                binding.webLink.setText(allArtDetails.getWeblink());
            }
            else {
                binding.webLink.setVisibility(View.GONE);
                binding.webLinkIcon.setVisibility(View.GONE);
                binding.webLinkView.setVisibility(View.GONE);
            }
            if (allArtDetails != null
                    && allArtDetails.getGallery() != null) {

                GlideApp.with(binding.imageView.getContext())
                        .load(allArtDetails.getGallery().getThumbPath())
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imageView);

                binding.galleryName.setText(Utils.getNameLanguage(allArtDetails.getGallery().getName()));
                binding.galleryOpenCloseStatus.setText(getResources().getString(R.string.openNow));
                binding.galleryOpenCloseStatus.setTextColor(getResources().getColor(R.color.color_green));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private void initOHRecycler() {
        List<Exhibition> exhibitions = new ArrayList<>();
        List<Owner> owners = new ArrayList<>();
        /*if (allArtDetails.getPrints() != null && allArtDetails.getPrints().size() > 0) {
            exhibitions = allArtDetails.getExhibitions();
            owners = allArtDetails.getOwners();
        } else {
            exhibitions = new ArrayList<>();
            owners = new ArrayList<>();
        }*/


        /*List<String> languages =
            Arrays.asList(getResources().getStringArray(R.array.language_list));*/
        OwnerHistoryAdapter adapter = new OwnerHistoryAdapter(owners);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.ohRecyclerView.setLayoutManager(layoutManager);
        binding.ohRecyclerView.setAdapter(adapter);

        ExhibitionHistoryAdapter adapter1 = new ExhibitionHistoryAdapter(exhibitions);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        binding.ehRecyclerView.setLayoutManager(layoutManager1);
        binding.ehRecyclerView.setAdapter(adapter1);

        if (allArtDetails.getExhibitions() != null && allArtDetails.getExhibitions().size() > 0) {
            exhibitions.clear();
            exhibitions.addAll(allArtDetails.getExhibitions());
            adapter1.notifyDataSetChanged();
        } else {
            binding.ehRecyclerView.setVisibility(View.GONE);
            binding.llExhibitionExpandLayout.setVisibility(View.GONE);
        }
        if (allArtDetails.getOwners() != null && allArtDetails.getOwners().size() > 0) {
            owners.clear();
            owners.addAll(allArtDetails.getOwners());
            adapter.notifyDataSetChanged();
        } else {
            binding.ohRecyclerView.setVisibility(View.GONE);
            binding.llOwnerShipExpandLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.video) {
            if (allArtDetails.getVideolink().equalsIgnoreCase(StringUtils.EMPTY)) {
                Toast.makeText(context, "Video not available", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getContext(), MyYouTubeActivity.class);
                intent.putExtra("url", allArtDetails.getVideolink());
                startActivity(intent);
            }
            return true;
        } else if (item.getItemId() == R.id.download) {
            if (allArtDetails != null && allArtDetails.getFull_path() != null && !allArtDetails.getFull_path().isEmpty()) {
                File file = null;
                try {
                    file = createImageFile();
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    Log.d("<<<<<", "Image Path" + file.getAbsolutePath());
                    countData("download");
                    Toast.makeText(context, getResources().getString(R.string.download_successfully) + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, getResources().getString(R.string.error_while_downloading), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Image file is empty", Toast.LENGTH_SHORT).show();
            }
        }else if(item.getItemId() == R.id.icon_share) {
            shareLink();
        } /*else if (item.getItemId() == R.id.support) {
            String categories = "";
            if (allArtDetails != null && allArtDetails.getCategories() != null && allArtDetails.getCategories().size() > 0) {
                for (ArtCategories artCategories : allArtDetails.getCategories()) {
                    categories=artCategories.getName()+","+categories;
                }
            }
            callback.changeFragment(Constants.EXPERT_LIST, categories.substring(0, categories.length() - 1), allArtDetails);
        }*/
        return false;
    }

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageName = "art_" + timeStamp + "_" + ".jpg";
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imageName);
    }

    public void shareLink() {
        String url = "";
        if (allArtDetails != null) {
            String name = Utils.getNameLanguage(allArtDetails.getName()).replace(" ", "-");
            String artId = allArtDetails.getId();
            url = BuildConfig.SHARE_BASE_URL + "/arts/details/?name=" + name + "&art=" + artId;
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Art genies Art");
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivityForResult(Intent.createChooser(share, "Art Genies"), 100);
    }

    @Override
    public void onSuccess(ArtDetailCount detail) {
        CountData count = detail.getData();
        binding.txtShareCount.setText(String.valueOf(count.getShareCount()));
        binding.txtDownloadCount.setText(String.valueOf(count.getDownloadCount()));
        binding.txtFavoriteCount.setText(String.valueOf(count.getFavoriteCount()));
        allArtDetails.setDownloadCount(count.getDownloadCount());
        allArtDetails.setFavoriteCount(count.getFavoriteCount());
        allArtDetails.setShareCount(count.getShareCount());
    }

    @Override
    public void onError(String msg, boolean isArtError) {
        Ui.hideSpinner(progressLayout);
        if (isArtError) {
            binding.llArtMenu.setVisibility(View.GONE);
            binding.txtNotFound.setVisibility(View.VISIBLE);
            binding.parallax.setVisibility(View.GONE);
        }
        //Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArtistDetailById(MatchArtInfo info) {
        //binding.toolbar.inflateMenu(R.menu.art_detail_menu);
        Ui.hideSpinner(progressLayout);
        this.allArtDetails = info.getData();
        setArtistData();
    }

    @Override
    public void onFavStatus(boolean isFav) {
        isFavorite = isFav;
        binding.fabFav.setImageDrawable(context.getResources().getDrawable(R.drawable.like_fill));
        binding.imgFavoriteCount.setImageDrawable(context.getResources().getDrawable(R.drawable.like_fill));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            countData("share");
            //viewModel
            Toast.makeText(context, getResources().getString(R.string.share_sucess), Toast.LENGTH_SHORT).show();
        }
    }

    private void countData(String purpose) {
        ArtCountRequest artCountRequest = new ArtCountRequest();
        CountArtDetail countArtDetail = new CountArtDetail();
        countArtDetail.setName(allArtDetails.getName());
        countArtDetail.setId(allArtDetails.getId());
        countArtDetail.setThumbPath(allArtDetails.getThumb_path());
        CountGallery countGallery = new CountGallery();
        countGallery.setId(allArtDetails.getGallery().getId());
        countGallery.setName(allArtDetails.getGallery().getName());
        countGallery.setThumbPath(allArtDetails.getGallery().getThumbPath());
        artCountRequest.setGallery(countGallery);
        artCountRequest.setArtDetailCount(countArtDetail);
        CountUser user = new CountUser();
        user.setId(PrefHelper.getUserData().getId());
        artCountRequest.setUser(user);
        artCountRequest.setPurpose(purpose);
        viewModel.getArtCount(allArtDetails.getId(), artCountRequest);
    }

    private void artDetailShowCaseSequence() {
        ShowcaseConfig config = new ShowcaseConfig(context);
        config.setDelay(0);

        MaterialShowcaseSequence sequence =
            new MaterialShowcaseSequence(getActivity(), "");
        sequence.setSingleUse(GalleryArtDetailFragment.class.getName());
        sequence.setConfig(config);

        // 1
        sequence.addSequenceItem(
            new MaterialShowcaseView.Builder(getActivity())
                .setTarget(binding.iconShare)
                .setTitleText(getString(R.string.share))
                .setContentText(getString(R.string.share_art_info))
                .setTargetTouchable(false)
                .setDismissText(getString(R.string.next))
                .build()
        );

       /* // 1
        sequence.addSequenceItem(
            new MaterialShowcaseView.Builder(getActivity())
                .setTarget(binding.video)
                .setTitleText(getString(R.string.video))
                .setContentText(getString(R.string.see_video))
                .setTargetTouchable(false)
                .setDismissText(getString(R.string.next))
                .build()
        );
*/
        // 1
        sequence.addSequenceItem(
            new MaterialShowcaseView.Builder(getActivity())
                .setTarget(binding.download)
                .setTitleText(getString(R.string.download))
                .setContentText(getString(R.string.click_to_download))
                .setTargetTouchable(false)
                .setDismissText(getString(R.string.done))
                .build()
        );

        config.setDelay(500);
        sequence.show();
    }

    public AllArtDetails getArtDetails() {
        return allArtDetails;
    }
}
