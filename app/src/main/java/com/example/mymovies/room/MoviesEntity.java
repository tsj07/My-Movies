package com.example.mymovies.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MoviesTable")
public class MoviesEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ImdbID")
    public String imdbID;

    @ColumnInfo(name = "Title")
    public String mName;

    @ColumnInfo(name = "Year")
    public String mYear;

    @ColumnInfo(name = "Poster")
    public String mPoster;

    public MoviesEntity(@NonNull String imdbID, String mName, String mYear, String mPoster) {
        this.imdbID = imdbID;
        this.mName = mName;
        this.mYear = mYear;
        this.mPoster = mPoster;
    }

    public String getmName() {
        return mName;
    }

    public String getmYear() {
        return mYear;
    }

    public String getmPoster() {
        return mPoster;
    }

    @NonNull
    public String getImdbID() {
        return imdbID;
    }
}
