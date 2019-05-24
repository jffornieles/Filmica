package io.keepcoding.filmica.data

import android.arch.persistence.room.Room
import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.keepcoding.filmica.view.watchlist.WatchlistFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object FilmsRepo {

    private val films: MutableList<Film> = mutableListOf()
    private val trendsFilms: MutableList<Film> = mutableListOf()
    private val searchFilms: MutableList<Film> = mutableListOf()
    private var activeFragmentFilm: MutableList<Film> = mutableListOf()

    private var totalPagesTrendingFilms: Int = 0
    private var totalPagesDiscoverFilms: Int = 0

    @Volatile
    private var db: FilmDatabase? = null

    private fun getDbInstance(context: Context): FilmDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                FilmDatabase::class.java,
                "filmica-db"
            ).build()
        }

        return db as FilmDatabase
    }

    fun findFilmById(id: String, activeFragment: String): Film? {

        when (activeFragment) {
            TAG_WATCHLIST -> activeFragmentFilm = films
            TAG_TRENDING -> activeFragmentFilm = trendsFilms
            TAG_SEARCH -> activeFragmentFilm = searchFilms
            TAG_FILM -> activeFragmentFilm = films
        }

        return activeFragmentFilm.find {
            return@find it.id == id
        }


    }

    fun saveFilm(
        context: Context,
        film: Film,
        callback: (Film) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().insertFilm(film)
            }

            async.await()
            callback.invoke(film)
        }
    }

    fun getFilms(
        context: Context,
        callback: (List<Film>) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {

            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().getFilms()
            }

            val films = async.await()
            callback.invoke(films)
        }
    }

    fun deleteFilm(
        context: Context,
        film: Film,
        callback: (Film) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().deleteFilm(film)
            }

            async.await()
            callback.invoke(film)
        }
    }

    fun discoverFilms(
        context: Context,
        page: Int,
        onResponse: (List<Film>, totalPages: Int) -> Unit,
        onError: (VolleyError) -> Unit
    ) {

        if (films.isEmpty()) {
            requestDiscoverFilms(page, onResponse, onError, context)
        } else if (page > 1) {
            requestDiscoverFilms(page, onResponse, onError, context)
        } else {
            onResponse.invoke(films, totalPagesDiscoverFilms)
        }

        requestDiscoverFilms(page, onResponse, onError, context)
    }

    private fun requestDiscoverFilms(
        page: Int,
        onResponse: (List<Film>, totalPages: Int) -> Unit,
        onError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.discoverMoviesUrl(page = page)
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val films = Film.parseFilms(response.getJSONArray("results"))
                FilmsRepo.films.clear()
                FilmsRepo.films.addAll(films)
                totalPagesDiscoverFilms = response.optInt("total_pages", 0)
                onResponse.invoke(FilmsRepo.films, totalPagesDiscoverFilms)
            },
            { error ->
                error.printStackTrace()
                onError.invoke(error)
            }
        )

        Volley.newRequestQueue(context)
            .add(request)
    }

    fun trendingFilms(
        context: Context,
        page: Int,
        onResponse: (List<Film>, totalPages: Int) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        if (trendsFilms.isEmpty()) {
            requestTrendingFilms(page, onResponse, onError, context)
        } else if (page > 1) {
            requestTrendingFilms(page, onResponse, onError, context)
        } else {
            onResponse.invoke(trendsFilms, totalPagesTrendingFilms)
        }

        requestTrendingFilms(page, onResponse, onError, context)
    }

    private fun requestTrendingFilms(
        page: Int,
        onResponse: (List<Film>, totalPages: Int) -> Unit,
        onError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.trendingMoviesUrl(page = page)
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val films = Film.parseFilms(response.getJSONArray("results"))
                trendsFilms.clear()
                trendsFilms.addAll(films)
                totalPagesTrendingFilms = response.optInt("total_pages", 0)
                onResponse.invoke(trendsFilms, totalPagesTrendingFilms)
            },
            { error ->
                error.printStackTrace()
                onError.invoke(error)
            }
        )

        Volley.newRequestQueue(context)
            .add(request)
    }

    fun searchFilms(
        context: Context,
        query: String,
        onResponse: (List<Film>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = ApiRoutes.searchUrl(query)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val films = Film.parseFilms(response.getJSONArray("results"))
                FilmsRepo.searchFilms.clear()
                if (films.size > 0) {
                    (0..9).map {
                        FilmsRepo.searchFilms.add(films[it])
                    }
                }
                onResponse.invoke(searchFilms)
            },
            { error ->
                error.printStackTrace()
                onError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

}