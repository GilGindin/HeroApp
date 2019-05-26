package com.gil.heroapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MyHeroAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private MyHeroAdapter mMyHeroAdapter;
    private ImageView mImageView;
    private TextView mTextView;
    private EndPointService mEndPointService;
    private List allResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initialButtons();
        createRetrofitCallback();

    }

    private void initialButtons() {
        mRecyclerView = findViewById(R.id.recycler_view_list_of_heroes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mImageView = findViewById(R.id.image_view_hero);
        mTextView = findViewById(R.id.text_view_hero_represnting);
    }

    private void createRetrofitCallback() {
        mEndPointService = RetrofitInstance.getRetrofitInstance().create(EndPointService.class);
        Call<List<Item>> myCall = mEndPointService.getAllHeroes();
        myCall.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (!response.isSuccessful()) {
                    Log.d("", "onResponse:---------- " + response.code());
                    return;
                }
                for (Item item : response.body()) {
                    allResults = response.body();
                    Log.d(TAG, "onResponse: --------" + response.body());
                    mTextView.setText(item.getTitle());
                    if (item.getImageUrl() != null){
                        Picasso.with(MainActivity.this).load(item.getImageUrl()).fit().into(mImageView);
                    }

                }

                mMyHeroAdapter = new MyHeroAdapter(MainActivity.this, allResults);
                mRecyclerView.setAdapter(mMyHeroAdapter);
                mMyHeroAdapter.setOnItemClickListener(MainActivity.this);
                mMyHeroAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("", "onFailure:------------------- " + t.getMessage());
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Item clieckedItem = (Item) allResults.get(position);
        String photo = clieckedItem.getImageUrl();
        String name = clieckedItem.getTitle();

        updateAppHeader(photo , name);

        Toast.makeText(this, "clickedddd", Toast.LENGTH_SHORT).show();
    }

    private void updateAppHeader(String photo , String name) {

        if (photo != null){
            Picasso.with(MainActivity.this).load(photo).fit().into(mImageView);
        }
        mTextView.setText(name);
    }
}
