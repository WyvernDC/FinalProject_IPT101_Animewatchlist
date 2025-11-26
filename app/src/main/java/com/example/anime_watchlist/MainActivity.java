package com.example.anime_watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anime_watchlist.models.AnimeResponse;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AnimeAdapter adapterLatest, adapterUpcoming, adapterGrid, adapterTop;
    private AnimeAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.jikan.moe/v4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(AnimeAPI.class);

        // Views
        EditText etSearchInput = findViewById(R.id.et_search_input);
        TextView tvWatchList = findViewById(R.id.tv_list_button);

        // Setup Adapters
        adapterLatest = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        adapterUpcoming = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        adapterTop = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        adapterGrid = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);

        // Setup RecyclerViews
        setupRecyclerView(R.id.recycler_latest, adapterLatest);
        setupRecyclerView(R.id.recycler_upcoming, adapterUpcoming);
        setupRecyclerView(R.id.recycler_top_movie, adapterTop);
        setupRecyclerView(R.id.recycler_grid, adapterGrid);

        // Search Logic - Open Browse Activity
        etSearchInput.setFocusable(false);
        etSearchInput.setClickable(true);
        etSearchInput.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
            startActivity(intent);
        });
        
        // Also click listener for the container if needed, but Edittext handles it mostly.
        findViewById(R.id.layout_search_bar).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
            startActivity(intent);
        });

        // Watch List Button
        tvWatchList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WatchListActivity.class);
            startActivity(intent);
        });

        // Load Initial Data
        loadLatestMovies();
        loadUpcomingAnime();
        loadTopMovies();
        loadGridAnime();
    }

    private void setupRecyclerView(int recyclerViewId, AnimeAdapter adapter) {
        RecyclerView recyclerView = findViewById(recyclerViewId);
        recyclerView.setAdapter(adapter);
    }

    private void loadLatestMovies() {
        fetchAndPopulate(api.getSeasonNow(), adapterLatest);
    }

    private void loadUpcomingAnime() {
        fetchAndPopulate(api.getSeasonUpcoming(), adapterUpcoming);
    }

    private void loadGridAnime() {
        fetchAndPopulate(api.searchAnime("action"), adapterGrid);
    }

    private void loadTopMovies() {
        fetchAndPopulate(api.getTopAnime("movie"), adapterTop);
    }

    private void fetchAndPopulate(Call<AnimeResponse> call, AnimeAdapter adapter) {
        call.enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body().getData());
                }
            }
            @Override public void onFailure(Call<AnimeResponse> call, Throwable t) {}
        });
    }
}
