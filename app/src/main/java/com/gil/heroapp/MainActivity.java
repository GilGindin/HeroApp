package com.gil.heroapp;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MyHeroAdapter.OnItemClickListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String IMAGE_HEADR = "imageHeader";
    public static final String TEXT_HEADER = "textHeader";
    public static final String DEFAULT = "N/A";


    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private MyHeroAdapter mMyHeroAdapter;
    private ImageView mImageView;
    private TextView mTextView;
    private EndPointService mEndPointService;
    private List allResults;
    private String photo;
    private String nameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                    mTextView.setText(item.getTitle());
                    if (item.getImageUrl() != null) {
                        Picasso.with(MainActivity.this).load(item.getImageUrl()).fit().into(mImageView);
                    }
                    loadSharedPrefs();
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
        photo = clieckedItem.getImageUrl();
        nameText = clieckedItem.getTitle();

        saveToSharedPrefs();
        swapItem(position, 0);
        updateAppHeader(photo, nameText);

    }

    public void swapItem(int fromPostion, int toPosition) {
        Collections.swap(allResults, fromPostion, toPosition);
        mMyHeroAdapter.notifyItemMoved(fromPostion, toPosition);
        mRecyclerView.scrollToPosition(toPosition);
    }

    public void updateAppHeader(String photo, String name) {
        if (photo != null) {
            Picasso.with(MainActivity.this).load(photo).fit().into(mImageView);
        }
        mTextView.setText(nameText);
    }

    private void saveToSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(IMAGE_HEADR, photo);
        editor.putString(TEXT_HEADER, nameText);
        editor.commit();
    }

    private void loadSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String loadPhoto = sharedPreferences.getString(IMAGE_HEADR, DEFAULT);
        String loadNameText = sharedPreferences.getString(TEXT_HEADER, DEFAULT);

        if (loadNameText.equals(DEFAULT) || loadPhoto.equals(DEFAULT)) {

        } else {

            if (loadPhoto != null) {
                Picasso.with(MainActivity.this).load(loadPhoto).fit().into(mImageView);
            }
            mTextView.setText(loadNameText);
        }
    }


}
