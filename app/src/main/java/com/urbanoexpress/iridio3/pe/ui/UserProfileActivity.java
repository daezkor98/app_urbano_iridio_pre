package com.urbanoexpress.iridio3.pe.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.urbanoexpress.iridio3.R;
import com.urbanoexpress.iridio3.databinding.ActivityUserProfileBinding;
import com.urbanoexpress.iridio3.pe.model.UserCredentialModel;
import com.urbanoexpress.iridio3.pe.presenter.UserProfilePresenter;
import com.urbanoexpress.iridio3.pe.ui.helpers.ModalHelper;
import com.urbanoexpress.iridio3.pe.util.CameraUtils;
import com.urbanoexpress.iridio3.pe.util.CommonUtils;
import com.urbanoexpress.iridio3.pe.view.UserProfileView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class UserProfileActivity extends AppThemeBaseActivity implements UserProfileView {

    private ActivityUserProfileBinding binding;
    private UserProfilePresenter presenter;
    private int positionSelectedOptionEditPhoto = 0;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();

        if (presenter == null) {
            presenter = new UserProfilePresenter(this);
            presenter.init();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.view_credentials) {
            presenter.onMenuViewCredentialClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroyActivity();
        compositeDisposable.dispose();
    }

    @Override
    protected void animOnLeaveActivity() {
        overridePendingTransition(R.anim.not_slide, R.anim.slide_exit_out_bottom);
    }

    @Override
    public void setTextUserId(String text) {
        binding.userIdText.setText(text);
    }

    @Override
    public void setTextFirstName(String text) {
        binding.firstNameText.setText(text);
    }

    @Override
    public void setTextLastName(String text) {
        binding.lastNameText.setText(text);
    }

    @Override
    public void setTextOccupation(String text) {
        binding.occupationText.setText(text);
    }

    @Override
    public void setTextStatus(String text) {
        binding.statusText.setText(text);
    }

    @Override
    public void setTextDocument(String text) {
        binding.documentText.setText(text);
    }

    @Override
    public void setTextDateAdmission(String text) {
        binding.dateAdmissionText.setText(text);
    }

    @Override
    public void setTextBranchOffice(String text) {
        binding.branchOfficeText.setText(text);
    }

    @Override
    public void setTextPhone(String text) {
        binding.phoneText.setText(text);
    }

    @Override
    public void setBackgroundResourceStatus(int resId) {
        binding.statusText.setBackgroundResource(resId);
    }

    @Override
    public void setTextColorResourceStatus(int resId) {
        binding.statusText.setTextColor(ContextCompat.getColor(this, resId));
    }

    @Override
    public void setVisibilityStatus(boolean visible) {
        binding.statusText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setUserPhoto(String url) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.img_account_circle)
                .error(R.drawable.img_account_circle);
        Glide.with(this).load(url).apply(requestOptions).circleCrop().into(binding.photoImage);
    }

    @Override
    public void setUserPhoto(byte[] data) {
        Glide.with(this).load(data).circleCrop().into(binding.photoImage);
    }

    @Override
    public void showDialogEditPhotoOptions() {
        positionSelectedOptionEditPhoto = 0;
        String[] options = {"Tomar foto", "Seleccionar foto"};
        ModalHelper.getBuilderAlertDialog(this)
                .setTitle("Editar foto")
                .setSingleChoiceItems(options, 0, (dialog, which) ->
                        positionSelectedOptionEditPhoto = which)
                .setPositiveButton(R.string.text_continuar, (dialog, which) -> {
                    if (positionSelectedOptionEditPhoto == 0) {
                        presenter.onTakePhotoClick();
                    } else if (positionSelectedOptionEditPhoto == 1) {
                        openGallery();
                    }
                })
                .setNegativeButton(R.string.text_cancelar, null)
                .show();
    }

    @Override
    public void openCamera(File photoFile) {
        Intent intent = CameraUtils.getIntentImageCapture(this,  photoFile);
        startImageCaptureForResult.launch(intent);
    }

    @Override
    public void navigateToUserCredential(UserCredentialModel credential) {
        Intent intent = new Intent(this, UserCredentialActivity.class);
        intent.putExtra(UserCredentialActivity.ARG.USER_CREDENTIAL, credential);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
    }

    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startPickImageForResult.launch(intent);
    }

    private void setupViews() {
        setupToolbar(binding.toolbar);
        setScreenTitle("Cuenta");

        binding.editPhotoButton.setOnClickListener(v -> presenter.onButtonEditPhotoClick());

        binding.showMyQrCodeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ShareProfileQRCodeActivity.class));
            overridePendingTransition(R.anim.slide_enter_from_bottom, R.anim.not_slide);
        });

        binding.passwordContentLayout.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));

        if (CommonUtils.isAndroidLollipop()) binding.securityContainerLayout.setClipToOutline(true);
    }

    private final ActivityResultLauncher<Intent> startImageCaptureForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    presenter.onPhotoCaptureResultOK();
                }
            });

    private final ActivityResultLauncher<Intent> startPickImageForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null && result.getData().getData() != null) {
                        try {
                            Uri imageUri = result.getData().getData();
                            Bitmap bitmap = getBitmapFromSelectedImage(imageUri);
                            //TODO ImageRotator is failling
//                            bitmap = ImageRotator.rotateImageIfRequired(this, bitmap, imageUri);
                            presenter.onPickImageResultOK(bitmap);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        showToast("Lo sentimos, la imagen no fue seleccionada correctamente.");
                    }
                }
            });

    private Bitmap getBitmapFromSelectedImage(Uri imageUri) throws FileNotFoundException, IOException {
        if (CommonUtils.isAndroid10()) {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
            return ImageDecoder.decodeBitmap(source);
        } else {
            return MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        }
    }
}