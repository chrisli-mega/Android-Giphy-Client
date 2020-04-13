package gregpearce.gifhub.ui.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import gregpearce.gifhub.R
import gregpearce.gifhub.api.GiphyGifsApi
import gregpearce.gifhub.api.model.GiphySingleSearchResponse
import gregpearce.gifhub.app.GIPHY_API_KEY
import gregpearce.gifhub.app.GIPHY_API_URL

import kotlinx.android.synthetic.main.activity_test_get_gif_by_id.*
import kotlinx.android.synthetic.main.content_test_get_gif_by_id.*
import retrofit.*
import javax.inject.Inject

class TestGetGifByIDActivity : BaseActivity() {

    lateinit var giphyGifsApi: GiphyGifsApi

    lateinit var activity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_get_gif_by_id)
        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        activity = this
        var url = GIPHY_API_URL
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        giphyGifsApi = retrofit.create(GiphyGifsApi::class.java)
        val call = giphyGifsApi.getGifByID("xT4uQulxzV39haRFjG", GIPHY_API_KEY, "xT4uQulxzV39haRFjG")
        call.enqueue(object : Callback<GiphySingleSearchResponse> {
            override fun onFailure(t: Throwable?) {

            }

            override fun onResponse(response: Response<GiphySingleSearchResponse>?, retrofit: Retrofit?) {
                if (response?.code() == 200) {
                    Glide.clear(testImageView)
                    val giphySingleSearchResponse = response.body()
                    Log.d("dfdf", giphySingleSearchResponse.data.images.fixedWidthSmall.url)
                    Glide.with(activity).load(giphySingleSearchResponse.data.images.fixedWidthSmall.url).asGif().crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(testImageView)
                }
            }
        })
    }
}
