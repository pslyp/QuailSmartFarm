package com.pslyp.dev.quailsmartfarm;

import com.pslyp.dev.quailsmartfarm.models.Status;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface quailSmartFarmApi {

    @GET("user")
    Call<Status> checkUser(@Query("id") String id);
}
