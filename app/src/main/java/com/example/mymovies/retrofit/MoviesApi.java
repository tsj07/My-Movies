package com.example.mymovies.retrofit;

import com.example.mymovies.model.MovieDetailsResponse;
import com.example.mymovies.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesApi {
    String key = "27345d4e";

    @GET("?type=movie&apikey=" + key)
    Call<SearchResponse> getSearchData(
            @Query("s") String mName
    );

    @GET("?type=movie&apikey=" + key)
    Call<SearchResponse> getNewPageData(
            @Query("s") String mName,
            @Query("page") int page
    );

    @GET("?apikey=" + key)
    Call<MovieDetailsResponse> getMovieDetails(
            @Query("t") String mName
    );

}
