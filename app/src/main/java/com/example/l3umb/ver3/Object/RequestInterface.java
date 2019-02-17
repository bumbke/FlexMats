package com.example.l3umb.ver3.Object;

import com.example.l3umb.ver3.Function.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST(Constants.BASE_PATHNAME)
    Call<ServerResponse> operation(@Body ServerRequest request);
}
