package com.example.anime_watchlist;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anime_watchlist.models.AnimeResponse;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView recyclerView;
    private TextView tvNoResults;
    private AnimeAdapter adapter;
    private AnimeAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        // Init API using Singleton Client
        api = RetrofitClient.getInstance().create(AnimeAPI.class);

        // Init Views
        etSearch = findViewById(R.id.et_browse_query);
        recyclerView = findViewById(R.id.recycler_browse_results);
        tvNoResults = findViewById(R.id.tv_no_results);
        ImageView btnBack = findViewById(R.id.btn_back_browse);

        // Init Adapter
        adapter = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        recyclerView.setAdapter(adapter);

        // Listeners
        btnBack.setOnClickListener(v -> finish());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.getText().toString());
                return true;
            }
            return false;
        });
        
        // Focus search immediately
        etSearch.requestFocus();
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;
        
        tvNoResults.setText("Searching...");
        tvNoResults.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        api.searchAnime(query).enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getData() == null || response.body().getData().isEmpty()) {
                        tvNoResults.setText("No results found");
                        tvNoResults.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        adapter.updateData(response.body().getData());
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoResults.setVisibility(View.GONE);
                    }
                } else {
                    tvNoResults.setText("Error searching");
                }
            }

            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable t) {
                tvNoResults.setText("Network error");
            }
        });
    }
}
