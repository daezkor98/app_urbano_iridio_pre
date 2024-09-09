package com.urbanoexpress.iridio3.pe.ui.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ModalEditarPlacaBinding;
import com.urbanoexpress.iridio3.pe.model.entity.Ruta;
import com.urbanoexpress.iridio3.pe.model.interactor.RutaPendienteInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.model.util.ModelUtils;
import com.urbanoexpress.iridio3.pe.ui.model.RutaEditarPlacaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.RutaEditarPlacaAdapter;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mick on 07/04/17.
 */

public class EditarPlacaDialog extends BaseDialogFragment implements
        RutaEditarPlacaAdapter.OnCheckedChangeItemListener {

    public static final String TAG = "EditarPlacaDialog";

    private ModalEditarPlacaBinding binding;
    private RutaPendienteInteractor interactor;

    private List<Ruta> rutas;

    private ArrayList<RutaEditarPlacaItem> rutaItems = new ArrayList<>();

    private int selectedIndexRuta = -1;

    public static EditarPlacaDialog newInstance() {
        return new EditarPlacaDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        binding = ModalEditarPlacaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getDialog().getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
        if (selectedIndexRuta >= 0) {
            binding.rvRutas.getAdapter().notifyItemChanged(selectedIndexRuta);
        }

        selectedIndexRuta = position;
    }

    private void setupViews() {
        interactor = new RutaPendienteInteractor(getActivity());

        binding.rvRutas.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvRutas.setHasFixedSize(true);

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnGuardar.setOnClickListener(v -> {
            if (binding.txtPlaca.getText().toString().trim().length() > 0) {
                if (selectedIndexRuta >= 0) {
                    guardarPlaca(binding.txtPlaca.getText().toString().trim());
                } else {
                    CommonUtils.vibrateDevice(getActivity(), 100);
                    showToast(R.string.activity_ruta_message_seleccione_ruta);
                }
            } else {
                CommonUtils.vibrateDevice(getActivity(), 100);
                showToast(R.string.activity_ruta_message_ingrese_placa);
            }
        });

        loadRutas();
    }

    private void loadRutas() {
        rutas = interactor.selectIdRutas();

        if (rutas.size() > 1) {
            binding.boxRutas.setVisibility(View.VISIBLE);

            for (int i = 0; i < rutas.size(); i++) {
                rutaItems.add(new RutaEditarPlacaItem(
                        rutas.get(i).getIdRuta(),
                        ModelUtils.getIconLineaNegocio(rutas.get(i))
                ));
            }

            RutaEditarPlacaAdapter adapter = new RutaEditarPlacaAdapter(getActivity(), rutaItems);
            adapter.setListener(this);
            binding.rvRutas.setAdapter(adapter);
        } else {
            selectedIndexRuta = 0;
        }
    }

    public void guardarPlaca(String placa) {
        if (CommonUtils.validateConnectivity(getActivity())) {
            showProgressDialog(R.string.activity_ruta_message_actualizando_placa);
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        dismissProgressDialog();
                        if (response.getBoolean("success")) {
                            showToast(R.string.activity_ruta_message_placa_editada_correctamente);
                            dismiss();
                        } else {
                            CommonUtils.vibrateDevice(getActivity(), 100);
                            showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        dismissProgressDialog();
                        showToast(R.string.json_object_exception);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    dismissProgressDialog();
                    showToast(R.string.volley_error_message);
                }
            };

            String[] params = new String[] {
                    rutas.get(selectedIndexRuta).getIdRuta(),
                    placa,
                    rutas.get(selectedIndexRuta).getLineaNegocio(),
                    Preferences.getInstance().getString("idUsuario", "")
            };

            RutaPendienteInteractor.editPlaca(params, callback);
        }
    }
}