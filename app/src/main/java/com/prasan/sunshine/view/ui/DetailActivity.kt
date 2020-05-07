package com.prasan.sunshine.view.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.prasan.sunshine.R
import com.prasan.sunshine.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private val FORECAST_SHARE_HASHTAG = " #SunshineApp"

    private lateinit var  binding:ActivityDetailBinding
    var weatherStringText:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        weatherStringText = intent.getStringExtra(Intent.EXTRA_TEXT)
        binding.tvDetailWeatherText.text = weatherStringText

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_share ->{
                shareCompatBuilder(weatherStringText)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    fun shareCompatBuilder(weatherString:String?){
        val contentType = "text/plain"
        val title = "Share Weather"

        ShareCompat.IntentBuilder.from(this)
            .setType(contentType)
            .setChooserTitle(title)
            .setText(weatherString)
            .startChooser()
    }

}
