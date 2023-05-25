
package com.example.mymovies.model;

import java.util.List;

import com.example.mymovies.model.Search;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResponse {

    @SerializedName("Search")
    @Expose
    private List<Search> search;

    @SerializedName("totalResults")
    @Expose
    private String totalResults;

    @SerializedName("Response")
    @Expose
    private String response;

    public List<Search> getSearch() {
        return search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public String getResponse() {
        return response;
    }


}
