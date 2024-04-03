package com.capcorp.ui.user.homescreen.home.transport.addpictures;

import android.os.Parcel;
import android.os.Parcelable;

public class AddPicturePojo implements Parcelable {
    public static final Parcelable.Creator<AddPicturePojo> CREATOR = new Parcelable.Creator<AddPicturePojo>() {
        @Override
        public AddPicturePojo createFromParcel(Parcel source) {
            return new AddPicturePojo(source);
        }

        @Override
        public AddPicturePojo[] newArray(int size) {
            return new AddPicturePojo[size];
        }
    };
    public String thumbnail;
    public String original;

    public AddPicturePojo(String thumbnail, String original) {
        this.thumbnail = thumbnail;
        this.original = original;
    }

    protected AddPicturePojo(Parcel in) {
        this.thumbnail = in.readString();
        this.original = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbnail);
        dest.writeString(this.original);
    }
}