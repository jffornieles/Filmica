package io.keepcoding.filmica.view.detail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.keepcoding.filmica.R


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (savedInstanceState == null) {
            val fragment = DetailFragment()
            val id = intent.getStringExtra("id")
            val active = intent.getStringExtra("active")

            val args = Bundle()
            args.putString("id", id)
            args.putString("active", active)

            fragment.arguments = args

            supportFragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit()

        }
    }
}
