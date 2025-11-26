package com.example.anime_watchlist.models;

import java.io.Serializable;

public class Anime implements Serializable {
    private String title;
    private Integer episodes;
    private Double score;
    private String synopsis;
    private String rating;
    private String duration;
    private Images images;

    public String getTitle() { return title; }
    public Integer getEpisodes() { return episodes; }
    public Double getScore() { return score; }
    public String getSynopsis() { return synopsis; }
    public String getRating() { return rating; }
    public String getDuration() { return duration; }
    public Images getImages() { return images; }
}
