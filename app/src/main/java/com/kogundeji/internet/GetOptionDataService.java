package com.kogundeji.internet;

import com.google.gson.JsonElement;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GetOptionDataService {

    @GET("quotes")
    Call<JsonElement> getOptionData(@QueryMap Map<String,String> parameters);
}
