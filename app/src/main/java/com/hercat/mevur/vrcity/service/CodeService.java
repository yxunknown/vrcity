package com.hercat.mevur.vrcity.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CodeService {

    @GET("/geocoder/v2/")
    Call<ResponseBody> getPoint(@Query("address") String address,
                                @Query("ak") String ak,
                                @Query("output") String output);

}
