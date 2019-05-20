package io.keepcoding.filmica.view.films

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.keepcoding.filmica.R
import io.keepcoding.filmica.data.Film
import io.keepcoding.filmica.data.FilmsRepo
import io.keepcoding.filmica.view.util.GridOffsetDecoration
import kotlinx.android.synthetic.main.fragment_films.*
import kotlinx.android.synthetic.main.layout_error.*

class FilmsFragment : Fragment() {

    lateinit var listener: OnFilmClickLister

    val list: RecyclerView by lazy {
        listFilms.addItemDecoration(GridOffsetDecoration())
        return@lazy listFilms
    }

    val adapter = FilmsAdapter {
        listener.onClick(it)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnFilmClickLister) {
            listener = context
        } else {
            throw IllegalArgumentException("The attached activity isn't implementing ${OnFilmClickLister::class.java.canonicalName}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_films, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter
        buttonRetry.setOnClickListener { reload() }
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload() {
        showProgress()

        FilmsRepo.discoverFilms(context!!,
            { films ->
                adapter.setFilms(films)
                showList()

            }, { errorRequest ->
                showError()
            })
    }

    private fun showList() {
        filmsProgress.visibility = View.INVISIBLE
        error.visibility = View.INVISIBLE
        list.visibility = View.VISIBLE
    }

    private fun showError() {
        filmsProgress.visibility = View.INVISIBLE
        list.visibility = View.INVISIBLE
        error.visibility = View.VISIBLE
    }

    private fun showProgress() {
        filmsProgress.visibility = View.VISIBLE
        error.visibility = View.INVISIBLE
        list.visibility = View.INVISIBLE
    }


    interface OnFilmClickLister {
        fun onClick(film: Film)
    }

}