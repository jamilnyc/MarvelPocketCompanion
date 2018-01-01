package com.jamil.companion.model;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Model class for Series
 */

public class Series extends Entity {

    public static final String TAG = Series.class.getSimpleName();

    private String mTitle, mDescription, mDetailUrl, mRating, mType;
    private int mStartYear, mEndYear;
    private ArrayList<com.jamil.companion.model.Creator> mCreators;

    public Series(int id,
                  Image thumbnail,
                  String title,
                  String description,
                  String detailUrl,
                  String rating,
                  String type,
                  int startYear,
                  int endYear,
                  ArrayList<com.jamil.companion.model.Creator> creators
    ) {
        super(id, thumbnail);
        mTitle = title;
        mDescription = description;
        mDetailUrl = detailUrl;
        mRating = rating;
        mType = type.length() > 1 ? type.substring(0,1).toUpperCase() + type.substring(1) : type;
        mStartYear = startYear;
        mEndYear = endYear;
        mCreators = creators;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getDetailUrl() {
        return mDetailUrl;
    }

    public String getRating() {
        return mRating;
    }

    public String getType() {
        return mType;
    }

    public int getStartYear() {
        return mStartYear;
    }

    public int getEndYear() {
        return mEndYear;
    }

    public ArrayList<com.jamil.companion.model.Creator> getCreators() {
        return mCreators;
    }

    public String getSearchFriendlyTitle()
    {
        String title = getTitle();

        // Remove everything after the first parenthesis
        int parenIndex = title.indexOf("(");
        if (parenIndex != -1) {
            title = title.substring(0, parenIndex);
        }

        // Remove other characters
        String[] undesiredCharacters = {".", ":", "!"};
        for (String undesired : undesiredCharacters) {
            title.replace(undesired, "");
        }

        return title;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeParcelable(mThumbnail, 0);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mDetailUrl);
        dest.writeString(mRating);
        dest.writeString(mType);
        dest.writeInt(mStartYear);
        dest.writeInt(mEndYear);
        dest.writeInt(mCreators.size());
        for (com.jamil.companion.model.Creator creator : mCreators) {
            dest.writeParcelable(creator, 0);
        }
    }

    private Series (Parcel in)
    {
        super(in.readInt(), (Image) in.readParcelable(Image.class.getClassLoader()));
        mTitle = in.readString();
        mDescription = in.readString();
        mDetailUrl = in.readString();
        mRating = in.readString();
        mType = in.readString();
        mStartYear = in.readInt();
        mEndYear = in.readInt();
        int creatorsSize = in.readInt();
        mCreators = new ArrayList<>();
        for (int i = 0; i < creatorsSize; ++i) {
            mCreators.add((com.jamil.companion.model.Creator)
                    in.readParcelable(com.jamil.companion.model.Creator.class.getClassLoader())
            );
        }
    }

    public static final Creator<Series> CREATOR = new Creator<Series>() {
        @Override
        public Series createFromParcel(Parcel source) {
            return new Series(source);
        }

        @Override
        public Series[] newArray(int size) {
            return new Series[size];
        }
    };

}
