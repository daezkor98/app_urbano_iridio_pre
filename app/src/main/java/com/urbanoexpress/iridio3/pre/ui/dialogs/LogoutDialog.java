package com.urbanoexpress.iridio3.pre.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.urbanoexpress.iridio3.pre.databinding.ModalLogoutBinding;
import com.urbanoexpress.iridio3.pre.services.DataSyncService;
import com.urbanoexpress.iridio3.pre.ui.InitActivity;
import com.urbanoexpress.iridio3.pre.util.CommonUtils;
import com.urbanoexpress.iridio3.pre.util.Session;
import com.urbanoexpress.iridio3.pre.work.UserStatusWorker;

/**
 * Created by mick on 10/03/17.
 */

public class LogoutDialog extends DialogFragment {

    public static final String TAG = "LogoutDialog";

    private ModalLogoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding = ModalLogoutBinding.inflate(inflater, container, false);
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

    private void setupViews() {
        binding.btnNo.setOnClickListener(v -> dismiss());

        binding.btnSi.setOnClickListener(v -> {
            WorkManager.getInstance(getActivity()).cancelUniqueWork(UserStatusWorker.TAG);

            new Thread(() -> {
                CommonUtils.deleteUserData();
                Session.clearSession();
            }).start();

            getActivity().stopService(new Intent(getActivity(), DataSyncService.class));
            getActivity().startActivity(new Intent(getActivity(), InitActivity.class));
            getActivity().finish();
        });
    }

}
