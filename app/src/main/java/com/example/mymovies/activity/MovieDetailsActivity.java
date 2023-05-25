package com.example.mymovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mymovies.R;
import com.example.mymovies.databinding.ActivityMovieDetailsBinding;
import com.example.mymovies.model.MovieDetailsResponse;
import com.example.mymovies.retrofit.RetrofitClient;
import com.example.mymovies.room.Database;
import com.example.mymovies.room.MoviesDAO;
import com.example.mymovies.room.MoviesEntity;
import com.example.mymovies.viewmodel.MoviesRepository;
import com.example.mymovies.viewmodel.MoviesViewModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    ActivityMovieDetailsBinding binding;
    MoviesViewModel viewModel;
    MoviesRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = new MoviesRepository(getApplication());
        viewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        String title = getIntent().getStringExtra("title");
        String name = getIntent().getStringExtra("name");
        if (getIntent().getStringExtra("title") != null) {
            getMovieDetails(title);
        } else if (getIntent().getStringExtra("name") != null) {
            getMovieDetails(name);
        }

        binding.ivBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getMovieDetails(String title) {
        Call<MovieDetailsResponse> responseCall = RetrofitClient.getInstance().getMyApi().getMovieDetails(title);
        responseCall.enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                MovieDetailsResponse detailsResponse = response.body();
                if (detailsResponse != null) {
                    binding.tvCountry.setText(detailsResponse.getCountry());
                    binding.tvPlot.setText(detailsResponse.getPlot());
                    binding.tvRuntime.setText(detailsResponse.getRuntime());
                    binding.tvReleased.setText(detailsResponse.getReleased());
                    binding.tvMovieName.setText(detailsResponse.getTitle());
                    binding.tvDirector.setText(detailsResponse.getDirector());
                    binding.tvRated.setText(detailsResponse.getRated());
                    binding.tvRating.setText(detailsResponse.getImdbRating());
                    binding.tvGenre.setText(detailsResponse.getGenre());
                    binding.tvBoxOffice.setText(detailsResponse.getBoxOffice());
                    binding.tvLanguage.setText(detailsResponse.getLanguage());
                    binding.tvActors.setText(detailsResponse.getActors());
                    binding.tvAwards.setText(detailsResponse.getAwards());
                    if (!detailsResponse.getPoster().equals("N/A")) {
                        Glide.with(getApplicationContext())
                                .load(detailsResponse.getPoster())
                                .into(binding.ivMoviePoster);
                    } else
                        binding.ivMoviePoster.setImageResource(R.drawable.img_3);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.scroolView.setVisibility(View.VISIBLE);
                    binding.appBar.setVisibility(View.VISIBLE);
                    binding.ivMoviePoster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MovieDetailsActivity.this, PosterActivity.class);
                            intent.putExtra("poster", detailsResponse.getPoster());
                            startActivity(intent);
                        }
                    });

                    if (viewModel.ifIdAlreadyExisted(detailsResponse.getImdbID()) == 0) {
                        binding.ivFavorite.setImageResource(R.drawable.ic_favourite);
                    } else {
                        binding.ivFavorite.setImageResource(R.drawable.favorite_red);
                    }

                    binding.ivFavorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (viewModel.ifIdAlreadyExisted(detailsResponse.getImdbID()) == 0) {
                                viewModel.add(new MoviesEntity(
                                        detailsResponse.getImdbID(),
                                        detailsResponse.getTitle(),
                                        detailsResponse.getYear(),
                                        detailsResponse.getPoster()));
                                binding.ivFavorite.setImageResource(R.drawable.favorite_red);
                                Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                            } else {
                                viewModel.deleteByImdbID(detailsResponse.getImdbID());
                                binding.ivFavorite.setImageResource(R.drawable.ic_favourite);
                                Toast.makeText(getApplicationContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    binding.ivShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            intent.putExtra(Intent.EXTRA_TITLE, detailsResponse.getTitle());
                            intent.putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Poster: " + detailsResponse.getPoster() + "\n" + "\n" +
                                            "Title: " + detailsResponse.getTitle() + "\n" +
                                            "Released: " + detailsResponse.getReleased() + "\n" +
                                            "IMDB Rating: " + detailsResponse.getImdbRating() + "\n" +
                                            "Runtime: " + detailsResponse.getRuntime() + "\n" +
                                            "Language: " + detailsResponse.getLanguage() + "\n" +
                                            "Genre: " + detailsResponse.getGenre() + "\n" +
                                            "Director: " + detailsResponse.getDirector() + "\n" +
                                            "Actors: " + detailsResponse.getActors() + "\n" +
                                            "Awards: " + detailsResponse.getAwards() + "\n" +
                                            "BoxOffice: " + detailsResponse.getBoxOffice() + "\n" +
                                            "Country: " + detailsResponse.getCountry()
                            );
                            intent.setType("text/plain");
                            startActivity(Intent.createChooser(intent, "Share movie details using"));
                        }
                    });

                } else {
                    binding.scroolView.setVisibility(View.GONE);
                    binding.appBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Data = Null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetailsResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}