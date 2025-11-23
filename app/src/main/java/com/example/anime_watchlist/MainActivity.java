package com.example.anime_watchlist;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime_watchlist.models.Anime;
import com.example.anime_watchlist.models.AnimeResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AnimeAdapter adapter;
    private AnimeAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1️⃣ Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.animeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnimeAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 2️⃣ Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.jikan.moe/v4/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(AnimeAPI.class);

        // 3️⃣ Setup search bar
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                String query = searchBar.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    searchAnime(query);
                }
                return true;
            }
            return false;
        });

        // Optional: load default search "Naruto"
        searchAnime("naruto");
    }

    // 4️⃣ Function to search anime dynamically
    private void searchAnime(String query) {
        Call<AnimeResponse> call = api.searchAnime(query);
        call.enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
