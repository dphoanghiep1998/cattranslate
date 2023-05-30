package com.neko.hiepdph.cattranslate.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gianghv.libads.InterstitialPreloadAdManager
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.gianghv.libads.NativeAdsManager
import com.neko.hiepdph.cattranslate.BuildConfig
import com.neko.hiepdph.cattranslate.common.*
import com.neko.hiepdph.cattranslate.databinding.ActivitySplashBinding
import com.neko.hiepdph.cattranslate.ui.CustomApplication
import com.neko.hiepdph.cattranslate.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var interSplashAds: InterstitialPreloadAdManager? = null

    private var status = 0
    private val initDone = AppSharePreference.INSTANCE.getSetLangFirst(false)
    private var dialogLoadingInterAds: DialogFragmentLoadingInterAds? = null
    private lateinit var app: CustomApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as CustomApplication
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogLoadingInterAds = DialogFragmentLoadingInterAds()
        initAds()
        handleAds()
        changeStatusBarColor()
    }

    private fun initAds() {
        interSplashAds = InterstitialPreloadAdManager(
            this,
            BuildConfig.inter_splash_id,
            BuildConfig.inter_splash_id2,
            BuildConfig.inter_splash_id3,
        )

        CustomApplication.app.mNativeAdManager = NativeAdsManager(
            this,
            BuildConfig.native_language_id,
            BuildConfig.native_language_id2,
            BuildConfig.native_language_id3,
        )

        CustomApplication.app.mNativeAdManagerHome = NativeAdsManager(
            this,
            BuildConfig.native_home_id,
            BuildConfig.native_home_id2,
            BuildConfig.native_home_id3,
        )


    }

    private val callback = object : InterstitialPreloadAdManager.InterstitialAdListener {
        override fun onClose() {
            dialogLoadingInterAds?.dismiss()
            navigateMain()
        }

        override fun onError() {
            dialogLoadingInterAds?.dismiss()
            navigateMain()
        }
    }

    private fun checkAdsLoad() {
        if (status >= 3) {
            if (interSplashAds?.loadAdsSuccess == true) {
                handleAtLeast3Second(action = {
                    lifecycleScope.launchWhenResumed {
                        dialogLoadingInterAds?.show(
                            supportFragmentManager,
                            dialogLoadingInterAds?.tag
                        )
                        interSplashAds?.showAds(
                            this@SplashActivity, callback
                        )
                    }
                })
            } else {
                navigateMain()
            }

        } else if (initDone && status >= 2) {
            if (interSplashAds?.loadAdsSuccess == true) {
                handleAtLeast3Second(action = {
                    lifecycleScope.launchWhenResumed {
                        dialogLoadingInterAds?.show(supportFragmentManager, dialogLoadingInterAds?.tag)
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(500)
                            interSplashAds?.showAds(
                                this@SplashActivity, callback
                            )
                        }
                    }

                })
            } else {
                navigateMain()
            }
        }
    }


    private fun handleAds() {
        if (!isInternetAvailable(this)) {
            handleWhenAnimationDone(action = {
                navigateMain()
            })
        } else {
            if (initDone) {
                Handler().postDelayed({
                    loadSplashAds()
                    loadNativeHomeAds()
                }, 1000)
            } else {
                Handler().postDelayed({
                    loadNativeAds()
                    loadSplashAds()
                    loadNativeHomeAds()
                }, 1000)
            }
        }
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

    private fun loadNativeAds() {
        CustomApplication.app.mNativeAdManager?.loadAds(onLoadSuccess = {
            app.nativeAD?.value = it
            status++
            checkAdsLoad()
        }, onLoadFail = {
            status++
            checkAdsLoad()
        })

    }

    private fun loadNativeHomeAds() {

        CustomApplication.app.mNativeAdManagerHome?.loadAds(onLoadSuccess = {
            app.nativeADHome?.value = it
            status++
            checkAdsLoad()
        }, onLoadFail = {
            status++
            checkAdsLoad()
        })
    }

    private fun loadSplashAds() {
        interSplashAds?.loadAds(onAdLoadFail = {
            status++
            interSplashAds?.loadAdsSuccess = false
            checkAdsLoad()
        }, onAdLoader = {
            status++
            interSplashAds?.loadAdsSuccess = true
            checkAdsLoad()
        })
    }

    private fun handleWhenAnimationDone(action: () -> Unit) {
        Handler().postDelayed({
            action.invoke()

        }, 3000)

    }

    private fun handleAtLeast3Second(action: () -> Unit) {
        Handler().postDelayed({
            action.invoke()
        }, 1000)
    }

    private fun navigateMain() {
        val i = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        InterstitialPreloadAdManager.isShowingAds = false
        InterstitialSingleReqAdManager.isShowingAds = false
//        dialogLoadingInterAds?.dismiss()

    }
}