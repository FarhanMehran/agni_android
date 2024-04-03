package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TopicSubmissions {
    @SerializedName("wallpapers")
    @Expose
    var wallpapers: Wallpapers? = null

    @SerializedName("nature")
    @Expose
    var nature: Nature? = null

    @SerializedName("textures-patterns")
    @Expose
    var texturesPatterns: TexturesPatterns? = null

    @SerializedName("color-theory")
    @Expose
    var colorTheory: ColorTheory? = null

    @SerializedName("experimental")
    @Expose
    var experimental: Experimental? = null

    @SerializedName("originalbydesign")
    @Expose
    var originalbydesign: Originalbydesign? = null

    @SerializedName("technology")
    @Expose
    var technology: Technology? = null

    @SerializedName("summer-on-film")
    @Expose
    var summerOnFilm: SummerOnFilm? = null
}