package com.example.cc.netphone;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyJson {

    public static void receiveHttpWithJson(final String url, okhttp3.Callback callback) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(callback);
        }
}