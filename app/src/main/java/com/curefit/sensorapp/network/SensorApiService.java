package com.curefit.sensorapp.network;

import com.curefit.sensorapp.data.PayLoadJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by rahul on 06/09/17.
 */

public interface SensorApiService {
    @POST("postlink2/api/users")
    Call<Object> postData(@Body PayLoadJson result);
}
