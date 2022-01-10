package com.urbanoexpress.iridio.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.urbanoexpress.iridio.R;
import com.urbanoexpress.iridio.databinding.ActivityTransferirGuiaBinding;
import com.urbanoexpress.iridio.presenter.TransferirGuiaPresenter;
import com.urbanoexpress.iridio.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio.ui.dialogs.TransferirGuiaDialog;
import com.urbanoexpress.iridio.ui.model.RutaItem;
import com.urbanoexpress.iridio.util.CommonUtils;
import com.urbanoexpress.iridio.util.Preferences;
import com.urbanoexpress.iridio.view.TransferirGuiaView;

import java.util.List;

public class TransferirGuiaActivity extends AppThemeBaseActivity implements TransferirGuiaView,
        ActionMode.Callback, RutaAdapter.OnClickGuiaItemListener {

    private ActivityTransferirGuiaBinding binding;
    private TransferirGuiaPresenter presenter;
    private ActionMode actionMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferirGuiaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        Preferences.getInstance().init(this, "UserProfile");

        if (presenter == null) {
            presenter = new TransferirGuiaPresenter(this);
            presenter.init();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trasnferir, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            presenter.onActionFiltros();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_menu_transferir_guia, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_check_all) {
            presenter.selectAllItems();
            return true;
        } else if (item.getItemId() == R.id.action_transferir_guias) {
            presenter.transferirGuias();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        CommonUtils.changeColorStatusBar(this, R.color.colorPrimaryDark);
        presenter.deselectAllItems();
    }

    @Override
    public void showGuias(List<RutaItem> guias) {
        RutaAdapter adapter = new RutaAdapter(this, this, guias);
        binding.rvGuias.setAdapter(adapter);
    }

    @Override
    public void showActionMode() {
        if (actionMode == null) {
            actionMode = startSupportActionMode(this);
            CommonUtils.changeColorStatusBar(this, R.color.gris_7);
        }
    }

    @Override
    public void hideActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        binding.rvGuias.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyItemInsert(int position) {
        binding.rvGuias.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyItemRemove(int position) {
        binding.rvGuias.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyAllItemChanged() {
        binding.rvGuias.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void setTitleActionMode(String title) {
        if (actionMode != null) {
            actionMode.setTitle(title);
        }
    }

    @Override
    public void navigateToFiltrarGuiaActivity(boolean[] checkedFiltros) {
        Intent intent = new Intent(this, FiltrarGuiaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBooleanArray("checkedFiltros", checkedFiltros);
        intent.putExtra("args", bundle);
        startActivity(intent);
    }

    @Override
    public void navigateToTransferirGuiaDialog(String[] guias, String idZona, String lineaNegocio) {
        TransferirGuiaDialog dialog = new TransferirGuiaDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("guias", guias);
        bundle.putString("idZona", idZona);
        bundle.putString("lineaNegocio", lineaNegocio);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), TransferirGuiaDialog.TAG);
    }

    @Override
    public void onClickGuiaItem(View view, int position) {
        presenter.onSelectedItem(position);
    }

    @Override
    public void onClickGuiaIconLinea(View view, int position) {
        presenter.onSelectedItem(position);
    }

    @Override
    public void onClickGuiaIconImporte(View view, int position) {

    }

    @Override
    public void onClickGuiaIconTipoEnvio(View view, int position) {

    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_transferir_guia);

        binding.rvGuias.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGuias.setHasFixedSize(true);
    }
}
