package com.neko.hiepdph.cattranslate.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.cattranslate.R
import com.neko.hiepdph.cattranslate.common.*
import com.neko.hiepdph.cattranslate.data.CatModel
import com.neko.hiepdph.cattranslate.databinding.FragmentCatPrankBinding
import com.neko.hiepdph.dogtranslatorlofi.viewmodel.AppViewModel

class CatPrankFragment : Fragment() {

    private lateinit var binding: FragmentCatPrankBinding
    private var adapter: CatPrankAdapter? = null
    private val viewModel by activityViewModels<AppViewModel>()
    var lastClickTime: Long = 0
    var handler = Handler()
    var runnable = Runnable {
        InterstitialSingleReqAdManager.isShowingAds = false
    }
    private var count = 0

    override fun onResume() {
        super.onResume()
        if (lastClickTime > 0) {
            handler.postDelayed(runnable, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
        adapter?.clearSection()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCatPrankBinding.inflate(inflater, container, false)
        count = 0
        return binding.root
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (SystemClock.elapsedRealtime() - lastClickTime < 10000 && InterstitialSingleReqAdManager.isShowingAds) return
                else {
                    viewModel.resetPlayer()
                    showInterAds(action = {
//                        requireContext().pushEvent(BuildConfig.click_tool_wifidetector_back)
                        navigateToPage(
                            R.id.catPrankFragment,
                            R.id.mainFragment
                        )
                    }, InterAdsEnum.BACK)
                    lastClickTime = SystemClock.elapsedRealtime()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
        showBannerAds(binding.bannerAds)
    }

    private fun initView() {
        initRecyclerView()
        initButton()
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            if (SystemClock.elapsedRealtime() - lastClickTime < 10000 && InterstitialSingleReqAdManager.isShowingAds) return@clickWithDebounce
            else {
                viewModel.resetPlayer()
                showInterAds(action = {
//                requireContext().pushEvent(BuildConfig.click_tool_wifidetector_back)
                    navigateToPage(
                        R.id.catPrankFragment,
                        R.id.mainFragment
                    )

                }, InterAdsEnum.BACK)
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        binding.btnDog.clickWithDebounce {
            viewModel.resetPlayer()
            navigateToPage(
                R.id.catPrankFragment, R.id.actionDogTypeFragment
            )
        }

    }


    private fun initRecyclerView() {
        val data = mutableListOf(
            CatModel(R.drawable.ic_cat_hello, R.string.hello, R.raw.cat_hello),
            CatModel(R.drawable.ic_cat_how_are_you, R.string.how_are_you, R.raw.cat_how_are_you),
            CatModel(R.drawable.ic_cat_good_cat, R.string.good_cat, R.raw.cat_good_cat),
            CatModel(R.drawable.ic_cat_bad_cat, R.string.bad_cat, R.raw.cat_bad_cat),
            CatModel(R.drawable.ic_cat_i_love_you, R.string.i_love_you, R.raw.cat_i_love_you),
            CatModel(R.drawable.ic_cat_come_here, R.string.come_here, R.raw.cat_come_here),
            CatModel(R.drawable.ic_cat_fine, R.string.fine, R.raw.cat_fine),
            CatModel(R.drawable.ic_cat_nonono, R.string.no_no, R.raw.cat_nono),
            CatModel(R.drawable.ic_cat_should_not, R.string.should_not, R.raw.cat_should_not),
            CatModel(R.drawable.ic_cat_yes, R.string.yes, R.raw.cat_yes),
            CatModel(
                R.drawable.ic_cat_leave_me_alone, R.string.leave_me_alone, R.raw.cat_leave_me_alone
            ),
            CatModel(
                R.drawable.ic_cat_what_you_have_done,
                R.string.what_have_you_done,
                R.raw.cat_what_have_you_done
            ),
            CatModel(
                R.drawable.ic_cat_punishes,
                R.string.you_are_punisshed,
                R.raw.cat_you_are_punisshed
            ),
            CatModel(R.drawable.ic_cat_angry, R.string.im_angry_with_you, R.raw.cat_anagry),
            CatModel(R.drawable.ic_cat_warning, R.string.last_warning, R.raw.cat_warning),

            )
        adapter = CatPrankAdapter {
            val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it.audio))
            viewModel.playAudio(mediaItem, onEnd = {
                adapter?.clearSection()
                if(count % 5 == 0){
                    showInterAds({}, InterAdsEnum.FUNCTION)
                }
                count ++
            })
        }
        binding.rcvCat.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        binding.rcvCat.layoutManager = layoutManager
        adapter?.setData(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.clearSection()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetPlayer()
    }

}