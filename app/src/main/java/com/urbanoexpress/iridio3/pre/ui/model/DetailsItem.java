package com.urbanoexpress.iridio3.pre.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailsItem implements Parcelable {

    private String title;
    private String description;

    public DetailsItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    protected DetailsItem(Parcel in) {
        title = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DetailsItem> CREATOR = new Creator<DetailsItem>() {
        @Override
        public DetailsItem createFromParcel(Parcel in) {
            return new DetailsItem(in);
        }

        @Override
        public DetailsItem[] newArray(int size) {
            return new DetailsItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}