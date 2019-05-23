package io.keepcoding.filmica.data

import android.arch.persistence.room.Room
import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object FilmsRepo {

    private val films: MutableList<Film> = mutableListOf()

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

    fun findFilmById(id: String): Film? {
        return films.find {
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
        onResponse: (List<Film>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = ApiRoutes.discoverMoviesUrl()
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val films =
                    Film.parseFilms(response.getJSONArray("results"))
                FilmsRepo.films.clear()
                FilmsRepo.films.addAll(films)
                onResponse.invoke(FilmsRepo.films)
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
        onResponse: (List<Film>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = ApiRoutes.trendingMoviesUrl()
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val films =
                    Film.parseFilms(response.getJSONArray("results"))
                FilmsRepo.films.clear()
                FilmsRepo.films.addAll(films)
                onResponse.invoke(FilmsRepo.films)
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
        callbackSuccess: ((List<Film>) -> Unit),
        callbackError: ((VolleyError) -> Unit)
    ) {
        if (films.isEmpty()) {
            requestSearchFilms(callbackSuccess, callbackError, context, query)
        } else {
            callbackSuccess.invoke(films)
        }
    }

    private fun requestSearchFilms(
        callbackSuccess: (List<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context,
        query: String
    ) {
        val url = ApiRoutes.searchUrl(query)
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val newFilms = Film.parseFilms(response.getJSONArray("results"))
                addNewFilm(newFilms)
                callbackSuccess.invoke(newFilms)
            },
            { error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

    fun addNewFilm(newFilms: List<Film>) {
        newFilms.map {film ->
            if (!films.contains(film)) {
                films.add(film)
            }
        }
    }


 /*   fun searchFilms(
        context: Context,
        query: String,
        onResponse: (List<Film>) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val url = ApiRoutes.searchUrl(query)

        if (films.isEmpty()) {

            val request = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    val films = Film.parseFilms(response.getJSONArray("results"))
                    FilmsRepo.films.clear()
                    FilmsRepo.films.addAll(films)
                    onResponse.invoke(FilmsRepo.films)
                },
                { error ->
                    error.printStackTrace()
                    onError.invoke(error)
                })

            Volley.newRequestQueue(context)
                .add(request)
        } else {
            onResponse.invoke(films)
        }

    }*/



    private fun dummyFilms(): MutableList<Film> {
        return (1..10).map { i: Int ->
            return@map Film(
                id = "${i}",
                title = "Film ${i}",
                overview = "Overview ${i}",
                genre = "Genre ${i}",
                rating = i.toFloat(),
                date = "2019-05-${i}"
            )
        }.toMutableList()
    }
}