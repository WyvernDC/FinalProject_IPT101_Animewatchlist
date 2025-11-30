package com.example.anime_watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anime_watchlist.models.Anime;
import com.example.anime_watchlist.models.AnimeResponse;
import com.example.anime_watchlist.models.Genre;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // Use item_anime_card_horizontal for horizontal scrolling lists to ensure proper sizing and resolution
        setupRecyclerView(recyclerLatest, adapterLatest = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card_horizontal), true);
        setupRecyclerView(recyclerUpcoming, adapterUpcoming = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card_horizontal), true);
        setupRecyclerView(recyclerTop, adapterTop = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card_horizontal), true);
        setupRecyclerView(recyclerAction, adapterAction = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card_horizontal), true);
        setupRecyclerView(recyclerRomance, adapterRomance = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card_horizontal), true);

        // Grid Layout for "For You" - Uses the responsive card layout (match_parent width for column fit)
        adapterGrid = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        recyclerGrid.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerGrid.setAdapter(adapterGrid);

        // Load Data - Staggered to prevent Rate Limiting (429)
        loadLatestAnime();

        new Handler(Looper.getMainLooper()).postDelayed(this::loadUpcomingAnime, 1000);

        new Handler(Looper.getMainLooper()).postDelayed(this::loadTopAnime, 2000);

        new Handler(Looper.getMainLooper()).postDelayed(this::loadActionAnime, 3500);

        new Handler(Looper.getMainLooper()).postDelayed(this::loadForYouAnime, 5000);

        new Handler(Looper.getMainLooper()).postDelayed(this::loadRomanceAnime, 6500);

        // Setup Listeners for Nav Bar
        findViewById(R.id.layout_search_bar).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BrowseActivity.class));
        });

        findViewById(R.id.tv_list_button).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WatchListActivity.class));
        });

        // New Profile Listener
        findViewById(R.id.img_profile).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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

    private void loadActionAnime() {
        api.searchAnime("action", null).enqueue(new Callback<AnimeResponse>() {
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

    private void loadForYouAnime() {
        // Get watchlist
        List<Anime> watchlist = WatchListManager.getInstance().getWatchlist();
        String genreIdToSearch = "1"; // Default to Action (ID 1)

        if (!watchlist.isEmpty()) {
            Map<Integer, Integer> genreCounts = new HashMap<>();
            for (Anime anime : watchlist) {
                if (anime.getGenres() != null) {
                    for (Genre g : anime.getGenres()) {
                        genreCounts.put(g.getMal_id(), genreCounts.getOrDefault(g.getMal_id(), 0) + 1);
                    }
                }
            }

            // Find most frequent genre
            int maxId = -1;
            int maxCount = -1;
            for (Map.Entry<Integer, Integer> entry : genreCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    maxId = entry.getKey();
                }
            }

            if (maxId != -1) {
                genreIdToSearch = String.valueOf(maxId);
            }
        }

        api.searchAnime(null, genreIdToSearch).enqueue(new Callback<AnimeResponse>() {
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

    private void loadRomanceAnime() {
        api.searchAnime("romance", null).enqueue(new Callback<AnimeResponse>() {
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