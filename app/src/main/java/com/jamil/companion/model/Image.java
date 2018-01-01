package com.jamil.companion.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable{

    // Constant classes as a convenience
    public class AspectRatio {

        public static final String PORTRAIT = "portrait";
        public static final String STANDARD = "standard";
        public static final String LANDSCAPE = "landscape";
    }

    public class Size {

        public static final String SMALL = "small";
        public static final String MEDIUM = "medium";
        public static final String LARGE = "large";
        public static final String XLARGE = "xlarge";
        public static final String FANTASTIC = "fantastic";
        public static final String UNCANNY = "uncanny";
        public static final String INCREDIBLE = "incredible";
    }

    private String mPath, mExtension;

    public Image(String path, String extension)
    {
        mPath = path;
        mExtension = extension;
    }

    public String getRawImageUrl()
    {
        return mPath + "." + mExtension;
    }

    public String getImageUrl(String aspectRatio, String size)
    {
        // TODO Handle errors for incorrect parameters
        return mPath + "/" + aspectRatio + "_" + size + "." + mExtension;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(mExtension);
    }

    private Image(Parcel in)
    {
        mPath = in.readString();
        mExtension = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {

        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
