package io.keepcoding.filmica.data

import android.net.Uri
import io.keepcoding.filmica.BuildConfig

object ApiRoutes {

    fun discoverMoviesUrl(
        language: String = "en-US",
        sort: String = "popularity.desc",
        page: Int = 1
    ): String {
        return getUriBuilder()
            .appendPath("discover")
            .appendPath("movie")
            .appendQueryParameter("language", language)
            .appendQueryParameter("sort_by", sort)
            .appendQueryParameter("page", page.toString())
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("include_video", "false")
            .build()
            .toString()
    }

    fun trendingMoviesUrl(
        page: Int = 1,
        language: String = "en-US"
    ): String {
        return getUriBuilder()
            .appendPath("trending")
            .appendPath("movie")
            .appendPath("week")
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("include_video", "false")
            .appendQueryParameter("language", language)
            .appendQueryParameter("page", page.toString())
            .build()
            .toString()
    }

    fun searchUrl(
        query: String,
        language: String = "en-US",
        page: Int = 1
    ): String {
        return getUriBuilder()
            .appendPath("search")
            .appendPath("movie")
            .appendQueryParameter("query", query)
            .appendQueryParameter("language", language)
            .appendQueryParameter("page", page.toString())
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("include_video", "false")
            .build()
            .toString()
    }

    private fun getUriBuilder(): Uri.Builder =
        Uri.Builder()
            .scheme("https")
            .authority("api.themoviedb.org")
            .appendPath("3")
            .appendQueryParameter("api_key", BuildConfig.MovieDbApiKey)
}