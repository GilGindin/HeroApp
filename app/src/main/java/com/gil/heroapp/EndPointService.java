package com.gil.heroapp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EndPointService {

    @GET("androidexam.json")
    Call<List<Item>> getAllHeroes();

}
