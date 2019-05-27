package com.gil.heroapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Item {

    private int id;
    @SerializedName("title")
    private String title;

    @SerializedName("abilities")
    private ArrayList<String> abilitiesObj;

    @SerializedName("image")
    private String imageUrl;

    private Boolean isClicked = false;

    public Item(String title, ArrayList<String> abilitiesObj, String imageUrl) {
        this.title = title;
        this.abilitiesObj = abilitiesObj;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<String> getAbilitiesObj() {
        return abilitiesObj;
    }

    public Boolean getClicked() {
        return isClicked;
    }
}
