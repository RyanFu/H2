package com.prettygirl.superstar.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class SuperStar implements Parcelable {

    public String id;

    public String name;

    public String subTitle;

    public SuperStar(String id, String name, String subTitle) {
        this.id = id;
        this.name = name;
        this.subTitle = subTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.id);
        out.writeString(this.name);
        out.writeString(this.subTitle);
    }

    public static final Parcelable.Creator<SuperStar> CREATOR = new Parcelable.Creator<SuperStar>() {
        public SuperStar createFromParcel(Parcel in) {
            return new SuperStar(in);
        }

        public SuperStar[] newArray(int size) {
            return new SuperStar[size];
        }
    };

    private SuperStar(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.subTitle = in.readString();
    }

}
