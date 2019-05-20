package io.keepcoding.filmica.view.films

import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.View
import com.squareup.picasso.Picasso
import io.keepcoding.filmica.R
import io.keepcoding.filmica.data.Film
import io.keepcoding.filmica.view.util.BaseFilmAdapter
import io.keepcoding.filmica.view.util.BaseFilmHolder
import io.keepcoding.filmica.view.util.SimpleTarget
import kotlinx.android.synthetic.main.item_film.view.*

class FilmsAdapter(val listener: (Film) -> Unit) :
    BaseFilmAdapter<FilmsAdapter.FilmViewHolder>(
        layoutItem = R.layout.item_film,
        holderCreator = { view -> FilmViewHolder(view, listener) }
    ) {


    class FilmViewHolder(itemView: View, listener: (Film) -> Unit) : BaseFilmHolder(
        itemView,
        listener
    ) {

        override fun bindFilm(film: Film) {
            super.bindFilm(film)

            with(itemView) {
                labelTitle.text = film.title
                labelGenre.text = film.genre
                labelRating.text = film.rating.toString()

                loadImage(film)
            }
        }


        private fun loadImage(it: Film) {
            val target = SimpleTarget { bitmap: Bitmap ->
                itemView.imgPoster.setImageBitmap(bitmap)
                setColor(bitmap)
            }

            itemView.imgPoster.tag = target

            Picasso.with(itemView.context)
                .load(it.getPosterUrl())
                .error(R.drawable.placeholder)
                .into(target)
        }

        private fun setColor(bitmap: Bitmap) {
            Palette.from(bitmap).generate {
                val defaultColor =
                    ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                val swatch = it?.vibrantSwatch ?: it?.dominantSwatch
                val color = swatch?.rgb ?: defaultColor

                itemView.container.setBackgroundColor(color)
                itemView.containerData.setBackgroundColor(color)
            }
        }
    }
}