package com.example.anime_watchlist;

import com.example.anime_watchlist.models.AnimeResponse;
import com.example.anime_watchlist.models.GenresResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AnimeAPI {

    @GET("anime")
    Call<AnimeResponse> searchAnime(
        @Query("q") String query,
        @Query("genres") String genres
    );

    @GET("genres/anime")
    Call<GenresResponse> getAnimeGenres();

    @GET("top/anime")
    Call<AnimeResponse> getTopAnime(@Query("type") String type);

    @GET("seasons/now")
    Call<AnimeResponse> getSeasonNow();

    @GET("seasons/upcoming")
    Call<AnimeResponse> getSeasonUpcoming();
}
