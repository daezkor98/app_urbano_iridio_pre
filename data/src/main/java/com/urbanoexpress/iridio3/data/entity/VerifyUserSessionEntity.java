package com.urbanoexpress.iridio3.data.entity;

import com.google.gson.annotations.SerializedName;

public class VerifyUserSessionEntity {

    @SerializedName("user")
    private UserEntity user;

    @SerializedName("app")
    private AppEntity app;

    public VerifyUserSessionEntity() {}

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public AppEntity getApp() {
        return app;
    }

    public void setApp(AppEntity app) {
        this.app = app;
    }

    public class UserEntity {

        @SerializedName("status")
        private String status;

        public UserEntity() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class AppEntity {

        @SerializedName("status")
        private String status;

        @SerializedName("update_required")
        private Boolean updateRequired;

        @SerializedName("latest_version_name")
        private String latestVersionName;

        public AppEntity() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean isUpdateRequired() {
            return updateRequired;
        }

        public void setUpdateRequired(Boolean updateRequired) {
            this.updateRequired = updateRequired;
        }

        public String getLatestVersionName() {
            return latestVersionName;
        }

        public void setLatestVersionName(String latestVersionName) {
            this.latestVersionName = latestVersionName;
        }
    }
}
