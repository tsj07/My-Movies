package com.example.mymovies.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.mymovies.Debounce;
import com.example.mymovies.R;
import com.example.mymovies.activity.MovieDetailsActivity;
import com.example.mymovies.adaptors.SearchAdaptor;
import com.example.mymovies.databinding.FragmentSearchBinding;
import com.example.mymovies.model.Search;
import com.example.mymovies.model.SearchResponse;
import com.example.mymovies.retrofit.RetrofitClient;
import com.example.mymovies.room.MoviesEntity;
import com.example.mymovies.viewmodel.MoviesRepository;
import com.example.mymovies.viewmodel.MoviesViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    MoviesViewModel viewModel;
    MoviesRepository repository;
    FragmentSearchBinding binding;
    SearchAdaptor adaptor;
    SearchAdaptor.OnItemClick listener;
    MoviesEntity entity;
    List<MoviesEntity> moviesList = new ArrayList<>();
    private final Debounce debounce = new Debounce();
    int count = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        repository = new MoviesRepository(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this).get(MoviesViewModel.class);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        return binding.getRoot();
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    getData(query.trim());
                    saveQuery(query.trim());
                    binding.llNextPage.setVisibility(View.VISIBLE);
                    nextPageListener(query.trim());
                } else {
                    binding.llNextPage.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                debounce.debounce(Void.class, new Runnable() {
                    @Override
                    public void run() {
                        getData(newText.trim());
                    }
                }, 500, TimeUnit.MILLISECONDS);
                saveQuery(newText.trim());
                binding.llNextPage.setVisibility(View.GONE);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                count = 1;
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(String mName) {
        Call<SearchResponse> responseCall = RetrofitClient.getInstance().getMyApi().getSearchData(mName);
        responseCall.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                Activity activity = getActivity();
                if (isAdded() && activity != null) {
                    SearchResponse searchResponse = response.body();
                    if (searchResponse != null) {
                        if (searchResponse.getResponse().equals("True")) {
                            viewModel.getMoviesList().observe(requireActivity(), list -> {
                                adaptor = new SearchAdaptor(list, searchResponse.getSearch(), requireActivity(), searchListener());
                                binding.rvSearch.setAdapter(adaptor);
                                binding.rvSearch.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                                binding.progressBarSearch.setVisibility(View.GONE);
                                binding.rvSearch.setVisibility(View.VISIBLE);
                                binding.tvNoMovieFound.setVisibility(View.GONE);
                            });
                        } else {
                            binding.llNextPage.setVisibility(View.GONE);
                            binding.tvNoMovieFound.setVisibility(View.VISIBLE);
                            binding.rvSearch.setVisibility(View.GONE);
                            binding.progressBarSearch.setVisibility(View.GONE);
                        }
                    } else
                        Toast.makeText(requireActivity(), "Data = Null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, Throwable t) {
                Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPage(String name, int page) {
        Call<SearchResponse> responseCall = RetrofitClient.getInstance().getMyApi().getNewPageData(name, page);
        responseCall.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                Activity activity = getActivity();
                if (isAdded() && activity != null) {
                    SearchResponse searchResponse = response.body();
                    if (searchResponse != null) {
                        if (searchResponse.getResponse().equals("True")) {
                            viewModel.getMoviesList().observe(requireActivity(), list -> {
                                adaptor = new SearchAdaptor(list, searchResponse.getSearch(), requireActivity(), searchListener());
                                binding.rvSearch.setAdapter(adaptor);
                                binding.rvSearch.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                                binding.rvSearch.setVisibility(View.VISIBLE);
                            });
                        } else
                            binding.rvSearch.setVisibility(View.GONE);
                    } else
                        Toast.makeText(requireActivity(), "Data = Null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(requireActivity(), "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SearchAdaptor.OnItemClick searchListener() {
        listener = new SearchAdaptor.OnItemClick() {
            @Override
            public void onItemViewClick(Search model, int pos) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("title", model.getTitle());
                startActivity(intent);
            }

            @Override
            public void onAdd(Search model, int pos) {
                if (viewModel.ifIdAlreadyExisted(model.getImdbID()) == 0) {
                    entity = new MoviesEntity(
                            model.getImdbID(),
                            model.getTitle(),
                            model.getYear(),
                            model.getPoster());
                    viewModel.add(entity);
                    model.setFavourite(true);
                } else {
                    viewModel.deleteByImdbID(model.getImdbID());
                    model.setFavourite(false);
                }
            }
        };
        return listener;
    }

    private void nextPageListener(String query) {
        binding.tvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count >= 2) {
                    count--;
                    getPage(query, count);
                    if (count == 1) {
                        binding.tvPrevious.setVisibility(View.INVISIBLE);
                        binding.tvPrevious.setEnabled(false);
                    } else {
                        binding.tvPrevious.setVisibility(View.VISIBLE);
                        binding.tvPrevious.setEnabled(true);
                    }
                }
            }
        });

        binding.tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                getPage(query, count);
                if (count >= 2) {
                    binding.tvPrevious.setVisibility(View.VISIBLE);
                    binding.tvPrevious.setEnabled(true);
                } else {
                    binding.tvPrevious.setVisibility(View.INVISIBLE);
                    binding.tvPrevious.setEnabled(false);
                }
            }
        });
    }

    private void saveQuery(String query) {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!query.isEmpty()) {
            editor.putString("query", query);
            editor.apply();
        } else {
            editor.putString("query", "Ant");
            editor.apply();
        }
    }

    private String loadQuery() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPref.getString("query", null);
    }

    @Override
    public void onResume() {
        getData(loadQuery());
        super.onResume();
    }

    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}