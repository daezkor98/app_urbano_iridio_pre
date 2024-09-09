package com.urbanoexpress.iridio3.pe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserCredentialModel implements Parcelable {

    private String fullName;
    private String document;
    private String occupation;
    private String dateAdmission;
    private String branchOffice;
    private String status;
    private String photoUrl;
    private String credentialUrl;

    public UserCredentialModel() {}

    protected UserCredentialModel(Parcel in) {
        fullName = in.readString();
        document = in.readString();
        occupation = in.readString();
        dateAdmission = in.readString();
        branchOffice = in.readString();
        status = in.readString();
        photoUrl = in.readString();
        credentialUrl = in.readString();
    }

    public static final Creator<UserCredentialModel> CREATOR = new Creator<UserCredentialModel>() {
        @Override
        public UserCredentialModel createFromParcel(Parcel in) {
            return new UserCredentialModel(in);
        }

        @Override
        public UserCredentialModel[] newArray(int size) {
            return new UserCredentialModel[size];
        }
    };

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getDateAdmission() {
        return dateAdmission;
    }

    public void setDateAdmission(String dateAdmission) {
        this.dateAdmission = dateAdmission;
    }

    public String getBranchOffice() {
        return branchOffice;
    }

    public void setBranchOffice(String branchOffice) {
        this.branchOffice = branchOffice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(document);
        dest.writeString(occupation);
        dest.writeString(dateAdmission);
        dest.writeString(branchOffice);
        dest.writeString(status);
        dest.writeString(photoUrl);
        dest.writeString(credentialUrl);
    }

    public static class Builder {

        private String fullName = "";
        private String document = "";
        private String occupation = "";
        private String dateAdmission = "";
        private String branchOffice = "";
        private String status = "";
        private String photoUrl = "";
        private String credentialUrl = "";

        public Builder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder setDocument(String document) {
            this.document = document;
            return this;
        }

        public Builder setOccupation(String occupation) {
            this.occupation = occupation;
            return this;
        }

        public Builder setDateAdmission(String dateAdmission) {
            this.dateAdmission = dateAdmission;
            return this;
        }

        public Builder setBranchOffice(String branchOffice) {
            this.branchOffice = branchOffice;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Builder setCredentialUrl(String credentialUrl) {
            this.credentialUrl = credentialUrl;
            return this;
        }

        public UserCredentialModel build() {
            UserCredentialModel credential = new UserCredentialModel();
            credential.setFullName(fullName);
            credential.setDocument(document);
            credential.setOccupation(occupation);
            credential.setDateAdmission(dateAdmission);
            credential.setBranchOffice(branchOffice);
            credential.setStatus(status);
            credential.setPhotoUrl(photoUrl);
            credential.setCredentialUrl(credentialUrl);
            return credential;
        }
    }
}
