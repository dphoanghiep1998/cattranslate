package com.neko.hiepdph.cattranslate.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.cattranslate.common.clickWithDebounce
import com.neko.hiepdph.cattranslate.data.CatModel
import com.neko.hiepdph.cattranslate.data.DogModel
import com.neko.hiepdph.cattranslate.databinding.LayoutItemDogBinding

class DogAdapter(private val onClickItem: (DogModel) -> Unit) :
    RecyclerView.Adapter<DogAdapter.ActionDogTypeViewHolder>() {

    private var data = mutableListOf<DogModel>()
    private var selectedPosition = -1


    fun setData(rawData: MutableList<DogModel>) {
        data.clear()
        data.addAll(rawData)
        notifyDataSetChanged()
    }
    fun clearSection(){
        selectedPosition = -1
        notifyDataSetChanged()
    }


    inner class ActionDogTypeViewHolder(val binding: LayoutItemDogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionDogTypeViewHolder {
        val binding =
            LayoutItemDogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActionDogTypeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ActionDogTypeViewHolder, position: Int) {
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