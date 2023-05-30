package com.neko.hiepdph.cattranslate.common

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gianghv.libads.AdaptiveBannerManager
import com.gianghv.libads.InterstitialSingleReqAdManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.neko.hiepdph.cattranslate.BuildConfig


fun Fragment.navigateToPage(fragmentId: Int, actionId: Int, bundle: Bundle? = null) {
    if (fragmentId == findNavController().currentDestination?.id) {
        findNavController().navigate(actionId, bundle)
    }
}

fun Fragment.navigateBack(id: Int) {
    if (findNavController().currentDestination?.id == id) {
        findNavController().popBackStack()
    }

}

fun Fragment.showBannerAds(view: ViewGroup, action: (() -> Unit)? = null) {
    val adaptiveBannerManager = AdaptiveBannerManager(
        requireActivity(),
        BuildConfig.banner_home_id,
        BuildConfig.banner_home_id2,
        BuildConfig.banner_home_id3,
    )
    if (AdaptiveBannerManager.isBannerLoaded) {
        adaptiveBannerManager.loadAdViewToParent(view)
        return
    }

    adaptiveBannerManager.loadBanner(view, onAdLoadFail = {
        view.visibility = View.GONE
        action?.invoke()
    }, onAdLoader = {
        view.visibility = View.VISIBLE

        action?.invoke()
    })
}

fun Context.pushEvent(key: String) {
    FirebaseAnalytics.getInstance(this).logEvent(key, null)
}

fun Fragment.showInterAds(
    action: () -> Unit, type: InterAdsEnum
) {
    if (!isAdded) {
        action.invoke()
        return
    }
    if (!isInternetAvailable(requireContext())) {
        action.invoke()
        return
    }

    if (activity == null) {
        action.invoke()
        return
    }
    if (InterstitialSingleReqAdManager.isShowingAds) {
        return
    }
    val interstitialSingleReqAdManager: InterstitialSingleReqAdManager
    when (type) {
        InterAdsEnum.SPLASH -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_splash_id,
                BuildConfig.inter_splash_id2,
                BuildConfig.inter_splash_id3,
            )
        }
        InterAdsEnum.BACK -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_back_id,
                BuildConfig.inter_back_id2,
                BuildConfig.inter_back_id3,
            )
        }
        InterAdsEnum.FUNCTION -> {
            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
                requireActivity(),
                BuildConfig.inter_function_id,
                BuildConfig.inter_function_id2,
                BuildConfig.inter_function_id3,
            )
        }


    }

    InterstitialSingleReqAdManager.isShowingAds = true

    val dialogLoadingInterAds = DialogFragmentLoadingInterAds()
    lifecycleScope.launchWhenResumed {
        dialogLoadingInterAds.show(childFragmentManager, dialogLoadingInterAds.tag)

        interstitialSingleReqAdManager.showAds(requireActivity(), onLoadAdSuccess = {
            dialogLoadingInterAds.dismissAllowingStateLoss()
        }, onAdClose = {
            InterstitialSingleReqAdManager.isShowingAds = false
            action()
            dialogLoadingInterAds.dismissAllowingStateLoss()
        }, onAdLoadFail = {
            InterstitialSingleReqAdManager.isShowingAds = false
            action()
            dialogLoadingInterAds.dismissAllowingStateLoss()
        })
    }

}

fun isInternetAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    if (netInfo != null) {
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    return false

}
