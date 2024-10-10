package com.a2b2.plog;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/upload") // 서버의 업로드 엔드포인트 경로
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);
}
