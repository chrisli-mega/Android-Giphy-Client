package gregpearce.gifhub.api

import gregpearce.gifhub.api.model.GiphySearchResponse
import gregpearce.gifhub.api.model.GiphySingleSearchResponse
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable

interface  GiphyGifsApi {
    @GET("gifs")
    fun getGifsByIDs(
            @Query("api_key") apiKey: String,
            @Query("ids") ids: String) : Observable<GiphySearchResponse>

    @GET("gifs/{gif_id}")
    fun getGifByID(
            @Path("gif_id") gif_id: String,
            @Query("api_key") apiKey: String,
            @Query("gif_id") id: String) : Call<GiphySingleSearchResponse>
}