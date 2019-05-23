package com.keepcoding.filmica.view.search

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
import kotlinx.android.synthetic.main.item_search.view.*

class SearchAdapter(clickListener: ((Film) -> Unit)? = null) :
    BaseFilmAdapter<SearchAdapter.SearchViewHolder>(
        R.layout.item_search,
        { view -> SearchViewHolder(view, clickListener) }
    ) {


    class SearchViewHolder(
        view: View,
        clickListener: ((Film) -> Unit)? = null
    ) : BaseFilmHolder(view, clickListener) {

        override fun bindFilm(film: Film) {
            super.bindFilm(film)

            with(itemView) {
                labelTitle.text = film.title
                titleGenre.text = film.genre
                labelVotes.text = film.rating.toString()
                loadImage(film)
            }
        }

        private fun loadImage(it: Film) {
            val target = SimpleTarget { bitmap: Bitmap ->
                itemView.imgPoster.setImageBitmap(bitmap)
                setColorFrom(bitmap)
            }

            itemView.imgPoster.tag = target

            Picasso.with(itemView.context)
                .load(it.getPosterUrl())
                .error(R.drawable.placeholder)
                .into(target)
        }

        private fun setColorFrom(bitmap: Bitmap) {
            Palette.from(bitmap).generate { palette ->
                val defaultColor = ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                val swatch = palette?.vibrantSwatch ?: palette?.dominantSwatch
                val color = swatch?.rgb ?: defaultColor

                itemView.container.setBackgroundColor(color)
                itemView.containerData.setBackgroundColor(color)
            }
        }
    }
}