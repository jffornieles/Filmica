package io.keepcoding.filmica.view.trending

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
import io.keepcoding.filmica.view.films.FilmsAdapter
import io.keepcoding.filmica.view.films.FilmsFragment
import io.keepcoding.filmica.view.util.EndlessScrollListener
import io.keepcoding.filmica.view.util.GridOffsetDecoration
import io.keepcoding.filmica.view.util.OnClickLister
import kotlinx.android.synthetic.main.fragment_films.*
import kotlinx.android.synthetic.main.layout_error.*

class TrendsFragment : Fragment() {

    lateinit var listener: OnClickLister

    private var lastLoadPage: Int = 1
    private var totalPages: Int? =  null

    val list: RecyclerView by lazy {
        listFilms.addItemDecoration(GridOffsetDecoration())
        return@lazy listFilms
    }


    val adapter = FilmsAdapter {
        listener.onClick(it)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnClickLister) {
            listener = context
        } else {
            throw IllegalArgumentException("The attached activity isn't implementing " +
                    "${OnClickLister::class.java.canonicalName}")
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

        list.addOnScrollListener(object : EndlessScrollListener(list.layoutManager!!) {
            override fun onLoadMore(currentPage: Int, totalItemCount: Int) {
                if (totalItemCount > 1) {
                    lastLoadPage += 1
                    totalPages?.let {
                        if (it > lastLoadPage) filmsProgress.visibility = View.VISIBLE
                        if (it >= lastLoadPage) reload(lastLoadPage)
                    }
                }
            }

            override fun onScroll(firstVisibleItem: Int, dy: Int, scrollPosition: Int) { }
        })

        buttonRetry.setOnClickListener { reload() }
    }

    override fun onResume() {
        super.onResume()
        reload()
    }

    private fun reload(page: Int = 1) {
        showProgress()

        FilmsRepo.trendingFilms(
            context!!,
            page,
            { films, totalPages ->
                if (this.totalPages == null) this.totalPages = totalPages
                if (page == 1) adapter.setFilms(films) else adapter.updateFilms(films)
                showList()


            }, { errorRequest ->
                showError()
                errorRequest.printStackTrace()
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

}


