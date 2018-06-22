package com.hercat.mevur.vrcity.service;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCall<T> {
    public void request(Call<T> call, final RequestListener listener, final String identity) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                System.out.println(response.toString());
                try {
                    if (response.body() instanceof ResponseBody) {
                        listener.success(((ResponseBody) response.body()).string(), response.code(), identity);
                    } else {
                        listener.success(response.toString(), response.code(), identity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.error(e.getMessage(), response.code(), identity);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                listener.error(t.getMessage(), t.hashCode(), identity);
            }
        });
    }
}

