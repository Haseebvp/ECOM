package network;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class ApiClient {
    public static String API_URL = "http://api.genmycert.com";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(final Context context) {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(rxAdapter)
                    .baseUrl(API_URL)
                    .build();
        }
        return retrofit;
    }
}