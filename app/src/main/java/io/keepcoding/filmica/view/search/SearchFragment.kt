package io.keepcoding.filmica.view.search


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.keepcoding.filmica.view.search.SearchAdapter

import io.keepcoding.filmica.R
import io.keepcoding.filmica.data.FilmsRepo
import io.keepcoding.filmica.view.util.GridOffsetDecoration
import io.keepcoding.filmica.view.util.OnClickLister
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_noresult.*

const val MIN_SEARCH_QUERY = 3

class SearchFragment : Fragment() {

    lateinit var listener: OnClickLister
    private var searching: Boolean = false

    val list: RecyclerView by lazy {
        val instance = view!!.findViewById<RecyclerView>(R.id.list_search)
        instance.addItemDecoration(GridOffsetDecoration())
        instance.setHasFixedSize(true)
        return@lazy instance
    }

    val adapter: SearchAdapter by lazy {
        val instance = SearchAdapter { film ->
            this.listener.onClick(film)
        }

        instance
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnClickLister) {
            listener = context
        } else {
            throw IllegalArgumentException("The attached activity isn't implementing ${OnClickLister::class.java.canonicalName}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter

        textSearch.addTextChangedListener(object: TextWatcher {

            override fun afterTextChanged(text: Editable?) {
                text?.let {
                    val query = text.toString()
                    if (query.length > MIN_SEARCH_QUERY) {
                        searching = false
                        progress.visibility = View.VISIBLE
                        layoutError.visibility = View.INVISIBLE
                        layoutNoResults.visibility = View.INVISIBLE
                        list.visibility = View.INVISIBLE
                        search(query)
                    } else {
                        searching = true
                        progress.visibility = View.INVISIBLE
                        layoutError.visibility = View.INVISIBLE
                        layoutNoResults.visibility = View.INVISIBLE
                        list.visibility = View.INVISIBLE
                    }
                }
            }

            override fun beforeTextChanged(text: CharSequence?, start: Int, after: Int, count: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }

    fun search(query: String) {
        FilmsRepo.searchFilms(context!!, query, { films ->
            if (!searching && films.size > 0) {
                adapter.setFilms(films)
                showResults()
            } else if (films.size == 0) {
                showNoResults()
            }
        }, { error ->
            showError()
            error.printStackTrace()
        })
    }

    private fun showNoResults() {
        progress.visibility = View.INVISIBLE
        layoutError.visibility = View.INVISIBLE
        layoutNoResults.visibility = View.VISIBLE
        list.visibility = View.INVISIBLE
    }

    private fun showResults() {
        progress.visibility = View.INVISIBLE
        layoutError.visibility = View.INVISIBLE
        layoutNoResults.visibility = View.INVISIBLE
        list.visibility = View.VISIBLE
    }

    private fun showError() {
        progress.visibility = View.INVISIBLE
        layoutError.visibility = View.VISIBLE
        layoutNoResults.visibility = View.INVISIBLE
        list.visibility = View.INVISIBLE
    }

}
