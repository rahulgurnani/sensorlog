package com.curefit.sensorsdk.network;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rahul on 06/09/17.
 */

public class SensorClient {
    private static Retrofit retrofit = null;
    private static SensorApiService sensorApiService;
    private static Retrofit getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://us-central1-sensorapp-4fb6f.cloudfunctions.net")      // TODO: move this out
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static synchronized SensorApiService getSensorService(Context context){
        if(sensorApiService == null){
            sensorApiService = getClient(context).create(SensorApiService.class);
        }
        return sensorApiService;
    }
}
