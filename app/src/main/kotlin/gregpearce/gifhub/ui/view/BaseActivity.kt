package gregpearce.gifhub.ui.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import gregpearce.gifhub.R
import gregpearce.gifhub.app.*
import gregpearce.gifhub.di.DaggerViewComponent
import gregpearce.gifhub.di.ViewComponent
import gregpearce.gifhub.di.ViewModule
import gregpearce.gifhub.ui.util.InstanceStateManager
import javax.inject.Inject


/**
 * A base class that contains logic common to all activities, such as the Retained Fragment that manages Presenters.
 * It also exposes a common interface to all views for DI.
 */
abstract class BaseActivity : AppCompatActivity() {
    lateinit var viewComponent: ViewComponent

    @Inject
    lateinit var instanceStateManager: InstanceStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModule = ViewModule(this, savedInstanceState ?: Bundle.EMPTY)

        viewComponent = DaggerViewComponent.builder()
                .applicationComponent(MainApplication.graph)
                .viewModule(viewModule)
                .build()

        viewComponent.inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        instanceStateManager.runSaveLambdas(outState!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.change_url_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.change_url -> {
                val baseURL = this.getSharedPreferences(getString(R.string.URL_PREFERENCE), Context.MODE_PRIVATE).getString(getString(R.string.URL_CONFIG), GIPHY_API_URL)
                val baseURLOpposite = if (baseURL == GIPHY_API_URL) MEGA_API_URL else GIPHY_API_URL
                AlertDialog.Builder(this)
                        .setTitle(R.string.change_url)
                        .setMessage("Convert $baseURL to $baseURLOpposite?")
                        .setPositiveButton(R.string.OK_BUTTON) { _, _ ->
                            this.getSharedPreferences(getString(R.string.URL_PREFERENCE), Context.MODE_PRIVATE).edit()
                                    .putString(getString(R.string.URL_CONFIG), baseURLOpposite).apply()
                        }
                        .setNegativeButton(R.string.DISMISS_BUTTON) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }
            R.id.convert_stickers_gifs -> {
                val config = this.getSharedPreferences(getString(R.string.URL_PREFERENCE), Context.MODE_PRIVATE).getString(getString(R.string.STICKERS_OR_GIFS_CONFIG), GIFS)
                val configOpposite = if (config == GIFS) STICKERS else GIFS
                AlertDialog.Builder(this)
                        .setTitle(R.string.convert_stickers_and_gifs)
                        .setMessage("Convert $config to $configOpposite?")
                        .setPositiveButton(R.string.OK_BUTTON) { _, _ ->
                            this.getSharedPreferences(getString(R.string.URL_PREFERENCE), Context.MODE_PRIVATE).edit()
                                    .putString(getString(R.string.STICKERS_OR_GIFS_CONFIG), configOpposite).apply()
                        }
                        .setNegativeButton(R.string.DISMISS_BUTTON) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}