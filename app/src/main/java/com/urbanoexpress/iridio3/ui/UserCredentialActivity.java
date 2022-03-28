package com.urbanoexpress.iridio3.ui;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityUserCredentialBinding;
import com.urbanoexpress.iridio3.model.UserCredentialModel;
import com.urbanoexpress.iridio3.util.MetricsUtils;
import com.urbanoexpress.iridio3.util.QRCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class UserCredentialActivity extends AppThemeBaseActivity {

    private ActivityUserCredentialBinding binding;
    private UserCredentialModel userCredentialModel;

    public interface ARG {
        String USER_CREDENTIAL = "userCredential";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserCredentialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    public void setUrlPhoto(String url) {
        Glide.with(this)
                .load(url)
                .into(binding.photoImage);
    }

    public void setQRCode(String value) {
        Bitmap drawable = new QRCode.Builder(this)
                .setValue(value)
                .setSize(MetricsUtils.dpToPx(this, 90))
                .build();

        Glide.with(this)
                .load(drawable)
                .into(binding.qrCodeImage);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);

        binding.toolbar.setNavigationIcon(R.drawable.ic_action_close_grey);

        userCredentialModel = getIntent().getExtras().getParcelable(ARG.USER_CREDENTIAL);

        if (userCredentialModel != null) {
            setUrlPhoto(userCredentialModel.getPhotoUrl());
            if (!userCredentialModel.getCredentialUrl().isEmpty()) {
                setQRCode(userCredentialModel.getCredentialUrl());
            }

            binding.fullNameText.setText(userCredentialModel.getFullName());
            binding.documentText.setText(userCredentialModel.getDocument());
            binding.occupationText.setText(userCredentialModel.getOccupation());
            binding.dateAdmissionText.setText(userCredentialModel.getDateAdmission());
            binding.branchOfficeText.setText(userCredentialModel.getBranchOffice());
            binding.statusText.setText(userCredentialModel.getStatus());

            binding.dateEmissionText.setText("Credencial generado el " +
                    LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                    + " a la(s) " +
                    LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));

        }
    }
}