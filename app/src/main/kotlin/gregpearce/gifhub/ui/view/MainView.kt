package gregpearce.gifhub.ui.view

import android.content.Context
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding.widget.textChanges
import gregpearce.gifhub.ui.presenter.SearchPresenter
import gregpearce.gifhub.ui.util.InstanceStateManager
import gregpearce.gifhub.util.rx.applyDefaults
import gregpearce.gifhub.util.rx.indexItems
import gregpearce.gifhub.util.rx.timberd
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainView : LinearLayout {
    @Inject lateinit var activity: BaseActivity
    @Inject lateinit var searchPresenter: SearchPresenter
    @Inject lateinit var instanceStateManager: InstanceStateManager

    lateinit var searchEditText: EditText
    lateinit var resultsCountTextView: TextView
    lateinit var resultsRecyclerView: RecyclerView
    val gifAdapter: GifAdapter

    init {
        var activity = context as BaseActivity
        activity.viewComponent.inject(this)
        gifAdapter = GifAdapter(activity, "multiple")

        initView()
        configureInstanceState()

        // subscribe the presenter to input from this view
        subscribePresenter()
        // subscribe this view to output from the presenter
        subscribeView()
    }

    private fun subscribePresenter() {
        val query = searchEditText.textChanges()
                // subscribe to the text changes on the UI thread
                .subscribeOn(AndroidSchedulers.mainThread())
                // convert to a string
                .map { it.toString() }
                // wait for 500ms pause between typing characters to prevent spamming the network on every character
                .debounce(500, TimeUnit.MILLISECONDS)
                // filter out duplicates
                .distinctUntilChanged()
                .timberd { "Sending search term to presenter: $it" }

        searchPresenter.subscribe(query)
    }

    private fun subscribeView() {
        searchPresenter.getResults()
                .indexItems()
                .applyDefaults()
                .subscribe({
                    // reset the scroll position for each new set of search resets
                    // except the first set, which could be after a config change
                    if (it.index > 0) resetScrollPosition()
                    showResultsCount(it.item.totalCount)
                }, {
                    Timber.e(it, it.message)
                })
    }

    /**
     * Contains the code for saving and restoring instance state.
     */
    private fun configureInstanceState() {
        val keyBase = "MainView_"

        val keyQuery = keyBase + "Query"
        val query = instanceStateManager.savedState.getString(keyQuery, "")
        searchEditText.text.append(query)

        val keyLayoutState = keyBase + "LayoutState"
        val layoutState = instanceStateManager.savedState.getParcelable<Parcelable>(keyLayoutState)
        resultsRecyclerView.layoutManager.onRestoreInstanceState(layoutState)

        instanceStateManager.registerSaveLambda {
            it.putString(keyQuery, searchEditText.text.toString())
            it.putParcelable(keyLayoutState, resultsRecyclerView.layoutManager.onSaveInstanceState())
        }
    }

    private fun initView() = AnkoContext.createDelegate(this).apply {
        orientation = VERTICAL

        searchEditText = editText { }

        resultsCountTextView = textView {
            padding = dip(5)
            textSize = 15f
        }

        resultsRecyclerView = recyclerView {
            adapter = gifAdapter
            layoutManager = GridLayoutManager(activity, 2)
        }

        val customStyle = { v: Any ->
            when (v) {
                is Button -> v.textSize = 26f
                is EditText -> v.textSize = 24f
            }
        }
        style(customStyle)
    }

    private fun showResultsCount(count: Int) {
        if (count == 0) {
            resultsCountTextView.text = "No results found"
        } else if (count == 1) {
            resultsCountTextView.text = "1 result found"
        } else {
            resultsCountTextView.text = "$count results found"
        }
    }

    private fun resetScrollPosition() {
        Timber.d("Reseting scroll position")
        resultsRecyclerView.layoutManager.scrollToPosition(0)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.mainView() = mainView {}

inline fun ViewManager.mainView(init: MainView.() -> Unit) = ankoView({ MainView(it) }, init)