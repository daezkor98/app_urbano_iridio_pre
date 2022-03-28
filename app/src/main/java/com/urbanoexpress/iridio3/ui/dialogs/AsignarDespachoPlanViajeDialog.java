package com.urbanoexpress.iridio3.ui.dialogs;

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

import com.urbanoexpress.iridio3.databinding.ModalAsignarDespachoPlanViajeBinding;
import com.urbanoexpress.iridio3.presenter.AsignarDespachoPresenter;
import com.urbanoexpress.iridio3.ui.adapter.AsignarDespachoAdapter;
import com.urbanoexpress.iridio3.ui.model.AsignarDespachoItem;
import com.urbanoexpress.iridio3.util.RecyclerTouchListener;
import com.urbanoexpress.iridio3.view.AsignarDespachoView;

import java.util.List;

/**
 * Created by mick on 29/11/16.
 */

public class AsignarDespachoPlanViajeDialog extends BaseDialogFragment implements AsignarDespachoView {

    public static final String TAG = AsignarDespachoPlanViajeDialog.class.getSimpleName();

    private ModalAsignarDespachoPlanViajeBinding binding;
    private AsignarDespachoPresenter presenter;

    public static AsignarDespachoPlanViajeDialog newInstance() {
        AsignarDespachoPlanViajeDialog fragment = new AsignarDespachoPlanViajeDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalAsignarDespachoPlanViajeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupViews();
        presenter = new AsignarDespachoPresenter(this);
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
    public void showDespachos(List<AsignarDespachoItem> despachos) {
        try {
            AsignarDespachoAdapter adapter = new AsignarDespachoAdapter(getActivity(), despachos);
            binding.lvDespachos.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        binding.lvDespachos.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyItemInsert(int position) {
        binding.lvDespachos.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyItemRemove(int position) {
        binding.lvDespachos.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyAllItemChanged() {
        binding.lvDespachos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setMensaje(String mensaje) {
        binding.lblMensaje.setText(mensaje);
    }

    @Override
    public void setVisibilityMensaje(int visibilityMensaje) {
        binding.boxMensaje.setVisibility(visibilityMensaje);
    }

    private void setupViews() {
        binding.lvDespachos.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.lvDespachos.setHasFixedSize(true);
        binding.lvDespachos.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity(),
                        binding.lvDespachos, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        presenter.onClickItemDespacho(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnAsignar.setOnClickListener(v -> presenter.onClickAsignarDespachos());
    }

}
