package gregpearce.gifhub.app

import android.text.TextUtils
import android.util.LruCache
import gregpearce.gifhub.api.GiphyApi
import gregpearce.gifhub.api.GiphyGifsApi
import gregpearce.gifhub.api.model.GiphySearchResponse
import gregpearce.gifhub.util.rx.assert
import gregpearce.gifhub.util.rx.timberd
import rx.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiphySearchCache @Inject constructor() {
    @Inject
    lateinit var giphyApi: GiphyApi

    @Inject
    lateinit var giphyGifsApi: GiphyGifsApi

    var cache = LruCache<Int, Observable<GiphySearchResponse>>(5)
    var lastSearch = ""

    fun getPage(search: String, pageIndex: Int): Observable<GiphySearchResponse> {
        if (lastSearch != search) {
            cache.evictAll()
            lastSearch = search
        }

        var page = cache.get(pageIndex)
        if (page == null) {
            page = fetchPage(search, pageIndex)
            cache.put(pageIndex, page)
            Timber.d("Page $pageIndex added to cache, cache size: ${cache.size()}")
        }

        return page
    }

    private fun fetchPage(search: String, pageIndex: Int): Observable<GiphySearchResponse> {
        Timber.d("Fetching page: $pageIndex")

        // calculate offset for this page
        val offset = (pageIndex + GIPHY_PAGE_START) * GIPHY_PAGE_SIZE

        if (search == "ids") {
            return giphyGifsApi.getGifsByIDs(GIPHY_API_KEY, "xT4uQulxzV39haRFjG, 3og0IPxMM0erATueVW,elxZwVNCcL5hvktNFs")
                    .timberd { "${it.data.size} gifs returned from search." }
                    // assert that the response code is valid
                    .assert({ it.meta.status == 200 },
                            { "Invalid Giphy API response status code: ${it.meta.status}" })
                    // retry 3 times before giving up
                    .retry(3)
                    // cache the result for reuse by later subscribers
                    .cache()
        } else if (TextUtils.isEmpty(search)) {
            return giphyApi.trending(GIPHY_API_KEY, offset, GIPHY_PAGE_SIZE)
                    .timberd { "${it.data.size} gifs returned from search." }
                    // assert that the response code is valid
                    .assert({ it.meta.status == 200 },
                            { "Invalid Giphy API response status code: ${it.meta.status}" })
                    // retry 3 times before giving up
                    .retry(3)
                    // cache the result for reuse by later subscribers
                    .cache()
        } else {
            return giphyApi.search(GIPHY_API_KEY, search, offset, GIPHY_PAGE_SIZE)
                    .timberd { "${it.data.size} gifs returned from search." }
                    // assert that the response code is valid
                    .assert({ it.meta.status == 200 },
                            { "Invalid Giphy API response status code: ${it.meta.status}" })
                    // retry 3 times before giving up
                    .retry(3)
                    // cache the result for reuse by later subscribers
                    .cache()
        }
    }
}