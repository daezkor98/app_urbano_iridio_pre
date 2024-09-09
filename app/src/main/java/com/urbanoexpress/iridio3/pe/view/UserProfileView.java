package com.urbanoexpress.iridio3.pe.view;

import com.urbanoexpress.iridio3.pe.model.UserCredentialModel;

import java.io.File;

public interface UserProfileView extends BaseView2 {

    void setTextUserId(String text);
    void setTextFirstName(String text);
    void setTextLastName(String text);
    void setTextOccupation(String text);
    void setTextStatus(String text);
    void setTextDocument(String text);
    void setTextDateAdmission(String text);
    void setTextBranchOffice(String text);
    void setTextPhone(String text);

    void setBackgroundResourceStatus(int resId);
    void setTextColorResourceStatus(int resId);
    void setVisibilityStatus(boolean visible);

    void setUserPhoto(String url);
    void setUserPhoto(byte[] data);

    void showMessageNotConnectedToNetwork();

    void showDialogEditPhotoOptions();

    void openCamera(File photoFile);
    void navigateToUserCredential(UserCredentialModel credential);
}
