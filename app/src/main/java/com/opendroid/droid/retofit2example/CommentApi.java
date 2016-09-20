package com.opendroid.droid.retofit2example;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by duce on 20-Sep-16.
 */

public interface CommentApi
{
    @GET("comments/{username}")
    Call<Comment> getComments(@Path("username") String username);

    @POST("comments/add")
    Call<Comment> postCommentToApi (@Body Comment c);
}
