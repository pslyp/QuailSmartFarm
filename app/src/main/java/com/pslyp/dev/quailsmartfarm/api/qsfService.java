package com.pslyp.dev.quailsmartfarm.api;

import com.pslyp.dev.quailsmartfarm.models.Status;
import com.pslyp.dev.quailsmartfarm.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface qsfService {

    @GET("user")
    Call<List<User>> getUsers();

    @GET("user/{id}/board")
    Call<User> getBoard(@Path("id") String id);

    @GET("user")
    Call<Status> checkUser(@Query("id") String id);

}