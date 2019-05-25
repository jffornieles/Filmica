package io.keepcoding.filmica.view.films

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import io.keepcoding.filmica.R
import io.keepcoding.filmica.data.*
import io.keepcoding.filmica.view.detail.DetailActivity
import io.keepcoding.filmica.view.detail.DetailFragment
import io.keepcoding.filmica.view.detail.DetailPlaceholderFragment
import io.keepcoding.filmica.view.search.SearchFragment
import io.keepcoding.filmica.view.trending.TrendsFragment
import io.keepcoding.filmica.view.util.OnClickLister
import io.keepcoding.filmica.view.watchlist.WatchlistFragment
import kotlinx.android.synthetic.main.activity_films.*



class FilmsActivity : AppCompatActivity(), OnClickLister {

    private lateinit var filmsFragment: FilmsFragment
    private lateinit var watchlistFragment: WatchlistFragment
    private lateinit var activeFragment: Fragment
    private lateinit var trendingFragment: TrendsFragment
    private lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films)

        if (savedInstanceState == null) {
            setupFragments()
        } else {
            val tag = savedInstanceState.getString("active", TAG_FILM)
            restoreFragments(tag)
        }

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_discover -> showMainFragment(filmsFragment)
                R.id.action_watchlist -> showMainFragment(watchlistFragment)
                R.id.action_trending -> showMainFragment(trendingFragment)
                R.id.action_search -> showMainFragment(searchFragment)
            }

            true
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("active", activeFragment.tag)
    }

    private fun setupFragments() {
        filmsFragment = FilmsFragment()
        watchlistFragment = WatchlistFragment()
        trendingFragment = TrendsFragment()
        searchFragment = SearchFragment()
        activeFragment = filmsFragment

        supportFragmentManager.beginTransaction()
            .add(R.id.container, filmsFragment, TAG_FILM)
            .add(R.id.container, watchlistFragment, TAG_WATCHLIST)
            .add(R.id.container, searchFragment, TAG_SEARCH)
            .add(R.id.container, trendingFragment, TAG_TRENDING)
            .hide(trendingFragment)
            .hide(watchlistFragment)
            .hide(searchFragment)
            .commit()

        putPlaceholder()
    }

    private fun restoreFragments(tag: String) {
        filmsFragment = supportFragmentManager.findFragmentByTag(TAG_FILM) as FilmsFragment
        watchlistFragment = supportFragmentManager.findFragmentByTag(TAG_WATCHLIST) as WatchlistFragment
        trendingFragment = supportFragmentManager.findFragmentByTag(TAG_TRENDING) as TrendsFragment
        searchFragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH) as SearchFragment

        activeFragment =
            when (tag) {
                TAG_WATCHLIST -> watchlistFragment
                TAG_TRENDING -> trendingFragment
                TAG_SEARCH -> searchFragment
                else -> {
                    filmsFragment
                }
            }

    }

    private fun showMainFragment(fragment: Fragment) {

        if (fragment.tag == "watchlist") {
            supportFragmentManager.beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit()
        }

        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()

        activeFragment = fragment

        putPlaceholder()
    }

    override fun onClick(film: Film) {
        if (!isDetailDetailViewAvailable()) {

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", film.id)
            intent.putExtra("active", activeFragment.tag)
            startActivity(intent)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container_detail,
                    DetailFragment.newInstance(film.id, activeFragment.tag!!)
                )
                .commit()
        }
    }

    private fun putPlaceholder() {
        if (isDetailDetailViewAvailable()) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container_detail,
                    DetailPlaceholderFragment()
                )
                .commit()
        }
    }

    private fun isDetailDetailViewAvailable() =
        findViewById<FrameLayout>(R.id.container_detail) != null

}