package com.example.mymovies.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.AndroidViewModel;

import com.example.mymovies.R;
import com.example.mymovies.databinding.ActivityMainBinding;
import com.example.mymovies.fragments.FavouritesFragment;
import com.example.mymovies.fragments.SearchFragment;
import com.example.mymovies.viewmodel.MoviesRepository;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpFragments();
    }

    private void setUpFragments() {
        replaceFragment(new SearchFragment());
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.mSearch):
                        replaceFragment(new SearchFragment());
                        break;
                    case (R.id.mFav):
                        replaceFragment(new FavouritesFragment());
                        break;
                }
                return true;
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment)
                .commit();

    }

}