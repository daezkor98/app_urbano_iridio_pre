package com.urbanoexpress.iridio3.pre.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.databinding.FragmentRutasBinding;
import com.urbanoexpress.iridio3.pre.presenter.RutaPendientePresenter;
import com.urbanoexpress.iridio3.pre.services.DataSyncService;
import com.urbanoexpress.iridio3.pre.ui.InitActivity;
import com.urbanoexpress.iridio3.pre.ui.adapter.ParadaAdapter;
import com.urbanoexpress.iridio3.pre.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pre.ui.interfaces.OnActionModeListener;
import com.urbanoexpress.iridio3.pre.ui.model.ParadaRutaItem;
import com.urbanoexpress.iridio3.pre.ui.model.RutaItem;
import com.urbanoexpress.iridio3.pre.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio3.pre.util.AnimationUtils;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.util.SimpleItemTouchHelperCallback;
import com.urbanoexpress.iridio3.pre.util.androidsdkfixs.itemtouchelper.ItemTouchHelper;
import com.urbanoexpress.iridio3.pre.view.RutaPendienteView;
import com.urbanoexpress.iridio3.pre.work.UserStatusWorker;

public class RutaPendienteFragment extends BaseFragment implements RutaPendienteView,
        RutaAdapter.OnClickGuiaItemListener, ActionMode.Callback, ParadaAdapter.OnParadaClickListener {

    private FragmentRutasBinding binding;
    private RutaPendientePresenter presenter;
    private OnActionModeListener onActionModeListener;
    private ActionMode actionMode;
    private ItemTouchHelper touchHelper;
    private RutaAdapter rutaAdapter;
    private Menu menu;
    private ParadaAdapter paradaAdapter;
    private Map<Integer, List<RutaItem>> mapaParadas = new HashMap<>();
    private List<Integer> idsParadas = new ArrayList<>();
    private List<RutaItem> todasLasGuiasPendientes = new ArrayList<>();

    public RutaPendienteFragment() {
    }

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
    public void showParadasAgrupadas(List<RutaItem> rutas) {
        try {
            if (rutas.size() > 0) {
                binding.rvRutas.setBackgroundColor(Color.parseColor("#f1f1f1"));
            } else {
                binding.rvRutas.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            todasLasGuiasPendientes = rutas;
            agruparPorParadas(rutas);

            paradaAdapter = new ParadaAdapter(getActivity(), this, idsParadas, mapaParadas, rutas);
            binding.rvRutas.setAdapter(paradaAdapter);
            clearAttachRecyclerView();
            addAttachRecyclerView();
//            agruparPorParadas(rutas);
//            mostrarParadasEnLista(rutas);
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
                new SimpleItemTouchHelperCallback(getActivity(), paradaAdapter, presenter);
//        ItemTouchHelper.Callback callback =
//                new SimpleItemTouchHelperCallback(getActivity(), rutaAdapter, presenter);

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
    public void onParadaClick(int paradaId, int position) {
        manejarClickParada(paradaId);
    }

    @Override
    public void showAuthenticationError() {
        ModalHelper.getBuilderAlertDialog(getActivity())
                .setTitle(R.string.pending_route_title_token_error)
                .setMessage(R.string.pending_route_msg_token_error)
                .setCancelable(false)
                .setPositiveButton(R.string.pending_route_phone_accept, (dialog, which) -> closeUSerSession())
                .show();
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

    private void agruparPorParadas(List<RutaItem> rutas) {
        mapaParadas.clear();
        idsParadas.clear();

        for (RutaItem ruta : rutas) {
            int paradaId = ruta.getParadaId();

            if (!mapaParadas.containsKey(paradaId)) {
                mapaParadas.put(paradaId, new ArrayList<>());
                idsParadas.add(paradaId);
            }

            mapaParadas.get(paradaId).add(ruta);
        }
    }

//    private void mostrarParadasEnLista(List<RutaItem> rutas) {
//        if (paradaAdapter == null) {
//            paradaAdapter = new ParadaAdapter(getActivity(),
//                    new ParadaAdapter.OnParadaClickListener() {
//                        @Override
//                        public void onParadaClick(int paradaId, int position) {
//                            manejarClickParada(paradaId);
//                        }
//                    }, idsParadas, mapaParadas, rutas);
//
//            binding.rvRutas.setAdapter(paradaAdapter);
//            clearAttachRecyclerView();
//            addAttachRecyclerView();
//        } else {
//            paradaAdapter.setData(idsParadas, mapaParadas);
//            paradaAdapter.notifyDataSetChanged();
//        }
//    }

    private void manejarClickParada(int paradaId) {
        List<RutaItem> guiasEnParada = mapaParadas.get(paradaId);

        if (guiasEnParada == null || guiasEnParada.isEmpty()) {
            return;
        }

        // Si solo tiene 1 guía, ir directo a gestionar
        if (guiasEnParada.size() == 1) {
            RutaItem guia = guiasEnParada.get(0);
            int posicion = encontrarPosicionGuia(guia);
            if (posicion >= 0) {
                if (actionMode == null) {
                    presenter.onClickItem(posicion);
                } else {
                    presenter.onSelectedItem(posicion);
                }
            }
        } else {
            mostrarModalGuiasParada(paradaId, guiasEnParada);
        }
    }

    private int encontrarPosicionGuia(RutaItem guiaBuscada) {
        for (int i = 0; i < todasLasGuiasPendientes.size(); i++) {
            if (todasLasGuiasPendientes.get(i).getGuia().equals(guiaBuscada.getGuia())) {
                return i;
            }
        }
        return -1;
    }

    private void mostrarModalGuiasParada(int paradaId, List<RutaItem> guias) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_parada_guias, null);
        builder.setView(dialogView);

        TextView txtTitulo = dialogView.findViewById(R.id.txtTituloParada);
        txtTitulo.setText("Parada #" + paradaId);

        RecyclerView rvGuias = dialogView.findViewById(R.id.rvGuiasParada);
        rvGuias.setLayoutManager(new LinearLayoutManager(getActivity()));

        RutaAdapter guiasAdapter = new RutaAdapter(getActivity(),
                new RutaAdapter.OnClickGuiaItemListener() {
                    @Override
                    public void onClickGuiaItem(View view, int position) {
                        // Cuando se selecciona una guía en el modal
                        RutaItem guiaSeleccionada = guias.get(position);
                        int posicionReal = encontrarPosicionGuia(guiaSeleccionada);

                        if (posicionReal >= 0) {
                            // Cerrar el modal
                            AlertDialog dialog = (AlertDialog) view.getRootView().getTag();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            // Manejar la selección
                            if (actionMode == null) {
                                presenter.onClickItem(posicionReal);
                            } else {
                                presenter.onSelectedItem(posicionReal);
                            }
                        }
                    }

                    @Override
                    public void onClickGuiaIconLinea(View view, int position) {
                        // Opcional: manejar click en icono
                    }

                    @Override
                    public void onClickGuiaIconImporte(View view, int position) {
                        // Opcional: manejar click en importe
                    }

                    @Override
                    public void onClickGuiaIconTipoEnvio(View view, int position) {
                        // Opcional: manejar click en tipo de envío
                    }
                }, guias);

        rvGuias.setAdapter(guiasAdapter);

        // Crear y mostrar el dialog
        AlertDialog dialog = builder.create();

        // Configurar botón de cerrar
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cerrar",
                (dialogInterface, which) -> dialogInterface.dismiss());

        dialog.show();
    }

    private void closeUSerSession() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork(UserStatusWorker.TAG);

        new Thread(() -> {
            CommonUtils.deleteUserData();
            Session.clearSession();
        }).start();

        requireActivity().stopService(new Intent(getActivity(), DataSyncService.class));
        requireActivity().startActivity(new Intent(getActivity(), InitActivity.class));
        requireActivity().finish();
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

