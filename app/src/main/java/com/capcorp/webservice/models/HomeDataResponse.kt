package com.capcorp.webservice.models


data class HomeDataResponse(
    val data: HomeData
)

data class HomeData(
    val store_cards: List<StoreCards>?,
    val header: Headers?,
    val video: Video?,
    val featured_links: FeatureLinks?
)

data class FeatureLinks(
    val title_esp: String?,
    val title_en: String?,
    val esp: FeatureLinksData?,
    val eng: FeatureLinksData?

)

data class Video(
    val video_thumbnail: String?,
    val esp: VideoURL?,
    val eng: VideoURL?
)

data class FeatureLinksData(
    val link_title: String?,
    val link_subtitle: VideoURL?,
    val link_url: VideoURL?,
    val order_number: VideoURL?
)

data class Headers(
    val esp: Title?,
    val eng: Title?
)

data class Title(
    val title: String?
)

data class VideoURL(
    val video_url: String?,
    val video_title: String?
)

data class StoreCards(
    val nameEsp: String?,
    val nameEn: String?,
    val store: List<Store>?
)

data class Store(
    val order_number: String?,
    val store_image: String?,
    val store_link: String?,
    val store_title: String?
)