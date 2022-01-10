package com.urbanoexpress.iridio.ui;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.data.rest.ApiRequest;
import com.urbanoexpress.iridio.data.rest.ApiRest;
import com.urbanoexpress.iridio.databinding.ActivityConfiguracionBinding;
import com.urbanoexpress.iridio.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.util.Session;
import com.urbanoexpress.iridio.util.network.Connection;
import com.urbanoexpress.iridio.util.network.Connectivity;

import org.apache.commons.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mick on 22/09/16.
 */

public class ConfiguracionActivity extends AppThemeBaseActivity {

    private ActivityConfiguracionBinding binding;
    private LocationManager locationManager;
    private boolean onlySectionAcercade = false;
    private String devicePhone = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Preferences.getInstance().init(this, "GlobalConfigApp");
        devicePhone = Preferences.getInstance().getString("phone", "");

        Preferences.getInstance().init(this, "UserProfile");

        try {
            onlySectionAcercade = getIntent().getExtras().getBundle("args").getBoolean("onlySectionAcercade");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!onlySectionAcercade) {
            binding.switchGPS.setChecked(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            setIconStatusInternet();
        }
    }

    @Override
    protected void animOnStartActivity() {
        overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(onlySectionAcercade
                ? R.string.text_acerca_de : R.string.title_activity_configuracion);

        binding.btnInfoIridio.setOnClickListener(v ->
                startActivity(new Intent(this, AcercaDeActivity.class)));

        binding.btnTerminosCondiciones.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.url_legal_terms_service)));
            startActivity(intent);
        });

        binding.btnPoliticaPrivacidad.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.url_legal_privacy_policy)));
            startActivity(intent);
        });

        binding.btnActivarGPS.setOnClickListener(v -> {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSnackBar("El servicio de ubicación está activado.");
            } else {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        binding.btnPanelPerfilUsuario.setOnClickListener(v ->
                startActivity(new Intent(this, UserProfileActivity.class)));

        binding.btnPanelRegistroDeErrores.setOnClickListener(v ->
                startActivity(new Intent(this, LogErrorSyncActivity.class)));

        binding.btnEstadoConexionInternet.setOnClickListener(v -> openModalDetalleServicioInternet());

        binding.btnValidateVersionApp.setOnClickListener(v -> validateVersionApp());

        loadSections();
        loadDatosUsuario();
        binding.lblVersionApp.setText(getString(R.string.text_version) + ": " +
                CommonUtils.getPackageInfo(ConfiguracionActivity.this).versionName);
    }

    private void loadSections() {
        if (onlySectionAcercade) {
            binding.sectionCuenta.setVisibility(View.GONE);
            binding.sectionGeneral.setVisibility(View.GONE);
        }
    }

    private void loadDatosUsuario() {
        if (!onlySectionAcercade) {
            String tipoUsuario = Session.getUser().getTipoUsuario().equals("I") ? "Interno" : "Externo";
            String nombreUsuario = (Session.getUser().getNombre().length() > 0)
                    ? WordUtils.capitalize(Session.getUser().getNombre().toLowerCase())
                    : Session.getUser().getUsuario();

            binding.lblNomApeUsuario.setText(nombreUsuario);
            binding.lblTipoUsuario.setText("Tipo de usuario: " + tipoUsuario);
        }
    }

    private void validateVersionApp() {
        if (Connection.hasNetworkConnectivity(ConfiguracionActivity.this)) {
            ApiRequest.getInstance().newParams();
            ApiRequest.getInstance().putParams("version_name",
                    CommonUtils.getPackageInfo(ConfiguracionActivity.this).versionName);
            ApiRequest.getInstance().putParams("version_code",
                    CommonUtils.getPackageInfo(ConfiguracionActivity.this).versionCode + "");
            ApiRequest.getInstance().putParams("device_imei", devicePhone);
            ApiRequest.getInstance().putParams("device_model", Build.MODEL);
            ApiRequest.getInstance().putParams("version_os", Build.VERSION.RELEASE);
            try {
                ApiRequest.getInstance().putParams("vp_id_user", Session.getUser().getIdUsuario());
            } catch (NullPointerException ex) {
                ApiRequest.getInstance().putParams("vp_id_user", "MS16");
            }
            ApiRequest.getInstance().request(ApiRest.getInstance().getApiBaseUrl() +
                            ApiRest.Api.VALIDATE_VERSION_APP,
                    ApiRequest.TypeParams.FORM_DATA, new ApiRequest.ResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getBoolean("is_updated")) {
                                    ModalHelper.getBuilderAlertDialog(ConfiguracionActivity.this)
                                            .setMessage(response.getString("message"))
                                            .setPositiveButton(R.string.text_aceptar, null)
                                            .show();
                                } else {
                                    ModalHelper.getBuilderAlertDialog(ConfiguracionActivity.this)
                                            .setMessage(response.getString("message"))
                                            .setPositiveButton(R.string.text_actualizar,
                                                    (dialog, which) -> openPlayStoreForUpdateIridio())
                                            .show();
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
        } else {
            showMessageNotConnectedToNetwork();
        }
    }

    private void openPlayStoreForUpdateIridio() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void setIconStatusInternet() {
        if (Connectivity.isConnected(ConfiguracionActivity.this)) {
            Log.d("ConfiguracionActivity", "Icon: Red Conectado.");
            binding.imgStatusInternet.setImageDrawable(
                    ContextCompat.getDrawable(ConfiguracionActivity.this,
                            R.drawable.ic_wifi_status_connection_ok));
            if (!Connectivity.isConnectedFast(ConfiguracionActivity.this)) {
                Log.d("ConfiguracionActivity", "Icon: Red Lento.");
                binding.imgStatusInternet.setImageDrawable(
                        ContextCompat.getDrawable(ConfiguracionActivity.this,
                                R.drawable.ic_wifi_status_connection_slow));
            }
        } else {
            Log.d("ConfiguracionActivity", "Icon: Red Desconectado.");
            binding.imgStatusInternet.setImageDrawable(
                    ContextCompat.getDrawable(ConfiguracionActivity.this,
                            R.drawable.ic_wifi_status_connection_none));
        }
    }

    private void openModalDetalleServicioInternet() {
        TextView lblServicioRed, lblTipoRed, lblVelocidadRed;

        View viewModal = getLayoutInflater().inflate(R.layout.modal_estado_conexion_internet, null);

        lblServicioRed = viewModal.findViewById(R.id.lblServicioRed);
        lblTipoRed = viewModal.findViewById(R.id.lblTipoRed);
        lblVelocidadRed = viewModal.findViewById(R.id.lblVelocidadRed);

        if (Connectivity.isConnected(ConfiguracionActivity.this)) {
            lblServicioRed.setText("Conectado");
            if (Connectivity.isConnectedWifi(ConfiguracionActivity.this)) {
                lblTipoRed.setText("WiFi");
            } else {
                lblTipoRed.setText("Mobil");
            }

            if (Connectivity.isConnectedFast(ConfiguracionActivity.this)) {
                lblVelocidadRed.setText("Rapida");
            } else {
                lblVelocidadRed.setText("Lenta");
            }
        } else {
            lblServicioRed.setText("Desconectado");
            lblTipoRed.setText("Desconectado");
            lblVelocidadRed.setText("Desconectado");
        }

        ModalHelper.getBuilderAlertDialog(ConfiguracionActivity.this)
                .setView(viewModal).show();
    }

}
