package com.neko.hiepdph.cattranslate.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gianghv.libads.AppOpenAdManager
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.neko.hiepdph.cattranslate.R
import com.neko.hiepdph.cattranslate.common.*
import com.neko.hiepdph.cattranslate.databinding.FragmentPrankRecordBinding
import com.neko.hiepdph.dogtranslatorlofi.viewmodel.AppViewModel

class PrankRecordFragment : Fragment() {
    private lateinit var binding: FragmentPrankRecordBinding
    private val viewModel by activityViewModels<AppViewModel>()
    private var isRecording = false
    private var isPlaying = false
    private val animSet = AnimationSet(true)
    private var currentTime = 0L
    private var timeRecord = 0L
    private var isNewRecord = false
    private var count = 0


    private val dogData = mutableListOf(
        R.raw.angry,
        R.raw.begie_11,
        R.raw.chihuahua_6,
        R.raw.cho_chan_cuu,
        R.raw.corgi_5,
        R.raw.frendly,
        R.raw.gaudan_4,
        R.raw.guilty,
        R.raw.happy,
        R.raw.husky_9,
        R.raw.let_play,
        R.raw.phuquoc_10,
        R.raw.pull_phap,
        R.raw.pugmatxe_12,
        R.raw.q_1,
        R.raw.requires,
        R.raw.scared,
        R.raw.shiba_7,
    )
    private val catData = mutableListOf(
        R.raw.cat_anagry,
        R.raw.cat_bad_cat,
        R.raw.cat_fine,
        R.raw.cat_good_cat,
        R.raw.cat_hello,
        R.raw.cat_come_here,
        R.raw.cat_how_are_you,
        R.raw.cat_i_love_you,
        R.raw.cat_leave_me_alone,
        R.raw.cat_nono,
        R.raw.cat_should_not,
        R.raw.cat_warning,
        R.raw.cat_what_have_you_done,
        R.raw.cat_yes,
        R.raw.cat_you_are_punisshed,
    )
    private val pigData = mutableListOf(
        R.raw.pig_1,
        R.raw.pig_2,
        R.raw.pig_3,
        R.raw.pig_4,
        R.raw.pig_5,
        R.raw.pig_6,
        R.raw.pig_7,

        )

    private var lionData = mutableListOf<Int>(
        R.raw.lion_1,
        R.raw.lion_2,
        R.raw.lion_3,
        R.raw.lion_4,
    )


