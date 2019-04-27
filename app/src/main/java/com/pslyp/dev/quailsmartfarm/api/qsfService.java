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
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface qsfService {

    @GET("user")
    Call<List<User>> getUsers();

    @GET("user")
    Call<User> checkUser(@Query("id") String id);

    @POST("user/{id}/board")
    Call<User> getBoard(@Path("id") String id);

    @POST("user/{id}/board/{token}")
    Call<Board> getBoardByToken(@Path("id") String id, @Path("token") String token);

    @POST("user/login")
    Call<User> logIn(@Query("email") String email, @Query("pass") String pass);

    @POST("user")
    Call<User> createUser(@Body User user);

    @FormUrlEncoded
    @POST("user")
    Call<User> createUser(
            @Field("id") String id,
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email
    );

    @PUT("user/{id}")
    Call<Board> updateUser(@Path("id") String id, @Body Board board);

    @PUT("user/{id}/board/{token}")
    Call<Board> updateBoard(@Path("id") String id, @Path("token") String token, @Body Board board);

}