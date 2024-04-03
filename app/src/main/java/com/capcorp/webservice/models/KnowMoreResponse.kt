package com.capcorp.webservice.models

data class KnowMoreResponse(
    val data: Data,
    val message: String,
    val statusCode: Int
){

data class Data(
    val featured_links: FeaturedLinks
)

data class FeaturedLinks(
    val eng: List<Eng>,
    val esp: List<Esp>,
    val title_en: String,
    val title_esp: String
)

data class Eng(
    val link_subtitle: String,
    val link_title: String,
    val link_url: String,
    val order_number: Int
)

data class Esp(
    val link_subtitle: String,
    val link_title: String,
    val link_url: String,
    val order_number: Int
)
}