    var lastClickTime: Long = 0
    var handler = Handler()
    var runnable = Runnable {
        InterstitialSingleReqAdManager.isShowingAds = false
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume: " + binding.btnPlay.isEnabled)
        if (lastClickTime > 0) {
            handler.postDelayed(runnable, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
        resetAnimation()
        isPlaying = false
    }

    private fun resetAnimation() {
        if (!isRecording) {
            binding.btnPlay.clearAnimation()
            binding.btnRecord.clearAnimation()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPrankRecordBinding.inflate(inflater, container, false)
        count = 0
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
        viewModel.currentType = 0
        showBannerAds(binding.bannerAds)

    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (SystemClock.elapsedRealtime() - lastClickTime < 10000 && InterstitialSingleReqAdManager.isShowingAds) return
                else {
                    viewModel.resetPlayer()
                    showInterAds(action = {
                        navigateToPage(
                            R.id.prankRecordFragment,
                            R.id.action_prankRecordFragment_to_mainFragment
                        )
                    }, InterAdsEnum.BACK)
                    lastClickTime = SystemClock.elapsedRealtime()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun initView() {
        val alphaIn = AlphaAnimation(0.0f, 1.0f)
        alphaIn.duration = 800
        animSet.addAnimation(alphaIn)

        val alphaOut = AlphaAnimation(1.0f, 0.0f)
        alphaOut.duration = 800
        animSet.addAnimation(alphaOut)
        initButton()
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            if (SystemClock.elapsedRealtime() - lastClickTime < 10000 && InterstitialSingleReqAdManager.isShowingAds) return@clickWithDebounce
            else {
                viewModel.resetPlayer()
                showInterAds(action = {
//                requireContext().pushEvent(BuildConfig.click_tool_wifidetector_back)
                    navigateBack(R.id.prankRecordFragment)


                }, InterAdsEnum.BACK)
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }
        val listGroup = mutableListOf(
            Triple(binding.containerDog, binding.imvDog, binding.layoutOverlay1),
            Triple(binding.containerCat, binding.imvCat, binding.layoutOverlay2),
            Triple(binding.containerPig, binding.imvPig, binding.layoutOverlay3),
            Triple(binding.containerLion, binding.imvLion, binding.layoutOverlay4),
        )

        binding.btnRecord.clickWithDebounce {
            isPlaying = false
            if (!isRecording) {
                val anim = AlphaAnimation(
                    1.0f,
                    0.3f,
                )
                anim.duration = 600
                anim.repeatCount = Animation.INFINITE
                anim.repeatMode = Animation.REVERSE
                binding.recordInstruction.text = getString(R.string.click_again_to_stop)
                binding.recording.show()
                binding.btnRecord.startAnimation(anim)
                binding.btnPlay.clearAnimation()
                isRecording = true
                viewModel.resetPlayer()
                startFakeRecording()
            } else {
                binding.recordInstruction.text = getString(R.string.click_to_start_recording)
                endFakeRecording()
                isRecording = false
                binding.btnRecord.clearAnimation()
                binding.recording.hide()
                binding.playInstruction.show()
                binding.btnPlay.show()
                isNewRecord = true
            }
        }

        binding.btnPlay.clickWithDebounce {
            if (isPlaying) {
                return@clickWithDebounce
            }
            if (isRecording) {
                Toast.makeText(
                    requireContext(), getString(R.string.stop_record_to_play), Toast.LENGTH_SHORT
                ).show()
                return@clickWithDebounce
            }
            val anim = AlphaAnimation(
                1.0f,
                0.3f,
            )
            anim.duration = 600
            anim.repeatCount = Animation.INFINITE
            anim.repeatMode = Animation.REVERSE
            binding.btnPlay.startAnimation(anim)
            isRecording = false
            isPlaying = true
            playTranslateRecording()
            isNewRecord = false
        }


        listGroup.forEachIndexed { index, item ->
            item.first.clickWithDebounce {
                if (isPlaying) {
                    Log.d("TAG", "initButton: ")
                    Toast.makeText(
                        requireContext(), getString(R.string.playing_warning), Toast.LENGTH_SHORT
                    ).show()
                    return@clickWithDebounce
                }
                when (index) {
                    0 -> requireContext().pushEvent("click_record_dog")
                    1 -> requireContext().pushEvent("click_record_cat")
                    2 -> requireContext().pushEvent("click_record_pig")
                    3 -> requireContext().pushEvent("click_record_lion")
                }
                requireContext().pushEvent("click_record_type")
                viewModel.currentType = index
                item.second.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_selected_item_prank)
                item.third.visibility = View.GONE
                listGroup.filter { it.first != item.first }.forEach {
                    it.second.background = null
                    it.third.visibility = View.VISIBLE
                }
            }
        }


    }

    private fun startFakeRecording() {
        currentTime = SystemClock.elapsedRealtime()
    }

    private fun endFakeRecording() {
        timeRecord = SystemClock.elapsedRealtime() - currentTime
    }

    private fun playTranslateRecording() {
        val oldData = when (viewModel.currentType) {
            0 -> {
                dogData.toMutableList()
            }
            1 -> {
                catData.toMutableList()
            }
            2 -> {
                pigData.toMutableList()
            }
            else -> {
                lionData.toMutableList()
            }
        }

        if (isNewRecord) {
            when (viewModel.currentType) {
                0 -> {
                    dogData.shuffle()
                }
                1 -> {
                    catData.shuffle()
                }
                2 -> {
                    pigData.shuffle()

                }
                else -> {
                    lionData.shuffle()
                }
            }
        }
        val listOfMediaItem = if (isNewRecord) {
            when (viewModel.currentType) {
                0 -> {
                    dogData.map {
                        MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it))
                    }
                }
                1 -> {
                    catData.map {
                        MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it))
                    }
                }
                2 -> {
                    pigData.map {
                        MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it))
                    }

                }
                else -> {
                    lionData.map {
                        MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it))
                    }
                }
            }

        } else {
            oldData.map {
                MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it))
            }
        }

        viewModel.playAudio(listOfMediaItem.toMutableList(), timeRecord, onEnd = {
            binding.btnPlay.clearAnimation()
            isPlaying = false

            val listGroup = mutableListOf(
                Triple(binding.containerDog, binding.imvDog, binding.layoutOverlay1),
                Triple(binding.containerCat, binding.imvCat, binding.layoutOverlay2),
                Triple(binding.containerPig, binding.imvPig, binding.layoutOverlay3),
                Triple(binding.containerLion, binding.imvLion, binding.layoutOverlay4),
            )
            listGroup.forEach {
                it.first.isEnabled = true
            }
            if (count == 0) {
                if(!AppOpenAdManager.isShowingAd){
                    showInterAds({}, InterAdsEnum.FUNCTION)
                }
                count++
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnPlay.clearAnimation()
        binding.btnRecord.clearAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetPlayer()
    }

}