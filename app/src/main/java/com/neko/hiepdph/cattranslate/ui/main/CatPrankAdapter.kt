package com.neko.hiepdph.cattranslate.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.cattranslate.common.clickWithDebounce
import com.neko.hiepdph.cattranslate.data.CatModel
import com.neko.hiepdph.cattranslate.databinding.LayoutItemDogBinding

class CatPrankAdapter(private val onClickItem: (CatModel) -> Unit) :
    RecyclerView.Adapter<CatPrankAdapter.CatTypeViewHolder>() {

    private var data = mutableListOf<CatModel>()
    private var selectedPosition = -1


    fun setData(rawData: MutableList<CatModel>) {
        data.clear()
        data.addAll(rawData)
        notifyDataSetChanged()
    }
    fun clearSection(){
        selectedPosition = -1
        notifyDataSetChanged()
    }


    inner class CatTypeViewHolder(val binding: LayoutItemDogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatTypeViewHolder {
        val binding =
            LayoutItemDogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatTypeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CatTypeViewHolder, position: Int) {
        with(holder) {
            val item = data[adapterPosition]
            binding.imvDog.setImageResource(item.image)
            binding.tvContent.text = itemView.context.getString(item.content)
            if (selectedPosition != adapterPosition) {
                binding.root.clearAnimation()
            } else {
                val anim = ScaleAnimation(
                    1.0f,
                    1.3f,
                    1.0f,
                    1.3f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                anim.duration = 600
                anim.repeatCount = Animation.INFINITE
                anim.repeatMode = Animation.REVERSE
                binding.root.startAnimation(anim)
            }
            binding.root.clickWithDebounce {
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
                onClickItem(item)
            }
        }
    }
}