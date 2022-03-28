package com.urbanoexpress.iridio3.ui.dialogs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ModalSelectOrigenPlanViajeBinding;
import com.urbanoexpress.iridio3.model.entity.PlanDeViaje;
import com.urbanoexpress.iridio3.ui.adapter.OrigenPlanViajeAdapter;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.ui.model.OrigenPlanViajeItem;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.util.LocationUtils;
import com.urbanoexpress.iridio3.util.MyLocation;
import com.urbanoexpress.iridio3.view.BaseModalsView;

import org.apache.commons.lang3.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mick on 14/03/17.
 */

public class SelectOrigenPlanViajeDialog extends DialogFragment implements OnClickItemListener {

    public static final String TAG = "SelectOrigenPlanViajeDialog";

    private ModalSelectOrigenPlanViajeBinding binding;
    private OnSelectedOrigenPlanViaje callback;

    private List<PlanDeViaje> origenPlanViaje;
    private List<OrigenPlanViajeItem> origenPlanViajeItems;

    private int indexSelectedItem = -1;

    public static SelectOrigenPlanViajeDialog newInstance(ArrayList<PlanDeViaje> origenPlanViaje) {
        SelectOrigenPlanViajeDialog dialog = new SelectOrigenPlanViajeDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("origenPlanViaje", origenPlanViaje);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (OnSelectedOrigenPlanViaje) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnSelectedOrigenPlanViaje");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalSelectOrigenPlanViajeBinding.inflate(inflater, container, false);
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
    public void onClickIcon(View view, int position) {

    }

    @Override
    public void onClickItem(View view, int position) {
        indexSelectedItem = position;
        setSelectItem(position);
    }

    private void setupViews() {
        binding.rvPlanViaje.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPlanViaje.setHasFixedSize(true);

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnContinuar.setOnClickListener(v -> {
            if (indexSelectedItem >= 0 ) {
                if (LocationUtils.getLatitude() != 0
                        && LocationUtils.getLongitude() != 0) {
                    boolean validateArrived = MyLocation.arrivedToLocation(getActivity(),
                            Double.parseDouble(
                                    origenPlanViaje.get(indexSelectedItem).getOrigen_latitude()),
                            Double.parseDouble(
                                    origenPlanViaje.get(indexSelectedItem).getOrigen_longitude()));

                    if (validateArrived) {
                        callback.onSelectedOrigenPlanViaje(
                                origenPlanViajeItems.get(indexSelectedItem)
                                        .getIdPlanViaje());
                        dismiss();
                    } else {
                        BaseModalsView.showAlertDialog(getActivity(),
                                R.string.act_plan_de_viaje_title_ubicacion_fuera_rango_origen_planviaje,
                                R.string.act_plan_de_viaje_msg_ubicacion_fuera_rango_origen_planviaje,
                                R.string.text_continuar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        callback.onSelectedOrigenPlanViaje(
                                                origenPlanViajeItems.get(indexSelectedItem)
                                                        .getIdPlanViaje());
                                        dismiss();
                                    }
                                }, R.string.text_cancelar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                    }
                } else {
                    callback.onSelectedOrigenPlanViaje(
                            origenPlanViajeItems.get(indexSelectedItem)
                                    .getIdPlanViaje());
                    dismiss();
                }
            } else {
                BaseModalsView.showToast(getActivity(),
                        R.string.act_plan_de_viaje_msg_seleccione_correctamente_origen,
                        Toast.LENGTH_SHORT);
            }
        });

        loadListaOrigen();
    }

    private void loadListaOrigen() {
        origenPlanViaje = (ArrayList<PlanDeViaje>) getArguments().getSerializable("origenPlanViaje");
        origenPlanViajeItems = new ArrayList<>();

        for (int i = 0; i < origenPlanViaje.size(); i++) {
            origenPlanViajeItems.add(new OrigenPlanViajeItem(
                    origenPlanViaje.get(i).getIdPlanViaje(),
                    WordUtils.capitalize(origenPlanViaje.get(i).getOrigen().toLowerCase()),
                    formatFecha(origenPlanViaje.get(i).getFecha()),
                    origenPlanViaje.get(i).getRuta(),
                    Color.parseColor("#00FFFFFF"),
                    false
            ));
        }

        OrigenPlanViajeAdapter adapter = new OrigenPlanViajeAdapter(
                getActivity(), origenPlanViajeItems, this);

        binding.rvPlanViaje.setAdapter(adapter);
    }

    private void setSelectItem(int position) {
        for (int i = 0; i < origenPlanViajeItems.size(); i++) {
            origenPlanViajeItems.get(i).setSelected(false);
            origenPlanViajeItems.get(i).setBackgroundColor(Color.parseColor("#00FFFFFF"));
        }

        origenPlanViajeItems.get(position).setSelected(true);
        origenPlanViajeItems.get(position).setBackgroundColor(Color.parseColor("#30000000"));

        if (CommonUtils.isAndroidLollipop()) {
            new Handler().postDelayed(() -> binding.rvPlanViaje.getAdapter().notifyDataSetChanged(), 260);
        } else {
            binding.rvPlanViaje.getAdapter().notifyDataSetChanged();
        }
    }


    private String formatFecha(String source) {
        String formatoFecha = source;
        try {
            Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(source);
            formatoFecha = new SimpleDateFormat("dd MMM").format(fecha);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return formatoFecha;
    }

    public interface OnSelectedOrigenPlanViaje {
        void onSelectedOrigenPlanViaje(String idPlanViaje);
    }
}
