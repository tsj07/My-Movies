package com.example.mymovies.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.activity.MovieDetailsActivity;
import com.example.mymovies.adaptors.FavouritesAdaptor;
import com.example.mymovies.databinding.FragmentFavouritesBinding;
import com.example.mymovies.room.MoviesEntity;
import com.example.mymovies.viewmodel.MoviesRepository;
import com.example.mymovies.viewmodel.MoviesViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FavouritesFragment extends Fragment {
    FragmentFavouritesBinding binding;
    MoviesViewModel viewModel;
    MoviesRepository repository;
    FavouritesAdaptor adaptor;
    FavouritesAdaptor.OnItemClick listener;

    public FavouritesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        repository = new MoviesRepository(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this).get(MoviesViewModel.class);
        getRoomData();
        new ItemTouchHelper(swipeToDelete()).attachToRecyclerView(binding.rvFavourites);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar2);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort_title) {
            viewModel.getMoviesList().observe(requireActivity(), list -> {
                Collections.sort(list, new Comparator<MoviesEntity>() {
                    @Override
                    public int compare(MoviesEntity p1, MoviesEntity p2) {
                        return p1.mName.compareToIgnoreCase(p2.mName);
                    }
                });
                adaptor.setData(list);
                saveList(list);
            });
            return true;

        } else if (item.getItemId() == R.id.action_sort_year) {
            viewModel.getMoviesList().observe(requireActivity(), list -> {
                Collections.sort(list, new Comparator<MoviesEntity>() {
                    @Override
                    public int compare(MoviesEntity p1, MoviesEntity p2) {
                        return p1.mYear.compareToIgnoreCase(p2.mYear);
                    }
                });
                adaptor.setData(list);
                saveList(list);
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRoomData() {
        viewModel.getMoviesList().observe(requireActivity(), list -> {
            if (list != null && list.size() > 0) {
                adaptor = new FavouritesAdaptor(requireActivity(), favoriteListener());
                adaptor.setData(list);
                binding.rvFavourites.setAdapter(adaptor);
                binding.rvFavourites.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                binding.rvFavourites.setVisibility(View.VISIBLE);
                binding.tvNoMovie.setVisibility(View.GONE);
            } else {
                binding.rvFavourites.setVisibility(View.GONE);
                binding.tvNoMovie.setVisibility(View.VISIBLE);
            }
        });
    }

    private FavouritesAdaptor.OnItemClick favoriteListener() {
        listener = new FavouritesAdaptor.OnItemClick() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDelete(MoviesEntity entity, int pos) {
                viewModel.deleteByImdbID(entity.getImdbID());
                viewModel.getMoviesList().observe(requireActivity(), list -> {
                    if (list.size() > 0) {
                        binding.tvNoMovie.setVisibility(View.GONE);
                        binding.rvFavourites.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvFavourites.setVisibility(View.GONE);
                        binding.tvNoMovie.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onItemViewClick(String name, int pos) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        };

        return listener;
    }

    private ItemTouchHelper.SimpleCallback swipeToDelete() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAbsoluteAdapterPosition();
                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.deleteByImdbID(Objects.requireNonNull(viewModel.getMoviesList().getValue()).get(pos).getImdbID());
                    viewModel.getMoviesList().observe(requireActivity(), list -> {
                        if (list.size() > 0) {
                            binding.tvNoMovie.setVisibility(View.GONE);
                            binding.rvFavourites.setVisibility(View.VISIBLE);
                        } else {
                            binding.rvFavourites.setVisibility(View.GONE);
                            binding.tvNoMovie.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    private void saveList(List<MoviesEntity> list) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("list", json);
        editor.apply();
    }

    private List<MoviesEntity> loadList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<List<MoviesEntity>>() {
        }.getType();
        List<MoviesEntity> list = new ArrayList<>();
        list = gson.fromJson(json, type);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public void onResume() {
//        viewModel.getMoviesList().observe(requireActivity(), list -> {
//            adaptor.setData(list);
//        });
        super.onResume();
    }

}