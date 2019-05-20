package io.keepcoding.filmica.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Entity
data class Film(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "poster_id") val posterId: String = "",
    val title: String = "No title",
    val genre: String = "No genre",
    val rating: Float = 0.0f,
    val overview: String = "No overview",
    val date: String = "1999-09-19"
) {
    @Ignore
    constructor(): this("")

    fun getPosterUrl(): String {
        return "https://image.tmdb.org/t/p/w500/${posterId}"
    }

    companion object {
        fun parseFilm(jsonFilm: JSONObject): Film {
            with(jsonFilm) {
                return Film(
                    id = getInt("id").toString(),
                    title = getString("title"),
                    overview = getString("overview"),
                    rating = getDouble("vote_average").toFloat(),
                    date = getString("release_date"),
                    posterId = optString("poster_path", ""),
                    genre = parseGenres(
                        jsonFilm.getJSONArray(
                            "genre_ids"
                        )
                    )
                )
            }
        }

        fun parseFilms(filmsArray: JSONArray): List<Film> {
            val films = mutableListOf<Film>()
            for (i in 0..(filmsArray.length() - 1)) {
                val film =
                    parseFilm(filmsArray.getJSONObject(i))
                films.add(film)
            }

            return films
        }

        private fun parseGenres(genresArray: JSONArray): String {
            val genres = mutableListOf<String>()

            for (i in 0..(genresArray.length() - 1)) {
                val genreId = genresArray.getInt(i)
                val genre = ApiConstants.genres[genreId] ?: ""
                genres.add(genre)
            }

            return genres.reduce { acc, genre ->  "$acc | $genre" }

        }
    }
}