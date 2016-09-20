package com.opendroid.droid.retofit2example;

/**
 * Created by ajaythakur on 8/23/16.
 */
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRestAdapter
{
    protected final String TAG = getClass().getSimpleName();
    protected Retrofit mRestAdapter;
    protected UserApi mApi;
    static final String TourAPP_URL = "http://192.168.0.13/composer/laravel_test/blog/public/index.php/";

    public UserRestAdapter()
    {
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(TourAPP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = mRestAdapter.create(UserApi.class); // create the interface
        Log.d(TAG, "UserRestAdapter -- created");
    }

    public void testUserApi(String username, Callback<User> callback)
    {
        Log.d(TAG, "testUserApi: for user:" + username);
        mApi.getUserFromApi(username).enqueue(callback);
    }

    public User testUserApiSync(String username)
    {
        Log.d(TAG, "testUserApi: for username:" + username);

        Call<User> call = mApi.getUserFromApi(username);
        User result = null;
        try {
            result = call.execute().body();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
