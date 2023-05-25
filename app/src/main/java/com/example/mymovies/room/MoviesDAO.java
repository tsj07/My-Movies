package com.example.mymovies.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoviesDAO {

    @Query("SELECT * from MoviesTable")
    LiveData<List<MoviesEntity>> getMoviesList();

    @Insert
    void add(MoviesEntity moviesEntity);

    @Query("DELETE FROM MoviesTable WHERE ImdbID = :imdbID")
    void deleteByImdbID(String imdbID);

    @Query("SELECT count(*) FROM MoviesTable WHERE ImdbID = :imdbIDToCheck")
    int ifIdAlreadyExisted(String imdbIDToCheck);

}
