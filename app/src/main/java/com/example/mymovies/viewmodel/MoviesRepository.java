package com.example.mymovies.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mymovies.room.Database;
import com.example.mymovies.room.MoviesDAO;
import com.example.mymovies.room.MoviesEntity;

import java.util.List;

public class MoviesRepository {
    private final MoviesDAO mDao;
    private final LiveData<List<MoviesEntity>> listLiveData;

    public MoviesRepository(Application application) {
        Database db = Database.getDatabase(application);
        mDao = db.moviesDAO();
        listLiveData = mDao.getMoviesList();
    }

    LiveData<List<MoviesEntity>> getMoviesList() {
        return listLiveData;
    }

    void add(MoviesEntity moviesEntity) {
        Database.databaseWriteExecutor.execute(() -> {
            mDao.add(moviesEntity);
        });
    }

    void deleteByImdbID(String imdbID) {
        Database.databaseWriteExecutor.execute(() -> {
            mDao.deleteByImdbID(imdbID);
        });
    }

    int ifIdAlreadyExisted(String imdbIDToCheck) {
        return mDao.ifIdAlreadyExisted(imdbIDToCheck);
    }

}
