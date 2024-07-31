package com.a2b2.plog;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KakaoApiService {
    @GET("v2/local/search/address.json")
    Call<AddressResponse> getCoordinates(
            @Query("query") String address,
            @Query("key") String apiKey
    );
}
