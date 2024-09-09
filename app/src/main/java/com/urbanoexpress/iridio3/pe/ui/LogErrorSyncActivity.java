package com.urbanoexpress.iridio3.pe.ui;

import androidx.annotation.NonNull;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityRegistroErrorSincDatosBinding;
import com.urbanoexpress.iridio3.pe.databinding.ModalDetalleLogErrorSyncBinding;
import com.urbanoexpress.iridio3.pe.model.entity.LogErrorSync;
import com.urbanoexpress.iridio3.pe.ui.model.LogErrorSyncItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.LogErrorSyncAdapter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.pe.util.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogErrorSyncActivity extends AppThemeBaseActivity implements OnClickItemListener {

    private ActivityRegistroErrorSincDatosBinding binding;
    private List<LogErrorSync> logErrorSyncList = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroErrorSincDatosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (savedInstanceState != null) {
            Preferences.getInstance().init(this, "UserProfile");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registro_errores, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_eliminar_registro_errores) {
            if (logErrorSyncList.size() > 0) {
                LogErrorSync.deleteAll(LogErrorSync.class,
                        NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                        Preferences.getInstance().getString("idUsuario", ""));
                loadDataLogErrors();
                showToast("El registro de errores se eliminó correctamente.");
            } else {
                showToast("Actualmente no hay registro de errores.");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_registro_errores);

        binding.lvErrors.setLayoutManager(new LinearLayoutManager(this));
        binding.lvErrors.setHasFixedSize(true);

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadDataLogErrors);

        loadDataLogErrors();
    }

    private void loadDataLogErrors() {
        logErrorSyncList = LogErrorSync.find(LogErrorSync.class,
                NamingHelper.toSQLNameDefault("idUsuario") + " = ?",
                Preferences.getInstance().getString("idUsuario", ""));

        if (logErrorSyncList.size() > 0) {
            ArrayList<LogErrorSyncItem> data = new ArrayList<>();

            for (LogErrorSync error : logErrorSyncList) {
                LogErrorSyncItem item = new LogErrorSyncItem(
                        error.getTitulo(),
                        error.getMensaje(),
                        error.getTimestamp(),
                        generateIconToLogError(error.getTipo())
                );
                data.add(item);
            }

            LogErrorSyncAdapter adapter = new LogErrorSyncAdapter(LogErrorSyncActivity.this, data);
            adapter.setListener(LogErrorSyncActivity.this);
            binding.lvErrors.setAdapter(adapter);
            binding.swipeRefreshLayout.setRefreshing(false);
        } else {
            binding.boxListErrors.setVisibility(View.GONE);
        }
    }

    private int generateIconToLogError(int tipo) {
        switch (tipo) {
            case LogErrorSync.Tipo.GPS:
                return R.drawable.ic_map_marker_grey;
            case LogErrorSync.Tipo.IMAGEN:
                return R.drawable.ic_image_grey;
            case LogErrorSync.Tipo.GUIA_GESTIONADA:
                return R.drawable.ic_package_grey;
            case LogErrorSync.Tipo.ESTADO_RUTA:
                return R.drawable.ic_routes_grey;
            case LogErrorSync.Tipo.SECUENCIA_GUIA:
                return R.drawable.ic_package_grey;
            case LogErrorSync.Tipo.GESTION_LLAMADA:
                return R.drawable.ic_phone_grey;
        }
        return 0;
    }

    private String generateTipoDatoToLogError(int tipo) {
        switch (tipo) {
            case LogErrorSync.Tipo.GPS:
                return "Trama (GPS)";
            case LogErrorSync.Tipo.IMAGEN:
                return "Imagen";
            case LogErrorSync.Tipo.GUIA_GESTIONADA:
                return "Descarga";
            case LogErrorSync.Tipo.ESTADO_RUTA:
                return "Estado Ruta";
            case LogErrorSync.Tipo.SECUENCIA_GUIA:
                return "Secuencia Guia";
            case LogErrorSync.Tipo.GESTION_LLAMADA:
                return "Gestión Llamada";
        }
        return "Desconocido";
    }

    @Override
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {
        ModalDetalleLogErrorSyncBinding binding = ModalDetalleLogErrorSyncBinding
                .inflate(getLayoutInflater(), null, false);

        binding.lblTipoDato.setText(generateTipoDatoToLogError(logErrorSyncList.get(position).getTipo()));
        binding.lblTipoError.setText(logErrorSyncList.get(position).getTitulo());
        binding.lblMensaje.setText(logErrorSyncList.get(position).getMensaje());
        binding.lblCodigoError.setText(logErrorSyncList.get(position).getSeguimiento_pila());

        Date date = new Date(Long.parseLong(logErrorSyncList.get(position).getTimestamp()));

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String hora = new SimpleDateFormat("HH:mm:ss").format(date);

        binding.lblFecha.setText(fecha);
        binding.lblHora.setText(hora);

        ModalHelper.getBuilderAlertDialog(this).setView(binding.getRoot()).show();
    }
}
