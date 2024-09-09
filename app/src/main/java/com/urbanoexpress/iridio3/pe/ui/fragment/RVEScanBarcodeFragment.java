package com.urbanoexpress.iridio3.pe.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.FragmentRecolectarValijaExpressScanBarcodeBinding;
import com.urbanoexpress.iridio3.pe.model.RecolectarValijaExpressViewModel;
import com.urbanoexpress.iridio3.pe.presenter.QRScannerPresenter;
import com.urbanoexpress.iridio3.pe.presenter.RVEScanBarcodePresenter;
import com.urbanoexpress.iridio3.pe.ui.QRScannerActivity;
import com.urbanoexpress.iridio3.pe.ui.model.DetailsItem;
import com.urbanoexpress.iridio3.pe.ui.adapter.DetailsAdapter;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.util.constant.LocalAction;
import com.urbanoexpress.iridio3.pe.view.RVEScanBarcodeView;

import java.util.List;

public class RVEScanBarcodeFragment extends BaseFragment implements RVEScanBarcodeView {

    public static final String TAG = "RVEScanBarcodeFragment";

    private FragmentRecolectarValijaExpressScanBarcodeBinding binding;
    private RVEScanBarcodePresenter presenter;
    private RecolectarValijaExpressViewModel model;

    public RVEScanBarcodeFragment() {}

    public static RVEScanBarcodeFragment newInstance() {
        return new RVEScanBarcodeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getViewContext())
                .registerReceiver(resultScannReceiver,
                        new IntentFilter(LocalAction.LOCAL_ACTION_BARCODE_SCAN_RESULT));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecolectarValijaExpressScanBarcodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        model = new ViewModelProvider(requireActivity()).get(RecolectarValijaExpressViewModel.class);
        presenter = new RVEScanBarcodePresenter(this, model);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getViewContext()).unregisterReceiver(resultScannReceiver);
    }

    @Override
    public void showProgressDialog(int messageId) {
        binding.progressLayout.progressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressDialog(String message) {
        binding.progressLayout.progressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgressDialog() {
        binding.progressLayout.progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void clearBarra() {
        binding.barraEditText.setText("");
        binding.barraEditText.requestFocus();
    }

    @Override
    public void setErrorBarra(String error) {
        binding.barraEditText.setError(error);
        binding.barraEditText.requestFocus();
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    @Override
    public void setDetails(List<DetailsItem> items) {
        binding.detailsContainer.setVisibility(View.VISIBLE);
        DetailsAdapter adapter = new DetailsAdapter(requireContext(), items);
        binding.detailsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setEnabledButtonNext(boolean enabled) {
        binding.nextButton.setEnabled(enabled);
    }

    @Override
    public void showMsgError(String msg) {
        binding.msgErrorText.setText(msg);
        binding.msgErrorText.setVisibility(View.VISIBLE);
        CommonUtils.vibrateDevice(getViewContext(), 100);
    }

    private void setupViews() {
        binding.detailsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.detailsRecyclerView.setHasFixedSize(true);

        binding.barraEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.msgErrorText.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.barraEditText.setOnKeyListener((v, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                hideKeyboard();
                presenter.processBarra(binding.barraEditText.getText().toString());
            }
            return false;
        });

        binding.scanBarcodeButton.setOnClickListener(v -> {
            binding.msgErrorText.setVisibility(View.GONE);

            Intent intent = new Intent(getViewContext(), QRScannerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("typeImpl", QRScannerPresenter.IMPLEMENT.READ_ONLY);
            intent.putExtra("args", bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        });

        binding.nextButton.setOnClickListener(v -> presenter.onNextButtonClick());
    }

    /**
     * Broadcast
     *
     * {@link BarcodeScannerReadOnlyImpl#sendOnResultScannListener}
     */
    private final BroadcastReceiver resultScannReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideKeyboard();
            presenter.processBarra(intent.getStringExtra("value"));
        }
    };
}