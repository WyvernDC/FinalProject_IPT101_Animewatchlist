package com.example.anime_watchlist;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anime_watchlist.models.AnimeResponse;
import com.example.anime_watchlist.models.Genre;
import com.example.anime_watchlist.models.GenresResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseActivity extends AppCompatActivity {

    private AutoCompleteTextView etSearch;
    private RecyclerView recyclerResults, recyclerGenres;
    private TextView tvNoResults;
    private ImageView btnFilter, btnClearFilter;
    private AnimeAdapter adapterResults;
    private GenreAdapter adapterGenres;
    private AnimeAPI api;
    private List<Genre> selectedGenres = new ArrayList<>();
    private Map<String, Genre> genreMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // Init API
        api = RetrofitClient.getInstance().create(AnimeAPI.class);

        // Init Views
        etSearch = findViewById(R.id.et_browse_query);
        recyclerResults = findViewById(R.id.recycler_browse_results);
        recyclerGenres = findViewById(R.id.recycler_genres);
        tvNoResults = findViewById(R.id.tv_no_results);
        btnFilter = findViewById(R.id.btn_filter_browse);
        btnClearFilter = findViewById(R.id.btn_clear_filter);
        ImageView btnBack = findViewById(R.id.btn_back_browse);

        // Setup Results Adapter
        adapterResults = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        recyclerResults.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerResults.setAdapter(adapterResults);

        // Setup Genre Adapter for Horizontal List
        adapterGenres = new GenreAdapter(new ArrayList<>(), genres -> {
            selectedGenres = genres;
            updateClearButtonVisibility();
            performSearch(etSearch.getText().toString());
        });
        recyclerGenres.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerGenres.setAdapter(adapterGenres);

        // Fetch Genres for both List and AutoComplete
        loadGenres();

        // Listeners
        btnBack.setOnClickListener(v -> finish());

        btnFilter.setOnClickListener(v -> {
            if (recyclerGenres.getVisibility() == View.VISIBLE) {
                recyclerGenres.setVisibility(View.GONE);
            } else {
                recyclerGenres.setVisibility(View.VISIBLE);
            }
        });

        btnClearFilter.setOnClickListener(v -> {
            selectedGenres.clear();
            adapterGenres.clearSelection();
            etSearch.setText("");
            updateClearButtonVisibility();
            performSearch("");
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = v.getText().toString().trim();
                
                // Check if text matches a genre (case-insensitive check via map)
                if (genreMap.containsKey(query.toLowerCase())) {
                    Genre g = genreMap.get(query.toLowerCase());
                    if (g != null) {
                        etSearch.setText("");
                        recyclerGenres.setVisibility(View.VISIBLE);
                        adapterGenres.selectGenreById(g.getMal_id());
                        hideKeyboard();
                        return true;
                    }
                }

                performSearch(query);
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Handle AutoComplete Selection - Adds as Filter
        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String genreName = (String) parent.getItemAtPosition(position);
            // Map keys for display names might be capitalized, but we stored lower case keys too? 
            // Wait, loadGenres stores original name -> Genre, and lowercase -> Genre?
            // Let's check loadGenres impl below.
            
            // Actually, let's just look up the name directly.
            // The adapter is populated with `genreNames` (Display names).
            // So `genreName` is "Action".
            // We need a map from DisplayName -> Genre.
            
            Genre selectedGenre = null;
            // Simple linear search or use map if keys match
            for (Map.Entry<String, Genre> entry : genreMap.entrySet()) {
                if (entry.getValue().getName().equalsIgnoreCase(genreName)) {
                    selectedGenre = entry.getValue();
                    break;
                }
            }

            if (selectedGenre != null) {
                etSearch.setText(""); 
                recyclerGenres.setVisibility(View.VISIBLE);
                adapterGenres.selectGenreById(selectedGenre.getMal_id());
                hideKeyboard();
            }
        });
        
        // Focus search
        etSearch.requestFocus();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    private void updateClearButtonVisibility() {
        if (!selectedGenres.isEmpty() || !etSearch.getText().toString().isEmpty()) {
            btnClearFilter.setVisibility(View.VISIBLE);
        } else {
            btnClearFilter.setVisibility(View.GONE);
        }
    }

    private void loadGenres() {
        api.getAnimeGenres().enqueue(new Callback<GenresResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenresResponse> call, @NonNull Response<GenresResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> genres = response.body().getData();
                    
                    // Update Chip Adapter
                    adapterGenres.updateGenres(genres);
                    
                    // Update AutoComplete
                    List<String> genreNames = new ArrayList<>();
                    genreMap.clear();
                    for (Genre g : genres) {
                        // Store lowercase key for case-insensitive matching
                        genreMap.put(g.getName().toLowerCase(), g);
                        genreNames.add(g.getName());
                    }
                    
                    ArrayAdapter<String> autoAdapter = new ArrayAdapter<>(
                        BrowseActivity.this, 
                        android.R.layout.simple_dropdown_item_1line, 
                        genreNames
                    );
                    etSearch.setAdapter(autoAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenresResponse> call, @NonNull Throwable t) {}
        });
    }

    private void performSearch(String query) {
        String searchQuery = query.trim();
        
        // If query is empty and no genres selected, show default state
        if (searchQuery.isEmpty() && selectedGenres.isEmpty()) {
            tvNoResults.setText("Search for your favorite anime");
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerResults.setVisibility(View.GONE);
            return;
        }
        
        tvNoResults.setText("Searching...");
        tvNoResults.setVisibility(View.VISIBLE);
        recyclerResults.setVisibility(View.GONE);

        // Build comma-separated genre IDs
        StringBuilder genreIds = new StringBuilder();
        for (int i = 0; i < selectedGenres.size(); i++) {
            genreIds.append(selectedGenres.get(i).getMal_id());
            if (i < selectedGenres.size() - 1) {
                genreIds.append(",");
            }
        }

        api.searchAnime(searchQuery.isEmpty() ? null : searchQuery, genreIds.length() > 0 ? genreIds.toString() : null)
                .enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnimeResponse> call, @NonNull Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getData() == null || response.body().getData().isEmpty()) {
                        tvNoResults.setText("No results found");
                        tvNoResults.setVisibility(View.VISIBLE);
                        recyclerResults.setVisibility(View.GONE);
                    } else {
                        adapterResults.updateData(response.body().getData());
                        recyclerResults.setVisibility(View.VISIBLE);
                        tvNoResults.setVisibility(View.GONE);
                    }
                } else {
                    tvNoResults.setText("Error searching");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnimeResponse> call, @NonNull Throwable t) {
                tvNoResults.setText("Network error");
            }
        });
    }
}
