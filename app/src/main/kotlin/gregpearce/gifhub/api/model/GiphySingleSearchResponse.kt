package gregpearce.gifhub.api.model

import com.google.gson.annotations.SerializedName

/**
 * Used by GSON to parse the Giphy Response.
 */
data class GiphySingleSearchResponse(val data: GiphyData, val meta: Meta) {
    data class GiphyData(val id: String, val images: ImageFormats)

    data class ImageFormats(@SerializedName("fixed_width_small") val fixedWidthSmall: GiphyImage,
                            @SerializedName("fixed_width") val fixedWidth: GiphyImage,
                            @SerializedName("fixed_width_still") val fixedWidthStill: GiphyImage)

    data class GiphyImage(val url: String, val webp: String, val width: Int, val height: Int)

    data class Meta(val status: Int)
}


