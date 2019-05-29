package com.gil.heroapp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EndPointService {

    //function to make end call from the heroapp
    @GET("androidexam.json")
    Call<List<Item>> getAllHeroes();

}
