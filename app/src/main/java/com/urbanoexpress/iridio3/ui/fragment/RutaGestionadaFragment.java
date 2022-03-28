package com.urbanoexpress.iridio3.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.FragmentRutasBinding;
import com.urbanoexpress.iridio3.presenter.RutaGestionadaPresenter;
import com.urbanoexpress.iridio3.ui.adapter.RutaAdapter;
import com.urbanoexpress.iridio3.ui.model.RutaItem;
import com.urbanoexpress.iridio3.util.AnimationUtils;
import com.urbanoexpress.iridio3.view.RutaGestionadaView;

import java.util.List;

public class RutaGestionadaFragment extends Fragment implements RutaGestionadaView,
        RutaAdapter.OnClickGuiaItemListener {

    private FragmentRutasBinding binding;
    private RutaGestionadaPresenter presenter;

    public RutaGestionadaFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRutasBinding.inflate(inflater, container, false);

        setupViews();

        if (presenter == null) {
            presenter = new RutaGestionadaPresenter(this);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroyActivity();
    }

    @Override
    public void showRutasGestionadas(final List<RutaItem> rutasGestionadas) {
        try {
            RutaAdapter adapter = new RutaAdapter(getActivity(), this, rutasGestionadas);
            binding.rvRutas.setAdapter(adapter);
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
    public void setVisibilitySwipeRefreshLayout(boolean visible) {
        binding.swipeRefreshLayout.setRefreshing(visible);
    }

    @Override
    public Context getContextView() {
        return getActivity();
    }

    @Override
    public View baseFindViewById(int id) {
        return binding.getRoot().findViewById(id);
    }

    @Override
    public void onClickGuiaItem(View view, int position) {
        presenter.onClickItem(position);
    }

    @Override
    public void onClickGuiaIconLinea(View view, int position) {
        presenter.onClickItem(position);
    }

    @Override
    public void onClickGuiaIconImporte(View view, int position) { }

    @Override
    public void onClickGuiaIconTipoEnvio(View view, int position) {
        presenter.onClickTipoEnvio(position);
    }

    private void setupViews() {
        binding.rvRutas.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvRutas.setHasFixedSize(true);

        binding.swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorGreyUrbano, R.color.colorBlackUrbano);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> presenter.onSwipeRefresh());
    }
}