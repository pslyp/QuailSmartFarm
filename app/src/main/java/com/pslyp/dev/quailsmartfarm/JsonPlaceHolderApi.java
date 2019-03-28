package com.pslyp.dev.quailsmartfarm;

import com.pslyp.dev.quailsmartfarm.models.Board;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("/user/{id}/board")
    Call<Board>
}
