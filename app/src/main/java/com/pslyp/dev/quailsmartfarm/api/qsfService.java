package com.pslyp.dev.quailsmartfarm.api;

import com.pslyp.dev.quailsmartfarm.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface qsfService {

    @GET("user")
    Call<User> getUsers();

    @GET("user")
    Call<User> checkUser(@Query("id") String id);

}