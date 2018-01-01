package com.jamil.companion.model;

import java.io.Serializable;

/**
 * Model class for Creators
 */

public class Creator extends Entity implements Serializable {
    public static final String TAG = Creator.class.getSimpleName();

    private String mName, mRole;

    public Creator(String name, String role)
    {
        super(0, null);
        mName = name;
        mRole = role;
    }

    public String getName() {
        return mName;
    }

    public String getRole() {
        return mRole;
    }

    public void writeToParcel(android.os.Parcel dest, int flags)
    {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mRole);
    }

    private Creator (android.os.Parcel in)
    {
        super(in.readInt(), null);
        mName = in.readString();
        mRole = in.readString();
    }

    public static final android.os.Parcelable.Creator<com.jamil.companion.model.Creator> CREATOR
    = new android.os.Parcelable.Creator<com.jamil.companion.model.Creator>() {

        @Override
        public com.jamil.companion.model.Creator createFromParcel(android.os.Parcel source) {
            return new com.jamil.companion.model.Creator(source);
        }

        @Override
        public com.jamil.companion.model.Creator[] newArray(int size) {
            return new com.jamil.companion.model.Creator[size];
        }
    };
}
