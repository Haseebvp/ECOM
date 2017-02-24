package network;

import android.database.Observable;

import models.AlbumData;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface ApiInterface {
    @GET
    rx.Observable<AlbumData> getData(@Url String url);
}
