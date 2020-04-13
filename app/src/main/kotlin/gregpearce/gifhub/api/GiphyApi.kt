package gregpearce.gifhub.api

import gregpearce.gifhub.api.model.GiphySearchResponse
import gregpearce.gifhub.api.model.GiphySingleSearchResponse
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

/**
 * http://api.giphy.com/v1/gifs
 */
interface  GiphyApi {
    @GET("search")
    fun search(@Query("api_key") apiKey: String,
               @Query("q") query: String,
               @Query("offset") offset: Int,
               @Query("limit") limit: Int) : Observable<GiphySearchResponse>

    @GET("trending")
    fun trending(@Query("api_key") apiKey: String,
                 @Query("offset") offset: Int,
                 @Query("limit") limit: Int) : Observable<GiphySearchResponse>

    @GET("translate")
    fun translate(@Query("api_key") apiKey: String,
                  @Query("s") searchTerm: String,
                  @Query("weirdness") weiredness: Int): Call<GiphySingleSearchResponse>

    @GET("random")
    fun random(@Query("api_key") apiKey: String,
               @Query("tag") searchTerm: String,
               @Query("rating") rating: String
    ) : Call<GiphySingleSearchResponse>
}