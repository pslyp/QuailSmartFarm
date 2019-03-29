package com.pslyp.dev.quailsmartfarm.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAPI {

    private Retrofit retrofit;
    private static final String BASE_URL = "https://quailsmartfarm.herokuapp.com";

    public RestAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public qsfService getQsfService() {
        return retrofit.create(qsfService.class);
    }

}
