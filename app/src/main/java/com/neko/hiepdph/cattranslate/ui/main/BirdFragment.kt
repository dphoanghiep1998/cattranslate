package com.neko.hiepdph.cattranslate.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
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
import com.neko.hiepdph.cattranslate.data.BirdModel
import com.neko.hiepdph.cattranslate.databinding.FragmentRoarDogTypeBinding
import com.neko.hiepdph.dogtranslatorlofi.viewmodel.AppViewModel


class BirdFragment : Fragment() {

    private lateinit var binding: FragmentRoarDogTypeBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var adapter: BirdAdapter? = null
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
        binding = FragmentRoarDogTypeBinding.inflate(inflater, container, false)
        count = 0
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
        showBannerAds(binding.bannerAds)
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
                            R.id.roarDogTypeFragment,
                            R.id.action_roarDogTypeFragment_to_mainFragment
                        )
                    }, InterAdsEnum.BACK)
                    lastClickTime = SystemClock.elapsedRealtime()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun initView() {
        initButton()
        initRecyclerView()
    }

    private fun initButton() {
        binding.btnDogAnim.clickWithDebounce {
            viewModel.resetPlayer()
            navigateToPage(
                R.id.roarDogTypeFragment, R.id.action_roarDogTypeFragment_to_actionDogTypeFragment
            )
        }
        binding.btnBack.clickWithDebounce {
            if (SystemClock.elapsedRealtime() - lastClickTime < 10000 && InterstitialSingleReqAdManager.isShowingAds) return@clickWithDebounce
            else {
                viewModel.resetPlayer()
                showInterAds(action = {
//                requireContext().pushEvent(BuildConfig.click_tool_wifidetector_back)
                    navigateToPage(
                        R.id.roarDogTypeFragment,
                        R.id.action_roarDogTypeFragment_to_mainFragment
                    )


                }, InterAdsEnum.BACK)
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }

    }

    private fun initRecyclerView() {
        adapter = BirdAdapter {
            val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it.audio))
            viewModel.playAudio(mediaItem, onEnd = {
                Log.d("TAG", "initRecyclerView: ")
                adapter?.clearSection()
                if(count % 5 == 0){
                    showInterAds({}, InterAdsEnum.FUNCTION)
                }
                count ++
            })
        }
        binding.rcvDog.adapter = adapter
        val gridLayoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        binding.rcvDog.layoutManager = gridLayoutManager

        val data = mutableListOf(
            BirdModel(R.drawable.ic_bird_1, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_2, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_3, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_4, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_5, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_6, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_7, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_8, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_9, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_10, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_11, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_12, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_13, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_14, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_15, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_16, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_17, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_18, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_19, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_20, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_21, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_22, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_23, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_24, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_25, R.raw.q_1),
            BirdModel(R.drawable.ic_bird_26, R.raw.q_1),

        )

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