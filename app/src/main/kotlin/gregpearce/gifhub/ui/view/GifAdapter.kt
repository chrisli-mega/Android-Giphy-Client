package gregpearce.gifhub.ui.view

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import gregpearce.gifhub.app.GIPHY_PAGE_SIZE
import gregpearce.gifhub.ui.model.Gif
import gregpearce.gifhub.ui.presenter.SearchPresenter
import gregpearce.gifhub.ui.util.InstanceStateManager
import gregpearce.gifhub.util.rx.applyDefaults
import rx.Observable
import timber.log.Timber
import javax.inject.Inject

class GifAdapter(val activity: BaseActivity, val type: String) : RecyclerView.Adapter<GifViewHolder>() {

    @Inject lateinit var presenter: SearchPresenter
    @Inject lateinit var instanceStateManager: InstanceStateManager

    var count = 0

    init {
        activity.viewComponent.inject(this)

        configureInstanceState()

        subscribeView()
    }

    private fun configureInstanceState() {
        val keyBase = "GifAdapter_"

        val keyCount = keyBase + "Count"
        count = instanceStateManager.savedState.getInt(keyCount, 0)

        instanceStateManager.registerSaveLambda {
            it.putInt(keyCount, count)
        }
    }

    fun subscribeView() {
        presenter.getResults()
                .applyDefaults()
                .subscribe ({
                    updateCount(it.totalCount)
                }, {
                    updateCount(0)
                })
    }

    private fun updateCount(count: Int) {
        Timber.d("Updating count to: $count")
        this.count = count
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GifViewHolder? {
        return GifViewHolder(GifView(parent!!.context))
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun onBindViewHolder(holder: GifViewHolder?, position: Int) {
        holder!!.setModel(getGifModel(position))
    }

    private fun getGifModel(position: Int): Observable<Gif> {
        // figure out which page to get
        var pageIndex = position / GIPHY_PAGE_SIZE
        var pagePosition = position - pageIndex * GIPHY_PAGE_SIZE
        Timber.d("Position: $position, Page: $pageIndex, Page Position: $pagePosition")

        // get the page
        val page = presenter.getResultPage(pageIndex)

        // filter to get the requested gif element within the page
        return page.map { it.gifs.elementAt(pagePosition) }
    }
}