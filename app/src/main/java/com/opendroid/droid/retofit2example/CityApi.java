package com.opendroid.droid.retofit2example;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by duce on 20-Sep-16.
 */
public interface CityApi
{
    @GET("cities}")
    Call<Comment> getCities();
}
