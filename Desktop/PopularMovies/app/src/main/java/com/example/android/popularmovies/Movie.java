package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fernando on 20/09/2016.
 */
public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private String mMovieImage;
    private String mMovieName;
    private String mReleaseDate;
    private float mRate;
    private int mOriginalId;

    public Movie(String mMovieImage, String mMovieName, String mReleaseDate, float mRate, int mOriginalId) {
        this.mMovieImage = mMovieImage;
        this.mMovieName = mMovieName;
        this.mReleaseDate = mReleaseDate;

        this.mRate = mRate;
        this.mOriginalId = mOriginalId;
    }

    private Movie(Parcel in) {

        mMovieImage = in.readString();
        mMovieName = in.readString();
        mReleaseDate = in.readString();

        mRate = in.readFloat();
        mOriginalId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieImage);
        dest.writeString(mMovieName);
        dest.writeString(mReleaseDate);

        dest.writeFloat(mRate);
        dest.writeInt(mOriginalId);

    }

    public String getmMovieImage() {
        return mMovieImage;
    }

    public void setmMovieImage(String mMovieImage) {
        this.mMovieImage = mMovieImage;
    }

    public String getmMovieName() {
        return mMovieName;
    }

    public void setmMovieName(String mMovieName) {
        this.mMovieName = mMovieName;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }


    public int getmOriginalId() {
        return mOriginalId;
    }

    public float getmRate() {
        return mRate;
    }

    public void setmRate(float mRate) {
        this.mRate = mRate;
    }


}
