package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.FragmentRutasBinding;
import com.urbanoexpress.iridio3.pe.presenter.RutaPendientePresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.ui.interfaces.OnActionModeListener;
import com.urbanoexpress.iridio3.pe.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio3.pe.util.AnimationUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.SimpleItemTouchHelperCallback;
import com.urbanoexpress.iridio3.pe.util.androidsdkfixs.itemtouchelper.ItemTouchHelper;
import com.urbanoexpress.iridio3.pe.view.RutaPendienteView;

public class RutaPendienteFragment extends BaseFragment implements RutaPendienteView,
        RutaAdapter.OnClickGuiaItemListener, ActionMode.Callback {

    private FragmentRutasBinding binding;
    private RutaPendientePresenter presenter;
    private OnActionModeListener onActionModeListener;
    private ActionMode actionMode;
    private ItemTouchHelper touchHelper;
    private RutaAdapter rutaAdapter;
    private Menu menu;

    public RutaPendienteFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
            presenter = new RutaPendientePresenter(this);
        }

        return binding.getRoot();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_menu_ruta_pendiente, menu);
        this.menu = menu;
        clearAttachRecyclerView();
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
        addAttachRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
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
            rutaAdapter = new RutaAdapter(getActivity(), this, rutasPendientes);
            binding.rvRutas.setAdapter(rutaAdapter);
            clearAttachRecyclerView();
            addAttachRecyclerView();
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
                RecyclerView.ViewHolder viewHolder
                        = binding.rvRutas.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    AnimationUtils.setAnimationBlinkEffect(
                            viewHolder.itemView.findViewById(R.id.bgLinearLayout));
                }
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
    public void clearAttachRecyclerView() {
        if (touchHelper != null) {
            touchHelper.attachToRecyclerView(null);
        }
    }

    @Override
    public void addAttachRecyclerView() {
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(getActivity(), rutaAdapter, presenter);

        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvRutas);
    }

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
        return false;
    }

    @Override
    public void onClickGuiaItem(View view, int position) {
        if (actionMode == null) {
            presenter.onClickItem(position);
        } else {
            onClickGuiaIconLinea(view, position);
        }
    }

    @Override
    public void onClickGuiaIconLinea(View view, int position) {
        if (!binding.swipeRefreshLayout.isRefreshing()) {
            presenter.onSelectedItem(position);
        }
    }

    @Override
    public void onClickGuiaIconImporte(View view, int position) {
        presenter.onClickImportePorCobrar(position);
    }

    @Override
    public void onClickGuiaIconTipoEnvio(View view, int position) {
        presenter.onClickTipoEnvio(position);
    }

    @Override
    public void showMessageNuevaRutaAsignada() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.activity_ruta_title_nueva_ruta_asignada)
                .setMessage(R.string.activity_ruta_message_nueva_ruta_asignada)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showMessageNoHayRutaDisponible() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.fragment_ruta_pendiente_titulo_no_hay_rutas)
                .setMessage(R.string.fragment_ruta_pendiente_message_no_hay_rutas)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showMessageRutaNoIniciada() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.activity_detalle_ruta_title_ruta_no_iniciada)
                .setMessage(R.string.activity_detalle_ruta_message_debe_iniciar_ruta)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
    }

    @Override
    public void showMessageRutaFinalizada() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.activity_detalle_ruta_title_ruta_finalizada)
                .setMessage(R.string.activity_detalle_ruta_message_ruta_finalizada)
                .setPositiveButton(R.string.text_aceptar, null)
                .show();
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