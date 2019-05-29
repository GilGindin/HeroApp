package com.gil.heroapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
    private List allResults;
    private String photo;
    private String nameText;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.collapse_activity_main);

        initialViews();

        //checking internet connection , if needed move the user to internet settings
        if (isOnline()) {
            createRetrofitCallback();
        } else {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isOnline()) {
            createRetrofitCallback();
        } else startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isOnline()) {
            createRetrofitCallback();
        } else startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    //initial the views on MainActivity
    private void initialViews() {
        mRecyclerView = findViewById(R.id.recycler_view_list_of_heroes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mImageView = findViewById(R.id.image_view_hero);
        mTextView = findViewById(R.id.text_view_hero_represnting);

    }

    //creating Retrofit callBack to HeroApp api
    private void createRetrofitCallback() {
        EndPointService mEndPointService = RetrofitInstance.getRetrofitInstance().create(EndPointService.class);
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
                runAnimation(mRecyclerView, 0);

            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("", "onFailure:------------------- " + t.getMessage());
            }
        });
    }

    //animiation for the recyclerview list at upload
    private void runAnimation(RecyclerView recyclerView, int type) {

        Context context = mRecyclerView.getContext();
        LayoutAnimationController controller = null;

        if (type == 0) {
            controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slider_from_left);
            setAdpter();
            mRecyclerView.setLayoutAnimation(controller);

        }
    }


    //function override from the recyclerview adapter , get item clicked position
    @Override
    public void onItemClick(int position) {
        Item clieckedItem = (Item) allResults.get(position);
        photo = clieckedItem.getImageUrl();
        nameText = clieckedItem.getTitle();

        saveToSharedPrefs();
        updateAppHeader(photo, nameText);
        swapItem(position, 0);
    }

    //function override from the recyclerview adpater , moving the clicked item to top of the list
    public void swapItem(int fromPostion, final int toPosition) {
        Collections.swap(allResults, fromPostion, toPosition);
        mMyHeroAdapter.notifyItemMoved(fromPostion, toPosition);
        mRecyclerView.smoothScrollToPosition(mMyHeroAdapter.getItemCount());
    }

    //function override from the recyclerview adapter , ypload the header image and text with
    //current item clicked's image and text
    public void updateAppHeader(String photo, String name) {
        if (photo != null) {
            Picasso.with(MainActivity.this).load(photo).fit().into(mImageView);
        }
        mTextView.setText(nameText);
    }

    //save image and text header to sharedPreferences
    private void saveToSharedPrefs() {
        mSharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(IMAGE_HEADR, photo);
        editor.putString(TEXT_HEADER, nameText);
        editor.commit();
    }

    //load data through sharedPreferences
    private void loadSharedPrefs() {
        mSharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String loadPhoto = mSharedPreferences.getString(IMAGE_HEADR, DEFAULT);
        String loadNameText = mSharedPreferences.getString(TEXT_HEADER, DEFAULT);

        if (loadNameText.equals(DEFAULT) || loadPhoto.equals(DEFAULT)) {

        } else {

            if (loadPhoto != null) {
                Picasso.with(MainActivity.this).load(loadPhoto).fit().into(mImageView);
            }
            mTextView.setText(loadNameText);
        }
    }

    //internet connection check
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //set the adapter with the recyclerview
    public void setAdpter() {
        mRecyclerView.setAdapter(mMyHeroAdapter);
        mMyHeroAdapter.setOnItemClickListener(MainActivity.this);
        mMyHeroAdapter.notifyDataSetChanged();

    }

}
