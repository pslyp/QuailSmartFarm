package com.pslyp.dev.quailsmartfarm.api;

import com.pslyp.dev.quailsmartfarm.models.Board;
import com.pslyp.dev.quailsmartfarm.models.Status;
import com.pslyp.dev.quailsmartfarm.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface qsfService {

    @GET("user")
    Call<List<User>> getUsers();

    @GET("user/{id}/board")
    Call<User> getBoard(@Path("id") String id);

    @GET("user")
    Call<Status> checkUser(@Query("id") String id);

    @POST("user/create")
    Call<User> createUser(@Body User user);

    @FormUrlEncoded
    @POST("user")
    Call<User> createUser(
            @Field("id") String id,
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email
    );

    @POST("user/{id}")
    Call<Board> insertBoard(@Path("id") String id, @Body Board board);

}