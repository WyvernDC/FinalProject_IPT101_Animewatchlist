package com.example.anime_watchlist.models;

import java.io.Serializable;

public class Jpg implements Serializable {
    private String image_url;
    private String large_image_url;

    public String getImage_url() { return image_url; }
    public String getLarge_image_url() { return large_image_url; }
}
