package com.urbanoexpress.iridio3.pe.ui;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.orm.util.NamingHelper;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityFiltrarGuiaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Data;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;

import java.util.ArrayList;
import java.util.Arrays;

public class FiltrarGuiaActivity extends AppThemeBaseActivity {

    private ActivityFiltrarGuiaBinding binding;

    private String queryLineaNegocio = "";
    private String queryTipo = "";

    private ArrayList<String> queryParamsList = new ArrayList<>();

    private boolean[] checkedFiltros = new boolean[0];

    private boolean isClickedAplicarFiltros = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFiltrarGuiaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filtrar_guia, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_undo_filter) {
            ModalHelper.getBuilderAlertDialog(this)
                    .setTitle(R.string.act_filtrar_guia_title_restablecer_filtro)
                    .setMessage(R.string.act_filtrar_guia_msg_restablecer_filtro)
                    .setPositiveButton(R.string.text_restablecer, (dialog, which) -> {
                        dialog.dismiss();
                        binding.switchValorado.setChecked(true);
                        binding.switchLogisticaLiviana.setChecked(true);
                        binding.switchGuia.setChecked(true);
                        binding.switchGuiaValija.setChecked(true);
                        binding.switchLiquidacion.setChecked(true);
                        binding.switchDevolucion.setChecked(true);
                        binding.switchRecoleccion.setChecked(true);
                        binding.switchRecoleccionInversa.setChecked(true);
                        binding.switchRecoleccionValija.setChecked(true);
                    })
                    .setNegativeButton(R.string.text_cancelar, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isClickedAplicarFiltros) {
            sendOnAplicarFiltroGuiaReceiver();
        }
    }


    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_filtrar_guia);

        checkedFiltros = getIntent().getBundleExtra("args").getBooleanArray("checkedFiltros");

        binding.switchValorado.setChecked(checkedFiltros[0]);
        binding.switchLogisticaLiviana.setChecked(checkedFiltros[1]);
        binding.switchGuia.setChecked(checkedFiltros[2]);
        binding.switchGuiaValija.setChecked(checkedFiltros[3]);
        binding.switchLiquidacion.setChecked(checkedFiltros[4]);
        binding.switchDevolucion.setChecked(checkedFiltros[5]);
        binding.switchRecoleccion.setChecked(checkedFiltros[6]);
        binding.switchRecoleccionInversa.setChecked(checkedFiltros[7]);
        binding.switchRecoleccionValija.setChecked(checkedFiltros[8]);

        binding.switchValorado.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchLogisticaLiviana.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchGuia.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchGuiaValija.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchLiquidacion.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchDevolucion.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchRecoleccion.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchRecoleccionInversa.setOnCheckedChangeListener(checkedChangeListener);
        binding.switchRecoleccionValija.setOnCheckedChangeListener(checkedChangeListener);

        binding.btnAplicarFiltro.setOnClickListener(v -> {
            isClickedAplicarFiltros = true;
            buildFiltroLineaNegocio();
            buildFiltroTipo();
            finish();
            overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
        });
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked)
            -> checkedFiltros[Integer.valueOf(buttonView.getTag().toString())] = isChecked;

    private void buildFiltroLineaNegocio() {
        queryLineaNegocio = "";

        if (binding.switchValorado.isChecked()) {
            queryLineaNegocio = "2";
        }

        if (binding.switchLogisticaLiviana.isChecked()) {
            queryLineaNegocio += queryLineaNegocio.isEmpty() ? "3" : ",3";
        }
    }

    private void buildFiltroTipo() {
        queryTipo = "";
        queryParamsList = new ArrayList<>(Arrays.asList(
                Preferences.getInstance().getString("idUsuario", ""),
                Data.Delete.NO + "",
                Ruta.EstadoDescarga.PENDIENTE + ""));

        if (binding.switchGuia.isChecked()) {
            addQueryTipo();
            queryParamsList.add("E");
            queryParamsList.add("P");

            // Para compatibilidad de los tipos de las antiguas guias en versiones anteriores del iridio
            addQueryTipo();
            queryParamsList.add("E");
            queryParamsList.add("E");
        }

        if (binding.switchGuiaValija.isChecked()) {
            addQueryTipo();
            queryParamsList.add("E");
            queryParamsList.add("V");
        }

        if (binding.switchLiquidacion.isChecked()) {
            addQueryTipo();
            queryParamsList.add("E");
            queryParamsList.add("L");
        }

        if (binding.switchDevolucion.isChecked()) {
            addQueryTipo();
            queryParamsList.add("E");
            queryParamsList.add("D");
        }

        if (binding.switchRecoleccion.isChecked()) {
            addQueryTipo();
            queryParamsList.add("R");
            queryParamsList.add("P");
        }

        if (binding.switchRecoleccionInversa.isChecked()) {
            addQueryTipo();
            queryParamsList.add("R");
            queryParamsList.add("I");
        }

        if (binding.switchRecoleccionValija.isChecked()) {
            addQueryTipo();
            queryParamsList.add("R");
            queryParamsList.add("V");
        }

        if (!queryTipo.isEmpty()) {
            queryTipo = " and (" + queryTipo + ")";
        }
    }

    private void addQueryTipo() {
        if (queryTipo.isEmpty()) {
            queryTipo = "(" + NamingHelper.toSQLNameDefault("tipo") + " = ? and "
                    + NamingHelper.toSQLNameDefault("tipoEnvio") + " = ?" + ")";
        } else {
            queryTipo += " or (" + NamingHelper.toSQLNameDefault("tipo") + " = ? and "
                    + NamingHelper.toSQLNameDefault("tipoEnvio") + " = ?" + ")";
        }
    }

    /**
     * Receiver
     *
     * {@link TransferirGuiaPresenter#aplicarFiltroGuiaReceiver}
     */
    private void sendOnAplicarFiltroGuiaReceiver() {
        Intent intent = new Intent(LocalAction.APLICAR_FILTRO_GUIA_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString("queryLineaNegocio", queryLineaNegocio);
        bundle.putString("queryTipo", queryTipo);
        bundle.putSerializable("queryParamsList", queryParamsList);
        bundle.putBooleanArray("checkedFiltros", checkedFiltros);
        intent.putExtra("args", bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
