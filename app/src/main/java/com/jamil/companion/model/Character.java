package com.jamil.companion.model;


import android.os.Parcel;

/**
 * Model class for a Character
 */

public class Character extends Entity
{
    private String mName, mDescription, mWikiLink;


    public Character (int id, Image thumbnail, String name, String description, String wikiLink)
    {
        super(id, thumbnail);
        mName = name;
        mDescription = description;
        mWikiLink = wikiLink;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getWikiLink() {
        return mWikiLink;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeParcelable(mThumbnail, 0);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mWikiLink);
    }

    private Character(Parcel in)
    {
        super(in.readInt(), (Image) in.readParcelable(Image.class.getClassLoader()));
        mName = in.readString();
        mDescription = in.readString();
        mWikiLink = in.readString();
    }

    public static final Creator<Character> CREATOR = new Creator<Character>() {

        @Override
        public Character createFromParcel(Parcel source) {
            return new Character(source);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };
}
