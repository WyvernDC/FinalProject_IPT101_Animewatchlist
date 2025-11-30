package com.example.anime_watchlist.models;

import java.io.Serializable;

public class Jpg implements Serializable {
    private String image_url;
    private String large_image_url;

    public Jpg() {
    }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getLarge_image_url() { return large_image_url; }
    public void setLarge_image_url(String large_image_url) { this.large_image_url = large_image_url; }
}
