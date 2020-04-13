package gregpearce.gifhub.ui.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import gregpearce.gifhub.R
import gregpearce.gifhub.api.GiphyApi
import gregpearce.gifhub.api.GiphyGifsApi

import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.content_start.*
import org.jetbrains.anko.onClick

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        setSupportActionBar(toolbar)

        testListedResponse.onClick {
            startActivity(Intent(this, MainActivity::class.java))
        }

        testGetGifById.onClick {
            startActivity(Intent(this, TestGetGifByIDActivity::class.java))
        }

        testTranslate.onClick {
            startActivity(Intent(this, TestTranslateActivity::class.java))
        }

        testRandom.onClick {
            startActivity(Intent(this, TestRandomActivity::class.java))
        }


    }

}
