package gregpearce.gifhub.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite
import dagger.Module
import dagger.Provides
import gregpearce.gifhub.R
import gregpearce.gifhub.api.GiphyApi
import gregpearce.gifhub.api.GiphyGifsApi
import gregpearce.gifhub.app.GIFS
import gregpearce.gifhub.app.GIPHY_API_URL
import gregpearce.gifhub.app.STICKERS
import gregpearce.gifhub.db.DbOpenHelper
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import rx.schedulers.Schedulers
import javax.inject.Singleton

/**
 * A module for defining Application scope dependencies.
 * These are usually dependencies that live in memory for the duration of the app's lifetime (Singletons).
 */
@Module
class ApplicationModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideGiphyApi(): GiphyApi {
        val preference = application.getSharedPreferences(application.getString(R.string.URL_PREFERENCE), MODE_PRIVATE)
        var url = preference.getString(application.getString(R.string.URL_CONFIG), GIPHY_API_URL)
        val stickersOrGifs = preference.getString(application.getString(R.string.STICKERS_OR_GIFS_CONFIG), GIFS)
        url += stickersOrGifs
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(GiphyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGiphyGifsApi(): GiphyGifsApi {
        var url = GIPHY_API_URL
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(GiphyGifsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBriteDatabase(context: Context): BriteDatabase {
        val sqlBrite = SqlBrite.create();
        val openHelper = DbOpenHelper(context)
        val db = sqlBrite.wrapDatabaseHelper(openHelper, Schedulers.io());
        return db
    }
}