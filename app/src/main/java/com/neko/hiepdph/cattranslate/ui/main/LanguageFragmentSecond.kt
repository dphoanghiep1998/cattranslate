package com.neko.hiepdph.cattranslate.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gianghv.libads.NativeAdsManager
import com.google.android.gms.ads.nativead.NativeAd
import com.neko.hiepdph.cattranslate.BuildConfig
import com.neko.hiepdph.cattranslate.R
import com.neko.hiepdph.cattranslate.common.*
import com.neko.hiepdph.cattranslate.common.AppSharePreference.Companion.INSTANCE
import com.neko.hiepdph.cattranslate.databinding.FragmentLanguageBinding
import com.neko.hiepdph.cattranslate.ui.CustomApplication
import java.util.*

class LanguageFragmentSecond : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    private var currentLanguage = Locale.getDefault().language

    private var adapter: AdapterLanguage? = null
    private var nativeAd: NativeAd? = null
    private val initDone = INSTANCE.getSetLangFirst(false)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        showBannerAds(binding.bannerAds)

    }

    private fun initView() {
        initRecyclerView()
        initButton()
    }

    private fun initButton() {
        binding.btnDone.clickWithDebounce {
            requireContext().pushEvent("click_language_save")
            INSTANCE.saveLanguage(currentLanguage)
            startActivity(requireActivity().intent)
            requireActivity().finish()
        }
    }

    private fun initRecyclerView() {
        val mLanguageList: MutableList<Any> = supportedLanguages().toMutableList()
        val mDisplayLangList: MutableList<Any> = supportDisplayLang().toMutableList()
        handleUnSupportLang(mLanguageList)
        mLanguageList.add(1, "adsApp")
        mDisplayLangList.add(1, "adsApp")
        adapter = AdapterLanguage(requireContext(), onCLickItem = {
            currentLanguage = it.language
        })
        adapter?.setData(mLanguageList, mDisplayLangList)
        binding.rcvLanguage.adapter = adapter
        binding.rcvLanguage.layoutManager = LinearLayoutManager(requireContext())
        adapter?.setCurrentLanguage(getCurrentLanguage())

        insertAds()
    }

    private fun handleUnSupportLang(mLanguageList: MutableList<Any>) {
        var support = false
        mLanguageList.forEachIndexed { index, item ->
            if (item is Locale) {
                if (item.language == currentLanguage) {
                    support = true
                }
            }
        }
        if (!support) {
            currentLanguage = (mLanguageList[0] as Locale).language
        }
    }

    private fun getCurrentLanguage(): String {
        return INSTANCE.getSavedLanguage(Locale.getDefault().language)
    }

    private fun insertAds() {
        CustomApplication.app.nativeAD?.observe(viewLifecycleOwner) {
            it?.let {
                adapter?.insertAds(it)
            }
        }
        if(CustomApplication.app.nativeAD?.value == null){
            CustomApplication.app.mNativeAdManager?.loadAds (onLoadSuccess = {
                CustomApplication.app.nativeAD?.value = it
            })
        }
    }

    private fun loadAds() {
        val mNativeAdManager = NativeAdsManager(
            requireContext(),
            BuildConfig.native_language_id,
            BuildConfig.native_language_id2,
            BuildConfig.native_language_id3,
        )
        mNativeAdManager.loadAds(onLoadSuccess = {
            nativeAd = it
            adapter?.insertAds(it)

        }, onLoadFail = {})


    }

}