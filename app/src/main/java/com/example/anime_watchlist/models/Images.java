package com.example.anime_watchlist.models;

import java.io.Serializable;

public class Images implements Serializable {
    private Jpg jpg;

    public Images() {
    }

    public Jpg getJpg() { return jpg; }
    public void setJpg(Jpg jpg) { this.jpg = jpg; }
}
