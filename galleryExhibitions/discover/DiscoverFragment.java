package com.art.genies.galleryExhibitions.discover;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.art.genies.App;
import com.art.genies.BaseFragment;
import com.art.genies.CustomInfoWindowGoogleMap;
import com.art.genies.FragmentCallback;
import com.art.genies.R;
import com.art.genies.apis.response.Locations;
import com.art.genies.common.BaseDataModel;
import com.art.genies.common.Constants;
import com.art.genies.galleryExhibitions.model.GalleryExhibitionsNearByModel;
import com.art.genies.galleryExhibitions.pojo.GalleryExhibitionNearBy;
import com.art.genies.galleryExhibitions.pojo.NearBy;
import com.art.genies.galleryExhibitions.pojo.exhibitions.ExhibitionData;
import com.art.genies.galleryExhibitions.pojo.exhibitions.Exhibitions;
import com.art.genies.galleryExhibitions.pojo.gallery.Gallery;
import com.art.genies.ui.landing_page.LandingActivity;
import com.art.genies.utils.Ui;
import com.art.genies.utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.blushine.android.ui.showcase.MaterialShowcaseView;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DiscoverFragment extends BaseFragment implements OnMapReadyCallback, BaseDataModel.CallBack<GalleryExhibitionNearBy>,
    GoogleMap.OnCameraMoveListener {
    private static final String SHOWCASE_CORNER_ID = DiscoverFragment.class.getName();
    private GoogleMap mMap;
    private AppCompatImageView mList;
    private RxPermissions rxPermissions;
    private CompositeDisposable compositeDisposable;
    private GalleryExhibitionsNearByModel galleryExhibitionsNearByModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private RelativeLayout progressLayout;
    private AppCompatImageView barImage;
    private AppCompatTextView barText;
    private TextView txtFetchGallery;
    private static int radius = 5000;
    private Double latitude;
    private Double longitude;
    private boolean isFirstLoad;
    private boolean animateCamera = true;
    private static final int REQUEST_ENABLE_GPS = 516;
    private LinearLayout llFotoFever;

    public DiscoverFragment(FragmentCallback callback) {
        super(callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        animateCamera = true;
        barImage = ((LandingActivity) getActivity()).mBottomNav.mImageViewBar1;
        txtFetchGallery = view.findViewById(R.id.txtFetchGallery);
        /*if (!Utils.isGpsEnabled(context)) {
            openGpsEnableSetting();
        }*/
        displayLocationSettingsRequest(context);

        txtFetchGallery.setOnClickListener(v -> {
            animateCamera = false;
            loadMapOnLastLocationBased();
        });

        barText = ((LandingActivity) getActivity()).mBottomNav.mTextViewBar1;
        mList = view.findViewById(R.id.list);
        progressLayout = view.findViewById(R.id.progressLayout);
        llFotoFever = view.findViewById(R.id.llFotoFever);
        rxPermissions = new RxPermissions(getActivity());
        compositeDisposable = new CompositeDisposable();
        galleryExhibitionsNearByModel = new GalleryExhibitionsNearByModel(this, getActivity());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mList.setOnClickListener(view1 -> {
            try {
                unSelectBarMenu();
                callback.changeFragment(Constants.GALLERY_EXHIBITION_LIST, "", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        llFotoFever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadExhibitionDetails("5f06052dadba7e04808763b0");
            }
        });
        discoverShowCase();
    }

    private void showRequestPermission() {
        compositeDisposable.add(rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        mMap.setMyLocationEnabled(true);
                        mMap.setOnInfoWindowClickListener(marker -> {
                            if (marker.getTag() != null) {
                                InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
                                if (infoWindowData.type.equals("Gallery")) {
                                    loadGalleryDetails(infoWindowData.id);
                                } else {
                                    loadExhibitionDetails(infoWindowData.id);
                                }
                            }
                        });
                        if (((LandingActivity)getActivity()).isCurrentExhibitionEnable) {
                            loadMapOnLastLocationBased();
                        } else {
                            checkCurrentExhibition();
                        }

                    } else {
                        showRequestPermission();
                    }
                }));
    }


    private void loadLastLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                showRequestPermission();
            }  else {
                showUnableToFetchLocationMsg();
            }
        });
    }

    private void loadMapOnLastLocationBased(){
        Log.d("<<<","radius"+getMapVisibleRadiusTest());
        NearBy nearBy = new NearBy();
        if (latitude != null && longitude != null) {
            nearBy.Lat = "" + latitude;
            nearBy.Long = "" + longitude;
            if (isFirstLoad) {
                nearBy.Radius = String.valueOf(getMapVisibleRadiusTest());
            } else {
                isFirstLoad = true;
                nearBy.Radius = String.valueOf(radius);
            }
            Ui.showSpinner(progressLayout);
            galleryExhibitionsNearByModel.loadNearBy(nearBy);
        } else {
            txtFetchGallery.setVisibility(View.GONE);
            showUnableToFetchLocationMsg();
        }
    }

    private void showUnableToFetchLocationMsg(){
        Toast.makeText(getContext(), getString(R.string.unableToFindLocation), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment;
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(i -> {
            mMap.getProjection().getVisibleRegion();
            LatLng latLng = mMap.getCameraPosition().target;
            if (latLng != null) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
            if (txtFetchGallery.getVisibility() != View.VISIBLE) {
                txtFetchGallery.setVisibility(View.VISIBLE);
            }
        });
        loadLastLocation();
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
        mMap.setInfoWindowAdapter(customInfoWindow);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    @Override
    public void onDataLoaded(GalleryExhibitionNearBy data) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (data == null || data.data == null) {
            Ui.dismissProgress();
            return;
        }
        mMap.clear();
        if (!data.data.galleries.isEmpty()) {
            for (Gallery gallery : data.data.galleries) {
                LatLng latLng = new LatLng(gallery.location.coordinates.get(1), gallery.location.coordinates.get(0));
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                InfoWindowData info = new InfoWindowData();
                info.image = gallery.full_path;
                info.name = Utils.getNameLanguage(gallery.name);
                info.type = "Gallery";
                info.id = gallery._id;
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(info);
                m.showInfoWindow();
            }
        }
        if (!data.data.exhibitions.isEmpty()) {
            for (Exhibitions exhibitions : data.data.exhibitions) {
                Locations location = exhibitions.locations.get(0);
                LatLng latLng = new LatLng(location.location.coordinates.get(1), location.location.coordinates.get(0));
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                InfoWindowData info = new InfoWindowData();
                info.image = exhibitions.full_path;
                info.name = Utils.getNameLanguage(exhibitions.name);
                info.type = "Exhibition";
                info.id = exhibitions._id;
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(info);
                m.showInfoWindow();
            }
        }
        animateToLocation();
        txtFetchGallery.setVisibility(View.GONE);
        Ui.hideSpinner(progressLayout);
        galleryExhibitionsNearByModel.clean();
        latitude = longitude = null;
    }

    private void animateToLocation() {
        if (animateCamera && latitude != null && longitude != null)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
        animateCamera = false;
    }
    @Override
    public void onError(Throwable throwable) {
        animateToLocation();
        txtFetchGallery.setVisibility(View.GONE);
        String errorMsg = galleryExhibitionsNearByModel.handle(throwable);
        if (errorMsg.equalsIgnoreCase(getString(R.string.somethingWentWrong))) {
            errorMsg = getString(R.string.no_exhibitions);
        }
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        galleryExhibitionsNearByModel.clean();
        latitude = longitude = null;
        Ui.hideSpinner(progressLayout);
    }

    private void unSelectBarMenu() {
        barImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorNavAccentUnselected));
        barText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorNavAccentUnselected));
    }

    @Override
    public void onCameraMove() {
    }

    public class InfoWindowData {
        public String image;
        public String name;
        public String id;
        public String type;
    }

    private void loadGalleryDetails(String id) {
        callback.changeFragment(Constants.GALLERY_DETAILS, id, null);
        unSelectBarMenu();
    }

    private void loadExhibitionDetails(String id) {
        callback.changeFragment(Constants.EXHIBITION_DETAILS, id, null);
        unSelectBarMenu();
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }


    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private double getMapVisibleRadiusTest() {
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();

        float[] distanceWidth = new float[1];
        float[] distanceHeight = new float[1];

        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        Location.distanceBetween(
            (farLeft.latitude + nearLeft.latitude) / 2,
            farLeft.longitude,
            (farRight.latitude + nearRight.latitude) / 2,
            farRight.longitude,
            distanceWidth
        );

        Location.distanceBetween(
            farRight.latitude,
            (farRight.longitude + farLeft.longitude) / 2,
            nearRight.latitude,
            (nearRight.longitude + nearLeft.longitude) / 2,
            distanceHeight
        );

        double radiusInMeters = Math.sqrt(Math.pow(distanceWidth[0], 2) + Math.pow(distanceHeight[0], 2)) / 2;
        return radiusInMeters;
    }

    private void discoverShowCase() {
        new MaterialShowcaseView.Builder(getActivity())
            .setTarget(mList)
            .setTitleText(getResources().getString(R.string.see_all))
            .setDismissText(getResources().getString(R.string.done))
            .setContentText(getResources().getString(R.string.all_galleries_exhibitions))
            .setDelay(0) // optional but starting animations immediately in onCreate can make them choppy
            .setSingleUse(SHOWCASE_CORNER_ID) // provide a unique ID used to ensure it is only shown once
            .show();
    }

    private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void openGpsEnableSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void displayLocationSettingsRequest(Context context) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("<<<<", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("<<<<<", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_ENABLE_GPS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("<<<<<", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("<<<<", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void checkCurrentExhibition() {
        Ui.showSpinner(progressLayout);
        txtFetchGallery.setClickable(false);
        App.getApi().checkCurrentExhibition(longitude,latitude)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new SingleObserver<ExhibitionData>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(ExhibitionData exhibitionData) {
                    Ui.hideSpinner(progressLayout);
                    txtFetchGallery.setClickable(true);
                    if (exhibitionData != null && exhibitionData.data != null) {
                        ((LandingActivity)getActivity()).isCurrentExhibitionEnable = true;
                        showExhibitionDialog(Utils.getNameLanguage(exhibitionData.data.name),exhibitionData.data._id);
                    } else {
                        loadMapOnLastLocationBased();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Ui.hideSpinner(progressLayout);
                    txtFetchGallery.setClickable(true);
                    loadMapOnLastLocationBased();
                }
            });

    }

    private void showExhibitionDialog(String exhibitionName,String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(exhibitionName);
        String msg = getResources().getString(R.string.looks_like) +" "+ exhibitionName;
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadExhibitionDetails(id);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loadMapOnLastLocationBased();
            }
        });
        builder.create().show();
    }



}
