package com.example.anime_watchlist.models;

import java.io.Serializable;
import java.util.List;

public class Anime implements Serializable {
    private String title;
    private Integer episodes;
    private Double score;
    private String synopsis;
    private String rating;
    private String duration;
    private Images images;
    private List<Genre> genres;

    public Anime() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getEpisodes() { return episodes; }
    public void setEpisodes(Integer episodes) { this.episodes = episodes; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Images getImages() { return images; }
    public void setImages(Images images) { this.images = images; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }
}
