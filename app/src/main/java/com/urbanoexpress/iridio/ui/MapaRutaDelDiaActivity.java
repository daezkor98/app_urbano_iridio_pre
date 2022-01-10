package com.urbanoexpress.iridio.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.application.AndroidApplication;
import com.urbanoexpress.iridio.databinding.ActivityMapaRutaDelDiaBinding;
import com.urbanoexpress.iridio.model.entity.Ruta;
import com.urbanoexpress.iridio.presenter.MapaRutaDelDiaPresenter;
import com.urbanoexpress.iridio.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio.ui.model.RutaItem;
import com.urbanoexpress.iridio.util.CachingUrlTileProvider;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.MetricsUtils;
import com.urbanoexpress.iridio.util.constant.Constants;
import com.urbanoexpress.iridio.view.MapaRutaDelDiaView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapaRutaDelDiaActivity extends AppThemeBaseActivity
        implements MapaRutaDelDiaView, RutaAdapter.OnClickGuiaItemListener, OnMapReadyCallback {

    private ActivityMapaRutaDelDiaBinding binding;
    private MapaRutaDelDiaPresenter presenter;
    private GoogleMap googleMap;
    private BottomSheetBehavior bottomSheetBehaviorGuias;
    private List<Marker> markerGuias;
    private int BEHAVIOR_PEEK_HEIGHT = 85;
    private TileOverlay tileOverlay;
    private static String CUSTOM_MAP_URL_FORMAT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapaRutaDelDiaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (presenter == null) {
            presenter = new MapaRutaDelDiaPresenter(this);
            presenter.init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroyActivity();
    }

    @Override
    public boolean onBackButtonPressed() {
        if (bottomSheetBehaviorGuias.getState() == BottomSheetBehavior.STATE_SETTLING
                || bottomSheetBehaviorGuias.getState() == BottomSheetBehavior.STATE_COLLAPSED
                || bottomSheetBehaviorGuias.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorGuias.setState(BottomSheetBehavior.STATE_HIDDEN);
            return true;
        } else {
            return presenter.onBackButtonPressed();
        }
    }

    @Override
    public void displayGuiasOnMap(List<Ruta> guias) {
        googleMap.clear();

        configCustomMap();

        LatLngBounds.Builder latlngBoundsBuilder = new LatLngBounds.Builder();
        LatLngBounds bounds;

        markerGuias = new ArrayList<>();

        for (int i = 0; i < guias.size(); i++) {
            LatLng latLng;
            if (CommonUtils.isValidCoords(guias.get(i).getGpsLatitude(), guias.get(i).getGpsLongitude())) {
                latLng = new LatLng(Double.parseDouble(guias.get(i).getGpsLatitude()),
                        Double.parseDouble(guias.get(i).getGpsLongitude()));
            } else {
                latLng = new LatLng(0,0);
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(
                            R.drawable.ic_marker_truck));

            switch (guias.get(i).getResultadoGestion()) {
                case Ruta.ResultadoGestion.NO_DEFINIDO:
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                            drawTextToBitmap(
                                    MapaRutaDelDiaActivity.this,
                                    R.drawable.ic_marker_yellow,
                                    guias.get(i).getSecuencia())
                    ));
                    break;
                case Ruta.ResultadoGestion.EFECTIVA_COMPLETA:
                case Ruta.ResultadoGestion.EFECTIVA_PARCIAL:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(
                            R.drawable.ic_marker_package_blue));
                    break;
                case Ruta.ResultadoGestion.NO_EFECTIVA:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(
                            R.drawable.ic_marker_package_red));
                    break;
            }

            markerGuias.add(googleMap.addMarker(markerOptions));

            latlngBoundsBuilder.include(latLng);
        }

        if (markerGuias.size() > 0) {
            bounds = latlngBoundsBuilder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.15); // offset from edges of the map 12% of screen

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.228864, -76.927627), 15));
        }
    }

    @Override
    public void displayRutearGuiasOnMap(List<Ruta> guias) {
        googleMap.clear();

        configCustomMap();

        LatLngBounds.Builder latlngBoundsBuilder = new LatLngBounds.Builder();
        LatLngBounds bounds;

        markerGuias = new ArrayList<>();

        for (int i = 0; i < guias.size(); i++) {
            LatLng latLng;
            if (CommonUtils.isValidCoords(guias.get(i).getGpsLatitude(), guias.get(i).getGpsLongitude())) {
                latLng = new LatLng(Double.parseDouble(guias.get(i).getGpsLatitude()),
                        Double.parseDouble(guias.get(i).getGpsLongitude()));
            } else {
                latLng = new LatLng(0,0);
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            drawTextToBitmap(
                                    MapaRutaDelDiaActivity.this,
                                    R.drawable.ic_marker_yellow, "")
            ));

            markerGuias.add(googleMap.addMarker(markerOptions));

            latlngBoundsBuilder.include(latLng);
        }

        if (markerGuias.size() > 0) {
            bounds = latlngBoundsBuilder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.15); // offset from edges of the map 12% of screen

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
    }

    @Override
    public void displayListGuias(List<RutaItem> guias) {
        bottomSheetBehaviorGuias.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (guias.size() == 1) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            binding.rvGuias.setLayoutParams(layoutParams);
        } else {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, MetricsUtils.dpToPx(
                    MapaRutaDelDiaActivity.this, 255));
            binding.rvGuias.setLayoutParams(layoutParams);
        }

        RutaAdapter rutaAdapter = new RutaAdapter(this, this, guias);
        binding.rvGuias.setAdapter(rutaAdapter);
        binding.wrapperBottom.setVisibility(View.VISIBLE);
        bottomSheetBehaviorGuias.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void displayMarkerSelector(String guia) {
        bottomSheetBehaviorGuias.setState(BottomSheetBehavior.STATE_HIDDEN);
        googleMap.clear();
        configCustomMap();
        binding.boxMenuFab.setVisibility(View.GONE);
        binding.lblGuiaElectronica.setText(guia);
        binding.boxMarkerSelector.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> googleMap.setPadding(0, 0, 0,
                MetricsUtils.dpToPx(this, 65) +
                        MetricsUtils.dpToPx(this, 10)), 500);
    }

    @Override
    public void hideMarkerSelector() {
        binding.boxMarkerSelector.setVisibility(View.GONE);
        binding.boxMenuFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVisibilityBoxRuteoGuias(int visibility) {
        if (visibility == View.VISIBLE) {
            bottomSheetBehaviorGuias.setState(BottomSheetBehavior.STATE_HIDDEN);
            binding.boxMenuFab.setVisibility(View.GONE);
            binding.boxRutearGuias.setVisibility(View.VISIBLE);
        } else {
            binding.boxMenuFab.setVisibility(View.VISIBLE);
            binding.boxRutearGuias.setVisibility(View.GONE);
        }
    }

    @Override
    public void setVisibilityFabGuiasSinCoordenadas(int visibility) {
        binding.fabGuiasSinCoordenadas.setVisibility(visibility);
    }

    @Override
    public void setVisibilityFabRutearGuias(int visibility) {
        binding.fabRutearGuias.setVisibility(visibility);
    }

    @Override
    public void setVisibilityMenuFab(int visibility) {
        binding.boxMenuFab.setVisibility(visibility);
    }

    @Override
    public void updateNumberIconMarker(int position, String number) {
        markerGuias.get(position).setIcon(BitmapDescriptorFactory.fromBitmap(
                drawTextToBitmap(this, R.drawable.ic_marker_yellow, number)));
    }

    @Override
    public void animateCameraMap(ArrayList<LatLng> latLngs) {
        LatLngBounds.Builder latlngBoundsBuilder = new LatLngBounds.Builder();
        LatLngBounds bounds;

        for (int i = 0; i < latLngs.size(); i++) {
            latlngBoundsBuilder.include(latLngs.get(i));
        }

        if (latLngs.size() > 0) {
            bounds = latlngBoundsBuilder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.15); // offset from edges of the map 12% of screen

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
    }

    @Override
    public void onClickGuiaItem(View view, int position) {
        presenter.onClickItemGuia();
    }

    @Override
    public void onClickGuiaIconLinea(View view, int position) {

    }

    @Override
    public void onClickGuiaIconImporte(View view, int position) {

    }

    @Override
    public void onClickGuiaIconTipoEnvio(View view, int position) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMapClickListener(latLng -> {
            if (bottomSheetBehaviorGuias.getState() ==
                    BottomSheetBehavior.STATE_SETTLING ||
                    bottomSheetBehaviorGuias.getState() ==
                            BottomSheetBehavior.STATE_COLLAPSED ||
                    bottomSheetBehaviorGuias.getState() ==
                            BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehaviorGuias.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        googleMap.setOnMarkerClickListener(marker -> {
            presenter.onClickMarkerMap(getPositionMarker(marker));
            return false;
        });

        googleMap.setPadding(0, MetricsUtils.dpToPx(this, 24) +
                CommonUtils.getActionBarDimensionPixelSize(this), 0, 0);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        /*googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                MapaRutaDelDiaActivity.this, R.raw.style_silver_map));*/

        /*tileProvider = new UrlTileProvider(512, 512) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                // The moon tile coordinate system is reversed.  This is not normal.
                int reversedY = (1 << zoom) - y - 1;
                String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, y);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };

        tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider).fadeIn(false));*/

        configCustomMap();

        //new LoadKMLTask().execute();

        presenter.onMapReady();
    }

    public void configCustomMap() {
        CachingUrlTileProvider tileProvider = new CachingUrlTileProvider(this, 512, 512) {
            @Override
            public String getTileUrl(int x, int y, int z) {
                //return String.format("https://a.tile.openstreetmap.org/%3$s/%1$s/%2$s.png",x,y,z);
                return String.format(Locale.US, CUSTOM_MAP_URL_FORMAT, z, x, y);
            }
        };
        tileOverlay = googleMap.addTileOverlay(tileProvider.createTileOverlayOptions());
    }

    /*private KmlLayer kmlLayer;

    private class LoadKMLTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                kmlLayer = new KmlLayer(googleMap, R.raw.kml, getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                kmlLayer.addLayerToMap();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }*/

    @SuppressLint("MissingPermission")
    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle("");

        if (MetricsUtils.isHighDensityScreen(this)) {
            CUSTOM_MAP_URL_FORMAT = Constants.CUSTOM_MAP_HDPI_URL_FORMAT;
        } else {
            CUSTOM_MAP_URL_FORMAT = Constants.CUSTOM_MAP_LDPI_URL_FORMAT;
        }

        binding.rvGuias.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGuias.setHasFixedSize(true);

        binding.fabShowMyLocation.setOnClickListener(v -> {
            LocationServices.getFusedLocationProviderClient(AndroidApplication.getAppContext())
                    .getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 15));
                }
            });
        });

        binding.fabRutearGuias.setOnClickListener(v -> presenter.onClickRutearGuias());

        binding.fabGuiasSinCoordenadas.setOnClickListener(v -> presenter.onClickGuiasSinCoordenadas());

        binding.btnSeleccionarCoordenada.setOnClickListener(v -> presenter.onClickSeleccionarCoordenada(
                googleMap.getCameraPosition().target.latitude,
                googleMap.getCameraPosition().target.longitude));

        binding.btnGuardarRuteoGuias.setOnClickListener(v -> presenter.onClickGuardarRuteoGuias());

        bottomSheetBehaviorGuias = BottomSheetBehavior.from(binding.bottomSheetGuias);
        bottomSheetBehaviorGuias.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehaviorGuias.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_SETTLING == newState ) {
                    googleMap.setPadding(0, MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 24) +
                                    CommonUtils.getActionBarDimensionPixelSize(MapaRutaDelDiaActivity.this), 0,
                            MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, BEHAVIOR_PEEK_HEIGHT) +
                                    MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 10));
                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    googleMap.setPadding(0, MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 24) +
                                    CommonUtils.getActionBarDimensionPixelSize(MapaRutaDelDiaActivity.this), 0,
                            MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, BEHAVIOR_PEEK_HEIGHT) +
                                    MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 10));
                } else if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    googleMap.setPadding(0, MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 24) +
                                    CommonUtils.getActionBarDimensionPixelSize(MapaRutaDelDiaActivity.this), 0,
                            binding.bottomSheetGuias.getHeight() - MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 5));
                } else if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    googleMap.setPadding(0, MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 24) +
                            CommonUtils.getActionBarDimensionPixelSize(MapaRutaDelDiaActivity.this), 0, 0);
                }

                /*if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(
                            MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 70),
                            MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 0));
                    layoutParams.gravity = Gravity.TOP;
                    layoutParams.anchorGravity = Gravity.END;
                    layoutParams.setAnchorId(R.id.bottomSheetGuias);
                    //binding.spaceMenuFab.setLayoutParams(layoutParams);
                } else {
                    CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(
                            MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 70),
                            MetricsUtils.dpToPx(MapaRutaDelDiaActivity.this, 70));
                    layoutParams.gravity = Gravity.TOP;
                    layoutParams.anchorGravity = Gravity.END;
                    layoutParams.setAnchorId(R.id.bottomSheetGuias);
                    //binding.spaceMenuFab.setLayoutParams(layoutParams);
                }*/
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if (CommonUtils.isAndroidLollipop()) {
            binding.rvGuias.setClipToOutline(true);
        }

        configGoogleMap();
    }

    private void configGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    private int getPositionMarker(Marker marker) {
        for (int i = 0; i < markerGuias.size(); i++) {
            if (markerGuias.get(i).getId().equals(marker.getId())) {
                return i;
            }
        }
        return -1;
    }

    public Bitmap drawTextToBitmap(Context mContext, int resourceId, String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(247,159, 0));
            //paint.setColor(Color.rgb(247,0, 88));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            //paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
//            int x = (bitmap.getWidth() - bounds.width())/6;
//            int y = (bitmap.getHeight() + bounds.height())/5;

            int x = (bitmap.getWidth() / 2) - ((bounds.width() / 2));
            int y = (bitmap.getHeight() / 2); // - (bounds.height() / 2);

            canvas.drawText(mText, x, y, paint);

            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}