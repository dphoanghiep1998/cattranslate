package com.neko.hiepdph.cattranslate.ui.main

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.neko.hiepdph.cattranslate.R
import com.neko.hiepdph.cattranslate.common.*
import com.neko.hiepdph.cattranslate.databinding.FragmentMainBinding
import com.neko.hiepdph.cattranslate.ui.CustomApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.system.exitProcess

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    var lastClickTime: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        changeBackPressCallBack()
        loadAds()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        lifecycleScope.launchWhenResumed {
            if (CustomApplication.app.firstOpen) {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(400)
                    checkTimeSession()
                }
                CustomApplication.app.firstOpen = false
            }
        }
    }

    private fun checkTimeSession() {
        val userRate = AppSharePreference.INSTANCE.getUserRated(false)
        val timeSession = AppSharePreference.INSTANCE.getTimeLogin(0)
        if (!userRate && timeSession > 0) {
            showRate()
        }
        AppSharePreference.INSTANCE.saveTimeLogin(10)
    }

    private fun showRate() {
        val dialogRateUs = DialogRateUs(object : RateCallBack {
            override fun onNegativePressed() {
            }

            override fun onPositivePressed() {
            }

        })
        dialogRateUs.show(childFragmentManager, dialogRateUs.tag)
    }


    private fun initView() {
        initFlag()
        initButton()
    }

    private fun initFlag() {
        val mLanguageList = supportedLanguages().toMutableList()
        val mDisplayLangList = supportDisplayLang().toMutableList()

        val currentLang = AppSharePreference.INSTANCE.getSavedLanguage(Locale.getDefault().language)

        mLanguageList.forEachIndexed { index, item ->
            if (item.language == currentLang) {
                binding.imvFlag.setImageResource(mDisplayLangList[index].second)
            }
        }
        binding.imvFlag.clickWithDebounce {
            navigateToPage(R.id.mainFragment, R.id.languageFragmentSecond)
        }
    }


    private fun initButton() {

        binding.tvCat.clickWithDebounce {
            requireContext().pushEvent("click_home_cat")
            navigateToPage(R.id.mainFragment, R.id.action_mainFragment_to_catPrankFragment)
        }
        binding.tvDog.clickWithDebounce {
            requireContext().pushEvent("click_home_dog")
            navigateToPage(R.id.mainFragment, R.id.action_mainFragment_to_actionDogTypeFragment)
        }
        binding.tvRecording.clickWithDebounce {
            requireContext().pushEvent("click_home_record")
            navigateToPage(R.id.mainFragment, R.id.action_mainFragment_to_prankRecordFragment)
        }
//        binding.btnCat.clickWithDebounce {
//            requireContext().pushEvent("click_home_record")
//            navigateToPage(R.id.mainFragment, R.id.action_mainFragment_to_catPrankFragment)
//        }
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (SystemClock.elapsedRealtime() - lastClickTime < 10000 && InterstitialSingleReqAdManager.isShowingAds) return
                else showInterAds(action = {
//                        requireContext().pushEvent(BuildConfig.click_tool_wifidetector_back)
                    requireActivity().finishAffinity()
                    exitProcess(0)

                }, InterAdsEnum.BACK)
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun loadAds() {
        CustomApplication.app.nativeADHome?.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.nativeView.setNativeAd(it)
                binding.nativeView.visibility = View.VISIBLE
                binding.nativeView.showShimmer(false)
                binding.nativeView.isVisible = true
            } else {
                binding.nativeView.isVisible = false
                binding.nativeView.visibility = View.GONE
            }
        }
        if (CustomApplication.app.nativeADHome?.value == null) {
            CustomApplication.app.mNativeAdManagerHome?.loadAds(onLoadSuccess = {
                CustomApplication.app.nativeADHome?.value = it
            })
        }
    }

}