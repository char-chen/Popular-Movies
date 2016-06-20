package com.charleschen.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    String author;
    String url;
    String content;

    public Review(String author, String url, String content) {
        this.author = author;
        this.url = url;
        this.content = content;
    }

    protected Review(Parcel in) {
        author = in.readString();
        url = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(url);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
