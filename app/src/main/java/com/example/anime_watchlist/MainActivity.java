package com.example.anime_watchlist;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anime_watchlist.models.AnimeResponse;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerLatest, recyclerUpcoming, recyclerTop, recyclerGrid;
    private RecyclerView recyclerAction, recyclerRomance;
    private AnimeAdapter adapterLatest, adapterUpcoming, adapterTop, adapterGrid;
    private AnimeAdapter adapterAction, adapterRomance;
    private AnimeAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        recyclerLatest = findViewById(R.id.recycler_latest);
        recyclerUpcoming = findViewById(R.id.recycler_upcoming);
        recyclerTop = findViewById(R.id.recycler_top_movie);
        recyclerGrid = findViewById(R.id.recycler_grid);
        recyclerAction = findViewById(R.id.recycler_action);
        recyclerRomance = findViewById(R.id.recycler_romance);

        // Initialize API
        api = RetrofitClient.getInstance().create(AnimeAPI.class);

        // Setup Adapters and LayoutManagers
        setupRecyclerView(recyclerLatest, adapterLatest = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card), true);
        setupRecyclerView(recyclerUpcoming, adapterUpcoming = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card), true);
        setupRecyclerView(recyclerTop, adapterTop = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card), true);
        setupRecyclerView(recyclerAction, adapterAction = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card), true);
        setupRecyclerView(recyclerRomance, adapterRomance = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card), true);

        // Grid Layout for "For You"
        adapterGrid = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        recyclerGrid.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerGrid.setAdapter(adapterGrid);

        // Load Data
        loadLatestAnime();
        loadUpcomingAnime();
        loadTopAnime();
        loadGridAnime(); // "For You" (e.g. Action search)
        loadActionAnime();
        loadRomanceAnime();

        // Setup Listeners for Nav Bar
        findViewById(R.id.layout_search_bar).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BrowseActivity.class));
        });

        findViewById(R.id.tv_list_button).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WatchListActivity.class));
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView, AnimeAdapter adapter, boolean isHorizontal) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, isHorizontal ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void loadLatestAnime() {
        api.getSeasonNow().enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterLatest.updateData(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadUpcomingAnime() {
        api.getSeasonUpcoming().enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterUpcoming.updateData(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadTopAnime() {
        api.getTopAnime("movie").enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterTop.updateData(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadGridAnime() {
        api.searchAnime("action").enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterGrid.updateData(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadActionAnime() {
        api.searchAnime("action").enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterAction.updateData(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadRomanceAnime() {
        api.searchAnime("romance").enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapterRomance.updateData(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {}
        });
    }
}
