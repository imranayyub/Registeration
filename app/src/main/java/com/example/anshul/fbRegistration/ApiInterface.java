package com.example.anshul.fbRegistration;


import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by Im on 21-11-2017.
 */

public interface ApiInterface {
    String BASE_URL = "http://192.168.16.241:3000/";
    @Headers("Content-Type: application/json")
    @POST("register")   //to fetch data from url
     Call<JsonObject> register(@Body Registrationinfo body);

    @Headers("Content-Type: application/json")
    @POST("login")
    Call<JsonObject> login(@Body Registrationinfo body);
}

