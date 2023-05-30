package com.neko.hiepdph.cattranslate.ui.main

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.neko.hiepdph.cattranslate.common.AppSharePreference
import com.neko.hiepdph.cattranslate.common.changeStatusBarColor
import com.neko.hiepdph.cattranslate.common.createContext
import com.neko.hiepdph.cattranslate.databinding.ActivityMainBinding
import com.neko.hiepdph.dogtranslatorlofi.viewmodel.AppViewModel
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<AppViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor()
        initPlayer()
        hideNavigationBar()
    }

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(
        newBase.createContext(
            Locale(
                AppSharePreference.INSTANCE.getSavedLanguage(
                    Locale.getDefault().language
                )
            )
        )
    )


    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }


    private fun hideNavigationBar() {
        val decorView: View = window.decorView
        val uiOptions: Int =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }

    private fun initPlayer() {
        val player = ExoPlayer.Builder(this).build()

        viewModel.setupPlayer(player)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetAll()
    }
}