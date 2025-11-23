package com.example.anime_watchlist;

import com.example.anime_watchlist.models.AnimeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AnimeAPI {

    @GET("anime")
    Call<AnimeResponse> searchAnime(@Query("q") String query);
}

