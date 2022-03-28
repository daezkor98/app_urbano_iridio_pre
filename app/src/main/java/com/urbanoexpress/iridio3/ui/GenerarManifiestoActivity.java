package com.urbanoexpress.iridio3.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityGenerarManifiestoBinding;
import com.urbanoexpress.iridio3.presenter.GenerarManifiestoPresenter;
import com.urbanoexpress.iridio3.ui.adapter.CodigoBarraAdapter;
import com.urbanoexpress.iridio3.ui.interfaces.OnClickItemListener;
import com.urbanoexpress.iridio3.ui.model.CodigoBarraItem;
import com.urbanoexpress.iridio3.util.CommonUtils;
import com.urbanoexpress.iridio3.view.GenerarManifiestoView;

public class GenerarManifiestoActivity extends AppThemeBaseActivity implements GenerarManifiestoView,
        OnClickItemListener, ActionMode.Callback {

    private ActivityGenerarManifiestoBinding binding;
    private GenerarManifiestoPresenter presenter;
    private ActionMode actionMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenerarManifiestoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (presenter == null) {
            presenter = new GenerarManifiestoPresenter(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generar_manifiesto, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_leer_codigo_barra) {
            startActivity(new Intent(this, QRScannerActivity.class));
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showCodigosBarra(List<CodigoBarraItem> codigosBarra) {
        CodigoBarraAdapter adapter = new CodigoBarraAdapter(this, codigosBarra);
        adapter.setListener(this);
        binding.lvCodigosBarra.setAdapter(adapter);
    }

    @Override
    public void notifyItemChanged(int position) {
        binding.lvCodigosBarra.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void notifyItemInsert(int position) {
        binding.lvCodigosBarra.getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyItemRemove(int position) {
        binding.lvCodigosBarra.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyAllItemChanged() {
        binding.lvCodigosBarra.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showActionMode() {
        if (actionMode == null) {
            actionMode = startSupportActionMode(GenerarManifiestoActivity.this);
            CommonUtils.changeColorStatusBar(GenerarManifiestoActivity.this, R.color.gris_7);
            presenter.onShowActionMode();
        }
    }

    @Override
    public void hideActionMode() {
        actionMode.finish();
    }

    @Override
    public void setTitleActionMode(String title) {
        if (actionMode != null) {
            actionMode.setTitle(title);
        }
    }

    @Override
    public EditText getViewTxtPlaca() {
        return binding.txtPlaca;
    }

    @Override
    public void onClickIcon(View view, int position) {
        presenter.onSelectedItem(position);
    }

    @Override
    public void onClickItem(View view, int position) {
        if (actionMode == null) {
            presenter.onClickItem(position);
        } else {
            presenter.onSelectedItem(position);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_ruta_pendiente, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            presenter.onClickActionMode("eliminar");
            return true;
        } else if (item.getItemId() == R.id.action_check_all) {
            presenter.onClickActionMode("seleccionar_todos");
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        presenter.onCloseActionMode();
        CommonUtils.changeColorStatusBar(GenerarManifiestoActivity.this, R.color.colorPrimaryDark);
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.activity_generar_man_title_generar_manifiesto);

        binding.lvCodigosBarra.setLayoutManager(new LinearLayoutManager(this));
        binding.lvCodigosBarra.setHasFixedSize(true);

        binding.btnGenerarManifiesto.setOnClickListener(v -> presenter.onClickGenerarManifiesto());
    }
}
