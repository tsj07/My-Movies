package com.example.mymovies.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mymovies.room.MoviesEntity;

import java.util.List;

public class MoviesViewModel extends AndroidViewModel {
    private final MoviesRepository repository;
    private final LiveData<List<MoviesEntity>> listLiveData;

    public MoviesViewModel(Application application) {
        super(application);
        repository = new MoviesRepository(application);
        listLiveData = repository.getMoviesList();
    }

    public LiveData<List<MoviesEntity>> getMoviesList(){
        return listLiveData;
    }

    public void add(MoviesEntity moviesEntity) {
        repository.add(moviesEntity);
    }

    public void deleteByImdbID(String imdbID) {
        repository.deleteByImdbID(imdbID);
    }

    public int ifIdAlreadyExisted(String imdbIDToCheck) {
        return repository.ifIdAlreadyExisted(imdbIDToCheck);
    }

}
