package com.example.mymovies.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.mymovies.databinding.ActivityPosterBinding;
import com.squareup.picasso.Picasso;

public class PosterActivity extends AppCompatActivity {
    ActivityPosterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String url = getIntent().getStringExtra("poster");

//        Picasso.get()
//                .load(url)
//                .into(binding.posterImage);

        Glide.with(getApplicationContext())
                .load(url)
                .into(binding.posterImage);
    }
}