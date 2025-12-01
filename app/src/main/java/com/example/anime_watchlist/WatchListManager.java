package com.example.anime_watchlist;

import com.example.anime_watchlist.models.Anime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class WatchListManager {
    private static WatchListManager instance;
    private List<Anime> watchlist;
    private DatabaseReference databaseReference;
    private DatabaseReference ratingsReference;
    private ValueEventListener valueEventListener;
    private String currentUserId;

    public interface RatingCallback {
        void onRatingLoaded(float rating);
    }

    private WatchListManager() {
        watchlist = new ArrayList<>();
        refreshUser();
    }

    public static synchronized WatchListManager getInstance() {
        if (instance == null) {
            instance = new WatchListManager();
        }
        return instance;
    }
    
    public void refreshUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserId).child("watchlist");
            ratingsReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserId).child("ratings");
            attachDatabaseListener();
        } else {
            currentUserId = null;
            databaseReference = null;
            ratingsReference = null;
            watchlist.clear();
        }
    }

    private void attachDatabaseListener() {
        if (databaseReference == null) return;

        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                watchlist.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Anime anime = postSnapshot.getValue(Anime.class);
                    if (anime != null) {
                        watchlist.add(anime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private Anime findInList(Anime anime) {
        if (anime == null || anime.getTitle() == null) return null;
        for (Anime a : watchlist) {
            if (a.getTitle() != null && a.getTitle().equals(anime.getTitle())) {
                return a;
            }
        }
        return null;
    }

    public void addToWatchlist(Anime anime) {
        if (databaseReference != null && !isInWatchlist(anime)) {
            String key = databaseReference.push().getKey();
            if (key != null) {
                databaseReference.child(key).setValue(anime);
            }
        }
    }

    public void removeFromWatchlist(Anime anime) {
        if (databaseReference == null) return;
        
        // We need to find the key in Firebase to remove it
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Anime a = postSnapshot.getValue(Anime.class);
                    if (a != null && a.getTitle() != null && a.getTitle().equals(anime.getTitle())) {
                        postSnapshot.getRef().removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    public boolean isInWatchlist(Anime anime) {
        return findInList(anime) != null;
    }

    public List<Anime> getWatchlist() {
        return new ArrayList<>(watchlist);
    }

    public void getUserRating(Anime anime, final RatingCallback callback) {
        if (ratingsReference == null || anime == null || anime.getTitle() == null) {
            callback.onRatingLoaded(0f);
            return;
        }
        String key = sanitizeKey(anime.getTitle());
        ratingsReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Float rating = snapshot.getValue(Float.class);
                callback.onRatingLoaded(rating != null ? rating : 0f);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onRatingLoaded(0f);
            }
        });
    }

    public void setUserRating(Anime anime, float rating) {
        if (ratingsReference != null && anime != null && anime.getTitle() != null) {
            String key = sanitizeKey(anime.getTitle());
            ratingsReference.child(key).setValue(rating);
        }
    }

    private String sanitizeKey(String key) {
        // Replace characters that are not allowed in Firebase keys
        return key.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
