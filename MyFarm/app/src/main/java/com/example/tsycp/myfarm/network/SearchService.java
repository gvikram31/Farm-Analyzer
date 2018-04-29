package com.example.tsycp.myfarm.network;

import android.content.Context;

import com.example.tsycp.myfarm.network.config.RetrofitBuilder;
import com.example.tsycp.myfarm.network.interfaces.RegisterInterface;
import com.example.tsycp.myfarm.network.interfaces.SearchInterface;

import retrofit2.Callback;

public class SearchService {

    private SearchInterface searchInterface;

    public SearchService(Context context) {
        searchInterface = RetrofitBuilder.builder(context)
                .create(SearchInterface.class);
    }

    public void doSearch(String latitude, String longitude, String zone, String timestamp, Callback callback) {
        searchInterface.search(latitude, longitude, zone, timestamp).enqueue(callback);
    }

}
