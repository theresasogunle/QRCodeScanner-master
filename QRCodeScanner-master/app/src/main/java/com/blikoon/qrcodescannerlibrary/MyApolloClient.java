package com.blikoon.qrcodescannerlibrary;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by MUKHTER on 12/04/2018.
 */

public class MyApolloClient {

    private static String BASE_URL="https://api.graph.cool/simple/v1/cjf8dwnpz58ze0139tl1yn95g";
    private static ApolloClient MyapolloClient;

    public static ApolloClient getApolloClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        MyapolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(client).build();
        return MyapolloClient;
    }
}
