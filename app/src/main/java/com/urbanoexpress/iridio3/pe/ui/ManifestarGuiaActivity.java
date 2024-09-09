package com.urbanoexpress.iridio3.pe.ui;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Bundle;
import android.view.View;

import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.databinding.ActivityManifestarGuiaBinding;
import com.urbanoexpress.iridio3.pe.presenter.ManifestarGuiaPresenter;
import com.urbanoexpress.iridio3.pe.ui.model.PiezaItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.PiezasAdapter;
import com.urbanoexpress.iridio3.pe.view.ManifestarGuiaView;

import java.util.List;

public class ManifestarGuiaActivity extends AppThemeBaseActivity implements ManifestarGuiaView {

    private ActivityManifestarGuiaBinding binding;
    private ManifestarGuiaPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManifestarGuiaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (presenter == null) {
            presenter = new ManifestarGuiaPresenter(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void showPiezas(List<PiezaItem> items) {
        try {
            PiezasAdapter adapter = new PiezasAdapter(items, presenter);
            binding.rvPiezas.setAdapter(adapter);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        try {
            binding.rvPiezas.getAdapter().notifyItemChanged(position);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyAllItemChanged() {
        try {
            binding.rvPiezas.getAdapter().notifyDataSetChanged();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setVisibilityTopMessage(boolean visible) {
        binding.lblMsg.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setVisibilityAddToRouteButton(boolean visible) {
        binding.addToRouteButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle(R.string.title_activity_manifestar_guia);

        ((SimpleItemAnimator) binding.rvPiezas.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.rvPiezas.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPiezas.setHasFixedSize(true);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this,
                R.drawable.divider_vertical_recyclerview_1dp));
        binding.rvPiezas.addItemDecoration(itemDecoration);

        binding.fabScanBarCode.setOnClickListener(v -> presenter.onFabScanBarCodeClick());
        binding.addToRouteButton.setOnClickListener(v -> presenter.onBtnManifestarClick());
    }
}