package io.keepcoding.filmica.data

import android.arch.persistence.room.*

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: Film)

    @Query("SELECT * FROM film")
    fun getFilms(): List<Film>

    @Update
    fun updateFilm(film: Film)

    @Delete
    fun deleteFilm(film: Film)
}