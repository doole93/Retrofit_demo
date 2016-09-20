package com.opendroid.droid.retofit2example;

/**
 * Created by ajaythakur on 8/23/16.
 */
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi
{
    @GET("users")
    Call<User> getUsers ();

    @GET("users/{username}")
    Call<User> getUserFromApi(@Path("username") String username);

    @POST("users")
    Call<User> postUserToApi (@Body User u);

    @GET("users/{username}/friends")
    Call<User> getUserFriendsFromApi(@Path("username") String username);

    @PUT("users/{username}")
    Call<User> updateUserToApi (@Body User u);

    @PUT("users/{username}/upvote")
    Call<User> downvoteUserFromApi (@Path("username") String usernameu);

    @PUT("users/{username}/downvote")
    Call<User> upvoteUserFromApi (@Path("username") String username);

    @DELETE("users/{username}")
    Call<User> deleteUserFromApi(@Path("username")String username);

}
