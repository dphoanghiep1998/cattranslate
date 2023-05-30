package com.neko.hiepdph.cattranslate.ui.main

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.cattranslate.R
import com.neko.hiepdph.cattranslate.common.AppSharePreference
import com.neko.hiepdph.cattranslate.common.clickWithDebounce
import com.neko.hiepdph.cattranslate.databinding.DialogRateUsBinding

interface RateCallBack {
    //    fun rateOnStore()
    fun onNegativePressed()
    fun onPositivePressed()
}

class DialogRateUs(private val callBack: RateCallBack) : DialogFragment() {
    private lateinit var binding: DialogRateUsBinding
    private var star = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(requireContext().getColor(R.color.transparent)))

        dialog.window!!.setLayout(
            (requireContext().resources.displayMetrics.widthPixels),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogRateUsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initFirst()
        initButton()

    }
    private fun openLink(strUri: String?) {
        try {
            val uri = Uri.parse(strUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun initButton() {
        binding.btnRate.clickWithDebounce {
            AppSharePreference.INSTANCE.saveUserRated(true)
            openLink("https://play.google.com/store/apps/details?id=com.dog.translate.prank.dog.joke")
            callBack.onPositivePressed()
            dismiss()
        }
        binding.root.clickWithDebounce {
            callBack.onNegativePressed()
            dismiss()
        }
        binding.btnLater.clickWithDebounce {
            callBack.onNegativePressed()
            dismiss()
        }

    }


    private fun initFirst() {
        binding.root.clickWithDebounce {
            dismiss()
        }
        binding.containerMain.clickWithDebounce {}

    }

}