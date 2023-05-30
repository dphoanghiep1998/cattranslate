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
import com.neko.hiepdph.cattranslate.data.DogModel
import com.neko.hiepdph.cattranslate.databinding.FragmentActionDogTypeBinding
import com.neko.hiepdph.dogtranslatorlofi.viewmodel.AppViewModel

class DogFragment : Fragment() {

    private lateinit var binding: FragmentActionDogTypeBinding
    private var adapter: DogAdapter? = null
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
        binding = FragmentActionDogTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (SystemClock.elapsedRealtime() - lastClickTime < 10000 && InterstitialSingleReqAdManager.isShowingAds) return
                else {
                    viewModel.resetPlayer()
                    showInterAds(action = {
                        navigateToPage(
                            R.id.actionDogTypeFragment,
                            R.id.action_actionDogTypeFragment_to_mainFragment
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
                        R.id.actionDogTypeFragment,
                        R.id.action_actionDogTypeFragment_to_mainFragment
                    )

                }, InterAdsEnum.BACK)
                lastClickTime = SystemClock.elapsedRealtime()
            }
        }

        binding.btnCat.clickWithDebounce {
            viewModel.resetPlayer()
            navigateToPage(
                R.id.actionDogTypeFragment, R.id.catPrankFragment
            )
        }

    }

    private fun initRecyclerView() {
        val data = mutableListOf(
            DogModel(R.drawable.ic_dog_curisous, R.string.curious, R.raw.curious),
            DogModel(R.drawable.ic_dog_suspicious, R.string.suspicious, R.raw.suspicious),
            DogModel(R.drawable.ic_dog_stalking, R.string.stalking, R.raw.stalking),
            DogModel(R.drawable.ic_dog_guilty, R.string.guilty, R.raw.guilty),
            DogModel(R.drawable.ic_dog_let_play, R.string.let_play, R.raw.let_play),
            DogModel(R.drawable.ic_dog_angy, R.string.angry, R.raw.angry),
            DogModel(R.drawable.ic_dog_friendly, R.string.friendly, R.raw.frendly),
            DogModel(R.drawable.ic_dog_requires, R.string.requires, R.raw.requires),
            DogModel(R.drawable.ic_dog_scared, R.string.scared, R.raw.scared),
            DogModel(R.drawable.ic_dog_happy, R.string.happy, R.raw.happy),
            DogModel(R.drawable.ic_dog_tired, R.string.tired, R.raw.tired),
            DogModel(R.drawable.ic_dog_trust, R.string.trusts, R.raw.trusts),
            DogModel(R.drawable.ic_dog_excited, R.string.excited, R.raw.q_1),
            DogModel(R.drawable.ic_dog_lightly, R.string.lightly, R.raw.pull_phap),
            DogModel(R.drawable.ic_dog_lament, R.string.lament, R.raw.cho_chan_cuu),
            DogModel(R.drawable.ic_dog_stalk, R.string.stalk, R.raw.gaudan_4),
            DogModel(R.drawable.ic_dog_trolling, R.string.trolling, R.raw.corgi_5),
            DogModel(R.drawable.ic_dog_tantrum, R.string.tantrum, R.raw.chihuahua_6),
            DogModel(R.drawable.ic_dog_annoyed, R.string.annoyed, R.raw.shiba_7),
            DogModel(R.drawable.ic_dog_stupid, R.string.stupid, R.raw.w_8),
            DogModel(R.drawable.ic_dog_whine, R.string.whine, R.raw.husky_9),
            DogModel(R.drawable.ic_dog_nervous, R.string.nervous, R.raw.phuquoc_10),
            DogModel(R.drawable.ic_dog_deadful, R.string.dreadfully, R.raw.begie_11),
            DogModel(R.drawable.ic_dog_warning, R.string.warning, R.raw.pugmatxe_12),
        )
        adapter = DogAdapter {
            val mediaItem = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(it.audio))
            viewModel.playAudio(mediaItem, onEnd = {
                adapter?.clearSection()
                if (count % 5 == 0) {
                    showInterAds({}, InterAdsEnum.FUNCTION)
                }
                count++
            })
        }
        binding.rcvDog.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        binding.rcvDog.layoutManager = layoutManager
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