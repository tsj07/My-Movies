
package com.example.mymovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetailsResponse {

    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("Rated")
    @Expose
    private String rated;
    @SerializedName("Released")
    @Expose
    private String released;
    @SerializedName("Runtime")
    @Expose
    private String runtime;
    @SerializedName("Director")
    @Expose
    private String director;
    @SerializedName("imdbID")
    @Expose
    private String imdbID;
    @SerializedName("Actors")
    @Expose
    private String actors;
    @SerializedName("Plot")
    @Expose
    private String plot;
    @SerializedName("Language")
    @Expose
    private String language;
    @SerializedName("Country")
    @Expose
    private String country;
    @SerializedName("Poster")
    @Expose
    private String poster;
    @SerializedName("Ratings")
    @Expose
    private List<Rating> ratings;
    @SerializedName("imdbRating")
    @Expose
    private String imdbRating;
    @SerializedName("Genre")
    @Expose
    private String genre;
    @SerializedName("Writer")
    @Expose
    private String writer;
    @SerializedName("Awards")
    @Expose
    private String awards;
    @SerializedName("BoxOffice")
    @Expose
    private String boxOffice;

    public String getAwards() {
        return awards;
    }

    public String getReleased() {
        return released;
    }

    public String getActors() {
        return actors;
    }

    public String getLanguage() {
        return language;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public String getGenre() {
        return genre;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    public String getWriter() {
        return writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public String getRated() {
        return rated;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getDirector() {
        return director;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getPlot() {
        return plot;
    }

    public String getCountry() {
        return country;
    }

    public String getPoster() {
        return poster;
    }

    public String getImdbRating() {
        return imdbRating;
    }


}
