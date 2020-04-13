package gregpearce.gifhub.ui.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import gregpearce.gifhub.R
import gregpearce.gifhub.api.GiphyApi
import gregpearce.gifhub.api.model.GiphySingleSearchResponse
import gregpearce.gifhub.app.GIFS
import gregpearce.gifhub.app.GIPHY_API_KEY
import gregpearce.gifhub.app.GIPHY_API_URL

import kotlinx.android.synthetic.main.activity_test_translate.*
import kotlinx.android.synthetic.main.content_test_get_gif_by_id.*
import kotlinx.android.synthetic.main.content_test_translate.*
import org.jetbrains.anko.onClick
import retrofit.*

class TestTranslateActivity : BaseActivity() {

    lateinit var giphyApi: GiphyApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_translate)
        setSupportActionBar(toolbar)
        val preference = application.getSharedPreferences(application.getString(R.string.URL_PREFERENCE), Context.MODE_PRIVATE)
        var url = preference.getString(application.getString(R.string.URL_CONFIG), GIPHY_API_URL)
        val stickersOrGifs = preference.getString(application.getString(R.string.STICKERS_OR_GIFS_CONFIG), GIFS)
        url += stickersOrGifs
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        giphyApi = retrofit.create(GiphyApi::class.java)

        buttonTestTranslate.onClick {
            val translatedWord = if (TextUtils.isEmpty(editTextTranslated.text.toString())) "Ryan Gosling" else editTextTranslated.text.toString()
            val weired = if (TextUtils.isEmpty(editTextWeired.text.toString())) 10 else editTextWeired.text.toString().toInt()

            val call = giphyApi.translate(GIPHY_API_KEY, translatedWord, weired)
            call.enqueue(object : Callback<GiphySingleSearchResponse> {
                override fun onFailure(t: Throwable?) {

                }

                override fun onResponse(response: Response<GiphySingleSearchResponse>?, retrofit: Retrofit?) {
                    if (response?.code() == 200) {
                        Glide.clear(imageViewTestTranslate)
                        val giphySingleSearchResponse = response.body()
                        Glide.with(applicationContext).load(giphySingleSearchResponse.data.images.fixedWidthSmall.url).asGif().crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(imageViewTestTranslate)
                    }
                }
            })

        }
    }

}
