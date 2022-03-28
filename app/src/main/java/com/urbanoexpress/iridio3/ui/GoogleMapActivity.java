package com.urbanoexpress.iridio3.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityGoogleMapBinding;
import com.urbanoexpress.iridio3.presenter.GoogleMapPresenter;
import com.urbanoexpress.iridio3.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.PermissionUtils;
import com.urbanoexpress.iridio3.view.GoogleMapView;

import java.util.ArrayList;

public class GoogleMapActivity extends AppThemeBaseActivity implements OnMapReadyCallback, GoogleMapView {

    private ActivityGoogleMapBinding binding;
    private GoogleMapPresenter presenter;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeEnterTransition());
        }

        setupViews();

        if (presenter == null) {
            presenter = new GoogleMapPresenter(this, getIntent().getExtras().getBundle("args"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!PermissionUtils.checkAppPermissions(this)) {
            startActivity(new Intent(this, RequestPermissionActivity.class));
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        }

        LocationUtils.validateSwitchedOnGPS(GoogleMapActivity.this, new LocationUtils.OnSwitchedOnGPSListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception ex) {
                startActivity(new Intent(GoogleMapActivity.this, TurnOnGPSActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroyActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_google_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_leyenda) {
            ModalHelper.getBuilderAlertDialog(this)
                    .setView(R.layout.modal_leyenda_plan_de_viaje)
                    .create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setOnMapClickListener(latLng -> {
            if (presenter.isNavegacionPlanDeViajePermitido()) {
                if (binding.fabIniciarNavegacion.isShown()) {
                    binding.fabIniciarNavegacion.hide();
                } else {
                    binding.fabIniciarNavegacion.show();
                }
                if (binding.fabNotificarIncidente.isShown()) {
                    binding.fabNotificarIncidente.hide();
                } else {
                    binding.fabNotificarIncidente.show();
                }
            }
            if (binding.fabGestionarDespachos.isShown()) {
                binding.fabGestionarDespachos.hide();
            } else {
                binding.fabGestionarDespachos.show();
            }
        });

        googleMap.setOnMarkerClickListener(marker -> {
            presenter.onClickMarkerMap(marker.getTag());
            return false;
        });

        if (PermissionUtils.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            googleMap.setMyLocationEnabled(true);
        }

        presenter.onMapReady();
    }

    @Override
    public void addMarker(double latitude, double longitude, int resIcon, Object tag) {
        LatLng locationRuta = new LatLng(latitude, longitude);
        Marker marker = googleMap.addMarker(new MarkerOptions().
                position(locationRuta).icon(BitmapDescriptorFactory.fromResource(resIcon)));
        marker.setTag(tag);
        markers.add(marker);
    }

    @Override
    public void updateIconMarker(String tag, int resIcon) {
        for (int i = 0; i < markers.size(); i++) {
            if (markers.get(i).getTag().equals(tag)) {
                markers.get(i).setIcon(BitmapDescriptorFactory.fromResource(resIcon));
            }
        }
    }

    @Override
    public void centerCameraToMarkers() {
        LatLngBounds.Builder latlngBoundsBuilder = new LatLngBounds.Builder();
        LatLngBounds bounds;

        for (int i = 0; i < markers.size(); i++) {
            latlngBoundsBuilder.include(markers.get(i).getPosition());
        }

        if (markers.size() > 0) {
            bounds = latlngBoundsBuilder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getSupportFragmentManager().findFragmentById(R.id.googleMap).getView().getHeight();
            int padding = (int) (width * 0.15); // offset from edges of the map 12% of screen

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
    }

    @Override
    public void centerCameraToMyLocation(LatLng latLng, float zoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /*@Override
    public void animateCamera(LatLng target, float zoom, float tilt, float bearing) {
        CameraPosition c = new CameraPosition(target, zoom, tilt, bearing);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(c));
    }*/

    @Override
    public void showFABIniciarNavegacion() {
        new Handler().postDelayed(() -> {
            if (presenter.isNavegacionPlanDeViajePermitido()) {
                binding.fabIniciarNavegacion.show();
                binding.fabNotificarIncidente.show();
            }
            //if (presenter.isBtnGestionarParadaPermitido()) {
            binding.fabGestionarDespachos.show();
            //}
        }, 500);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.act_plan_de_viaje_title_mapa_paradas);

        binding.fabGestionarDespachos.setOnClickListener(v -> presenter.onClickGestionarDespachos());
        binding.fabIniciarNavegacion.setOnClickListener(v -> presenter.onClickIniciarNavegacion());
        binding.fabNotificarIncidente.setOnClickListener(v -> presenter.onClickReportarIncidencia());

        configGoogleMap();
    }

    private void configGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    public static Transition makeEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Transition fade = new Fade();
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            return fade;
        }
        return null;
    }
}
