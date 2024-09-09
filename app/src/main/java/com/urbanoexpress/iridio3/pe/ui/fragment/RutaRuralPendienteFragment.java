package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.FragmentRutasBinding;
import com.urbanoexpress.iridio3.pe.presenter.RutaRuralPendientePresenter;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnActionModeListener;
import com.urbanoexpress.iridio3.pe.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio3.pe.util.AnimationUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.RutaPendienteView;

import java.util.List;

public class RutaRuralPendienteFragment extends BaseFragment implements RutaPendienteView,
        ActionMode.Callback {

    private FragmentRutasBinding binding;
    private RutaRuralPendientePresenter presenter;
    private OnActionModeListener onActionModeListener;
    private ActionMode actionMode;
    private Menu menu;

    public RutaRuralPendienteFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onActionModeListener = (OnActionModeListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRutasBinding.inflate(inflater, container, false);

        setupViews();

        if (presenter == null) {
            presenter = new RutaRuralPendientePresenter(this);
            presenter.init();
        }

        return binding.getRoot();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_menu_ruta_pendiente, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_gestion_multiple) {
            presenter.onActionGestionMultiple();
            return true;
        } else if (id == R.id.action_check_all) {
            presenter.onActionSelectAllRutasPendientes();
            return true;
        } else if (id == R.id.action_ordenar_guias) {
            presenter.onActionOrdenarGuias();
            return true;
        } else if (id == R.id.action_definir_posicion_guia) {
            presenter.onActionDefinirPosicionGuia();
            return true;
        } else if (id == R.id.action_guardar_orden_guias) {
            presenter.onActionGuardarOrdenGuias();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        onActionModeListener.onCloseActionMode();
        presenter.onClickHomeButtonOnSelectedItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.onDestroyActivity();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void showDatosRutasPendientes(List<RutaItem> rutasPendientes) {
        try {
            if (rutasPendientes.size() > 0) {
                binding.rvRutas.setBackgroundColor(Color.parseColor("#f1f1f1"));
            } else {
                binding.rvRutas.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            RutaAdapter rutaAdapter = new RutaAdapter(getActivity(), presenter, rutasPendientes);
            binding.rvRutas.setAdapter(rutaAdapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        try {
            binding.rvRutas.getAdapter().notifyItemChanged(position);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyItemInsert(int position) {
        try {
            binding.rvRutas.getAdapter().notifyItemInserted(position);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyItemRemove(int position) {
        try {
            binding.rvRutas.getAdapter().notifyItemRemoved(position);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyAllItemChanged() {
        try {
            binding.rvRutas.getAdapter().notifyDataSetChanged();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void scrollToPosition(final int position) {
        try {
            binding.rvRutas.scrollToPosition(position);
            new Handler().postDelayed(() -> getActivity().runOnUiThread(() -> {
                View view = binding.rvRutas.findViewHolderForAdapterPosition(position).itemView;
                view = view.findViewById(R.id.bgLinearLayout);
                AnimationUtils.setAnimationBlinkEffect(view);
            }), 1000);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isActiveActionMode() {
        return actionMode != null;
    }

    @Override
    public void showActionMode() {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(this);
            CommonUtils.changeColorStatusBar(getActivity(), R.color.gris_7);
            onActionModeListener.onShowActionMode();
        }
    }

    @Override
    public void hideActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void setTitleActionMode(String title) {
        if (actionMode != null) {
            actionMode.setTitle(title);
        }
    }

    @Override
    public void clearAttachRecyclerView() { }

    @Override
    public void addAttachRecyclerView() { }

    @Override
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    @Override
    public Menu getMenuActionMode() {
        return menu;
    }

    @Override
    public boolean isRefreshingSwipeRefreshLayout() {
        return binding.swipeRefreshLayout.isRefreshing();
    }

    @Override
    public void showMessageNuevaRutaAsignada() {

    }

    @Override
    public void showMessageNoHayRutaDisponible() {

    }

    @Override
    public void showMessageRutaNoIniciada() {

    }

    @Override
    public void showMessageRutaFinalizada() {

    }

    private void setupViews() {
        binding.rvRutas.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvRutas.setHasFixedSize(true);

        binding.swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorGreyUrbano, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (actionMode != null) {
                setVisibilitySwipeRefreshLayout(false);
            } else {
                presenter.onSwipeRefresh();
            }
        });
    }
}