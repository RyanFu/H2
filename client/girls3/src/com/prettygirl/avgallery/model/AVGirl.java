package com.prettygirl.avgallery.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AVGirl implements Parcelable, Comparable<AVGirl> {

    public String id;

    public String name;

    public int sortKey;

    public String path;

    public String subTilte;

    public AVGirl(String name, int sortKey, String subTilte, String path) {
        this.name = name;
        this.path = path;
        this.sortKey = sortKey;
        this.subTilte = subTilte;
        this.id = path.split("[.]")[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeInt(this.sortKey);
        out.writeString(this.subTilte);
        out.writeString(this.path);
    }

    public static final Parcelable.Creator<AVGirl> CREATOR = new Parcelable.Creator<AVGirl>() {
        public AVGirl createFromParcel(Parcel in) {
            return new AVGirl(in);
        }

        public AVGirl[] newArray(int size) {
            return new AVGirl[size];
        }
    };

    private AVGirl(Parcel in) {
        this.name = in.readString();
        this.sortKey = in.readInt();
        this.subTilte = in.readString();
        this.path = in.readString();
        this.id = this.path.split("[.]")[0];
    }

    @Override
    public int compareTo(AVGirl another) {
        return this.sortKey - another.sortKey;
    }

}
