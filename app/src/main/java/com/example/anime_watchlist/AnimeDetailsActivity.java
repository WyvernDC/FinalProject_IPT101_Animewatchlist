package com.example.anime_watchlist;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.anime_watchlist.models.Anime;
import com.google.firebase.auth.FirebaseAuth;

public class AnimeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_details);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get Anime object from Intent
        Anime anime = (Anime) getIntent().getSerializableExtra("anime_data");

        if (anime != null) {
            ImageView heroImage = findViewById(R.id.img_hero_background);
            TextView title = findViewById(R.id.tv_title);
            TextView metadata = findViewById(R.id.tv_metadata);
            TextView description = findViewById(R.id.tv_description);
            Button addToWatchlistBtn = findViewById(R.id.btn_add_watchlist);
            RatingBar ratingBar = findViewById(R.id.rating_bar);

            if (title != null) title.setText(anime.getTitle());
            
            String metaText = "";
            if (anime.getRating() != null) metaText += anime.getRating();
            if (anime.getScore() != null) metaText += " | Score: " + anime.getScore();
            if (anime.getEpisodes() != null) metaText += " | " + anime.getEpisodes() + " eps";
            
            if (metadata != null) metadata.setText(metaText);
            
            if (description != null) {
                if (anime.getSynopsis() != null) {
                    description.setText(anime.getSynopsis());
                } else {
                    description.setText("No description available.");
                }
            }

            if (heroImage != null && anime.getImages() != null && anime.getImages().getJpg() != null) {
                String imageUrl = anime.getImages().getJpg().getLarge_image_url();
                if (imageUrl == null) imageUrl = anime.getImages().getJpg().getImage_url();
                
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background) // Fallback
                        .into(heroImage);
            }

            if (addToWatchlistBtn != null) {
                updateWatchlistButtonState(addToWatchlistBtn, anime);
                
                addToWatchlistBtn.setOnClickListener(v -> {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Toast.makeText(this, "Please log in to add to watchlist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (WatchListManager.getInstance().isInWatchlist(anime)) {
                        WatchListManager.getInstance().removeFromWatchlist(anime);
                        Toast.makeText(this, "Removed from Watchlist", Toast.LENGTH_SHORT).show();
                        addToWatchlistBtn.setText("ADD TO WATCHLIST");
                    } else {
                        WatchListManager.getInstance().addToWatchlist(anime);
                        Toast.makeText(this, "Added to Watchlist", Toast.LENGTH_SHORT).show();
                        addToWatchlistBtn.setText("REMOVE FROM WATCHLIST");
                    }
                });
            }

            if (ratingBar != null) {
                // Initialize rating from API (Firebase)
                WatchListManager.getInstance().getUserRating(anime, rating -> {
                    ratingBar.setRating(rating);
                });

                ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
                    if (fromUser) {
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(this, "Please log in to rate", Toast.LENGTH_SHORT).show();
                            // Reset logic if needed, or just ignore
                            return;
                        }
                        WatchListManager.getInstance().setUserRating(anime, rating);
                        Toast.makeText(AnimeDetailsActivity.this, "Rating saved: " + rating, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void updateWatchlistButtonState(Button btn, Anime anime) {
        if (WatchListManager.getInstance().isInWatchlist(anime)) {
            btn.setText("REMOVE FROM WATCHLIST");
        } else {
            btn.setText("ADD TO WATCHLIST");
        }
    }
}
