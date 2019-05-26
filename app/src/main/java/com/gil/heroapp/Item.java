package com.gil.heroapp;

import com.google.gson.annotations.SerializedName;



public class Item {

    private int id;
    @SerializedName("title")
    private String title;

    @SerializedName("abilities")
    private int[] abilitiesObj;

    @SerializedName("image")
    private String imageUrl;

    public Item(String title, int[] abilitiesObj, String imageUrl) {
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


    public int[] getAbilitiesObj() {
        return abilitiesObj;
    }

    public class Abilities {

        private int[] abilities;

        public int[] getAbilities() {
            return abilities;
        }
    }


}
