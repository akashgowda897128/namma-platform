package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import java.util.List;

public interface ApiService {
    @GET("getTrainStatus")
    Call<List<TrainModel>> getTrains(
        @Header("x-rapidapi-key") String apiKey,
        @Header("x-rapidapi-host") String host,
        @Query("stationCode") String stationCode
    );
}
