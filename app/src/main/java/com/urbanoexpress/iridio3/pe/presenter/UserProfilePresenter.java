package com.urbanoexpress.iridio3.pe.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.VolleyError;
import com.urbanoexpress.iridio3.pe.R;
import com.urbanoexpress.iridio3.pe.model.UserCredentialModel;
import com.urbanoexpress.iridio3.pe.model.interactor.UserProfileInteractor;
import com.urbanoexpress.iridio3.pe.model.interactor.callback.RequestCallback;
import com.urbanoexpress.iridio3.pe.util.FileUtils;
import com.urbanoexpress.iridio3.pe.util.ImageRotator;
import com.urbanoexpress.iridio3.pe.util.Preferences;
import com.urbanoexpress.iridio3.pe.util.Session;
import com.urbanoexpress.iridio3.pe.util.network.Connectivity;
import com.urbanoexpress.iridio3.pe.util.network.volley.MultipartJsonObjectRequest;
import com.urbanoexpress.iridio3.pe.view.UserProfileView;

import org.apache.commons.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserProfilePresenter {

    private UserProfileView view;

    private String photoDirPath = "";
    private File photoCapture;

    private UserCredentialModel userCredentialModel;

    private final CompositeDisposable compositeDisposable;

    public UserProfilePresenter(UserProfileView view) {
        this.view = view;
        this.compositeDisposable = new CompositeDisposable();
    }

    public void init() {
        StringBuilder stringBuilder = new StringBuilder("profile/");
        stringBuilder.append(Preferences.getInstance().getString("idUsuario", ""));
        stringBuilder.append("/photo/");
        photoDirPath = stringBuilder.toString();

        requestGetUserProfile();
    }

    public void onButtonEditPhotoClick() {
        if (userCredentialModel != null) {
            view.showDialogEditPhotoOptions();
        }
    }

    public void onTakePhotoClick() {
        photoCapture = FileUtils.generateFile(
                view.getViewContext(), generateImageName(""), photoDirPath);
        view.openCamera(photoCapture);
    }

    public void onPhotoCaptureResultOK() {
        view.showProgressDialog();
        prepareDataImageFile(photoCapture, new PrepareDataPhotoObserver(photoCapture.getName()));
    }

    public void onPickImageResultOK(Bitmap bitmap) {
        view.showProgressDialog();
        String imageName = generateImageName("");
        prepareDataImageBitmap(bitmap, new PrepareDataPhotoObserver(imageName));
    }

    public void onMenuViewCredentialClick() {
        if (userCredentialModel != null) {
            view.navigateToUserCredential(userCredentialModel);
        }
    }

    public void onDestroyActivity() {
        compositeDisposable.dispose();
    }

    private void requestGetUserProfile() {
        view.showProgressDialog();

        if (Connectivity.isConnected(view.getViewContext())) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    view.dismissProgressDialog();
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            String firstName = WordUtils.capitalizeFully(data.getString("nombres"));
                            String lastName = WordUtils.capitalizeFully(data.getString("apellidos"));

                            userCredentialModel = new UserCredentialModel.Builder()
                                    .setFullName(firstName + " " + lastName)
                                    .setDocument(data.getString("docu_identidad"))
                                    .setOccupation(WordUtils.capitalizeFully(data.getString("cargo")))
                                    .setDateAdmission(data.getString("fec_ingreso"))
                                    .setBranchOffice(WordUtils.capitalizeFully(data.getString("provincia")))
                                    .setStatus(WordUtils.capitalizeFully(data.getString("estado_user")))
                                    .setPhotoUrl(data.getString("url_foto"))
                                    .setCredentialUrl(data.getString("url_credencial"))
                                    .build();

                            view.setUserPhoto(userCredentialModel.getPhotoUrl());
                            view.setTextUserId(Preferences.getInstance().getString("idUsuario", ""));
                            view.setTextFirstName(firstName);
                            view.setTextLastName(lastName);
                            view.setTextStatus(userCredentialModel.getStatus());
                            view.setTextDocument(userCredentialModel.getDocument());
                            view.setTextDateAdmission(userCredentialModel.getDateAdmission());
                            view.setTextOccupation(userCredentialModel.getOccupation());
                            view.setTextBranchOffice(userCredentialModel.getBranchOffice());
                            view.setTextPhone(Session.getUser().getDevicePhone());

                            try {
                                if (Integer.parseInt(data.getString("estado_flag")) == 1) {
                                    view.setBackgroundResourceStatus(R.drawable.bg_active_status_user_profile);
                                    view.setTextColorResourceStatus(R.color.green_4);
                                } else {
                                    view.setBackgroundResourceStatus(R.drawable.bg_inactive_status_user_profile);
                                    view.setTextColorResourceStatus(R.color.colorPrimary);
                                }
                                view.setVisibilityStatus(true);
                            } catch (NullPointerException | NumberFormatException ex) {
                                ex.printStackTrace();
                                view.setVisibilityStatus(false);
                            }
                        } else {
                            view.showToast(response.getString("msg_error"));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        view.showToast(R.string.json_object_exception);
                    }
                }
                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    view.dismissProgressDialog();
                    view.showToast(R.string.volley_error_message);
                }
            };

            String[] params = {
                    Preferences.getInstance().getString("idUsuario", ""),
                    Session.getUser().getDevicePhone()
            };

            UserProfileInteractor.getUserProfile(params, callback);
        } else {
            view.dismissProgressDialog();
            view.showMessageNotConnectedToNetwork();
        }
    }

    private void requestUploadPhotoUserProfile(String fileName, byte[] data) {

        //Log.i("TAG", "requestUploadPhotoUserProfile: " +  data.length);
        //Si se optiene datos 218754/41279 bytelength

        if (Connectivity.isConnected(view.getViewContext())) {
            RequestCallback callback = new RequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    view.dismissProgressDialog();
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject dataJSON = response.getJSONObject("data");
                            userCredentialModel.setPhotoUrl(dataJSON.getString("photo_url"));
                            view.setUserPhoto(data);
                            view.showToast("Foto actualizada exitosamente.");
                        } else {
                            view.showToast("Lo sentimos, ocurrió un error al momento de actualizar la foto.");
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        view.showToast(R.string.json_object_exception);
                    }
                }
                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    view.dismissProgressDialog();
                    view.showToast(R.string.volley_error_message);
                }
            };

            MultipartJsonObjectRequest.DataPart photo =
                    new MultipartJsonObjectRequest.DataPart(fileName, data, "image/png");

            String[] params = {
                    fileName,
                    Preferences.getInstance().getString("idUsuario", ""),
                    Session.getUser().getDevicePhone()
            };

            UserProfileInteractor.uploadPhoto(params, photo, callback);
        } else {
            view.dismissProgressDialog();
            view.showMessageNotConnectedToNetwork();
        }
    }

    private String generateImageName(String prefix) {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String photoName = "Imagen_" + timeStamp + ".jpg";
        if (!prefix.isEmpty()) {
            photoName = prefix + "_" + timeStamp + ".jpg";
        }
        return photoName;
    }

    private Single<byte[]> readFileToByteArray(File file) {
        return Single.create(emitter -> {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bitmap = ImageRotator.rotateImageIfRequired(bitmap, file.getAbsolutePath());
            byte[] byteArray = FileUtils.readBitmapToByteArray(bitmap, 20);

            if (byteArray != null) {
                emitter.onSuccess(byteArray);
            } else {
                emitter.onError(new IOException("Lo sentimos, ocurrió un error al leer el archivo de la foto."));
            }
        });
    }

    private Single<byte[]> readBitmapToByteArray(Bitmap bitmap) {
        return Single.create(emitter -> {
            byte[] byteArray = FileUtils.readBitmapToByteArray(bitmap, 20);

            if (byteArray != null) {
                emitter.onSuccess(byteArray);
            } else {
                emitter.onError(new IOException("Lo sentimos, ocurrió un error al leer el archivo de la foto."));
            }
        });
    }

    private void prepareDataImageFile(File file, DisposableSingleObserver<byte[]> observer) {
        subscribeToPrepareDataImage(readFileToByteArray(file), observer);
    }

    private void prepareDataImageBitmap(Bitmap bitmap, DisposableSingleObserver<byte[]> observer) {
        subscribeToPrepareDataImage(readBitmapToByteArray(bitmap), observer);
    }

    private void subscribeToPrepareDataImage(Single<byte[]> single,
                                             DisposableSingleObserver<byte[]> observer) {
        Single<byte[]> observable = single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        compositeDisposable.add(observable.subscribeWith(observer));
    }

     private class PrepareDataPhotoObserver extends DisposableSingleObserver<byte[]> {

        private String imageName;

        public PrepareDataPhotoObserver(String imageName) {
            this.imageName = imageName;
        }

        @Override
        public void onSuccess(byte @NonNull [] bytes) {
            requestUploadPhotoUserProfile(imageName, bytes);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
            view.dismissProgressDialog();
            view.showToast(e.getMessage());
        }
    }
}
