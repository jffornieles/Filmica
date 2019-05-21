package io.keepcoding.filmica.view.watchlist


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.keepcoding.filmica.R
import io.keepcoding.filmica.data.Film
import io.keepcoding.filmica.data.FilmsRepo
import io.keepcoding.filmica.view.util.BaseFilmHolder
import io.keepcoding.filmica.view.util.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_watchlist.*

class WatchlistFragment : Fragment() {

    val adapter: WatchListAdapter = WatchListAdapter {
        showDetail(it)
    }

    private fun showDetail(film: Film) {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeHandler()
        watchlist.adapter = adapter
    }

    private fun setupSwipeHandler() {
        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
                val film = (holder as BaseFilmHolder).film
                val position = holder.adapterPosition
                deleteFilm(film, position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(watchlist)
    }

    private fun deleteFilm(film: Film, position: Int) {
        FilmsRepo.deleteFilm(context!!, film) {
            adapter.deleteFilm(position)
            Snackbar.make(view!!, "Film eliminado de watchlist", Snackbar.LENGTH_LONG)
                .setAction("UNDO", { undoDelete(film, position) })
                .show()
        }
    }

    private fun undoDelete(film: Film, position: Int) {
        FilmsRepo.saveFilm(context!!, film) {
            FilmsRepo.getFilms(context!!) {
                adapter.watchlist(film, position)
                Snackbar.make(view!!, "Film añadido al watchlist", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", {
                        deleteFilm(film, position)
                    }
                    ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        FilmsRepo.getFilms(context!!) {
            adapter.setFilms(it)
        }
    }


}
