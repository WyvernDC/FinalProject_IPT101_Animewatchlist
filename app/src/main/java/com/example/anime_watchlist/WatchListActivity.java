package com.example.anime_watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anime_watchlist.models.Anime;
import java.util.ArrayList;
import java.util.List;

public class WatchListActivity extends AppCompatActivity {

    private RecyclerView moviesRecyclerView;
    private AnimeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        // Back Button Logic
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            // Go back to MainActivity (or previous screen)
            finish(); 
        });

        moviesRecyclerView = findViewById(R.id.moviesRecyclerView);

        adapter = new AnimeAdapter(new ArrayList<>(), R.layout.item_anime_card);
        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        moviesRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWatchlistData();
    }

    private void loadWatchlistData() {
        List<Anime> watchlist = WatchListManager.getInstance().getWatchlist();
        adapter.updateData(watchlist);
    }
}
