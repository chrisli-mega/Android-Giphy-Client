package gregpearce.gifhub.ui.view

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import gregpearce.gifhub.R
import gregpearce.gifhub.api.GiphyApi
import gregpearce.gifhub.api.model.GiphySingleSearchResponse
import gregpearce.gifhub.app.GIFS
import gregpearce.gifhub.app.GIPHY_API_KEY
import gregpearce.gifhub.app.GIPHY_API_URL

import kotlinx.android.synthetic.main.activity_test_random.*
import kotlinx.android.synthetic.main.activity_test_random.toolbar
import kotlinx.android.synthetic.main.activity_test_translate.*
import kotlinx.android.synthetic.main.content_test_random.*
import kotlinx.android.synthetic.main.content_test_translate.*
import org.jetbrains.anko.onClick
import retrofit.*

class TestRandomActivity : BaseActivity() {

    lateinit var giphyApi: GiphyApi
    lateinit var activity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        setContentView(R.layout.activity_test_random)
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

        buttonTestRandom.onClick {
            val tagWord = if (TextUtils.isEmpty(editTextTag.text.toString())) "Burrito" else editTextTag.text.toString()
            val rating = if (TextUtils.isEmpty(editTextRating.text.toString())) "g" else editTextRating.text.toString()

            val call = giphyApi.random(GIPHY_API_KEY, tagWord, rating)
            call.enqueue(object : Callback<GiphySingleSearchResponse> {
                override fun onFailure(t: Throwable?) {

                }

                override fun onResponse(response: Response<GiphySingleSearchResponse>?, retrofit: Retrofit?) {
                    if (response?.code() == 200) {
                        Glide.clear(imageViewTestRandom)
                        val giphySingleSearchResponse = response.body()
                        Glide.with(activity).load(giphySingleSearchResponse.data.images.fixedWidthSmall.url).asGif().crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(imageViewTestRandom)
                    }
                }
            })

        }
    }

}
