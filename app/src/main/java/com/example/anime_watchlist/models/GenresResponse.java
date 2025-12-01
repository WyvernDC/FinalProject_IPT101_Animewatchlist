package com.example.anime_watchlist.models;

import java.util.List;

public class GenresResponse {
    private List<Genre> data;

    public List<Genre> getData() {
        return data;
    }

    public void setData(List<Genre> data) {
        this.data = data;
    }
}
