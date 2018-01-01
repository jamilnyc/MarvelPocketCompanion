package com.jamil.companion.model;

import android.os.Parcelable;

public abstract class Entity implements Parcelable{

    protected int mId;
    protected Image mThumbnail;

    public Entity(int id, Image thumbnail)
    {
        mId = id;
        mThumbnail = thumbnail;
    }

    public int getId() {
        return mId;
    }

    public Image getThumbnail() {
        return mThumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
