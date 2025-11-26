package com.example.anime_watchlist;

import com.example.anime_watchlist.models.Anime;
import java.util.ArrayList;
import java.util.List;

public class WatchListManager {
    private static WatchListManager instance;
    private List<Anime> watchlist;

    private WatchListManager() {
        watchlist = new ArrayList<>();
    }

    public static synchronized WatchListManager getInstance() {
        if (instance == null) {
            instance = new WatchListManager();
        }
        return instance;
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
        if (!isInWatchlist(anime)) {
            watchlist.add(anime);
        }
    }

    public void removeFromWatchlist(Anime anime) {
        Anime toRemove = findInList(anime);
        if (toRemove != null) {
            watchlist.remove(toRemove);
        }
    }

    public boolean isInWatchlist(Anime anime) {
        return findInList(anime) != null;
    }

    public List<Anime> getWatchlist() {
        return new ArrayList<>(watchlist);
    }
}
