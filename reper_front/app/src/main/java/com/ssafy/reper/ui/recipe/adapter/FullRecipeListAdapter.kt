package com.ssafy.reper.ui.recipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.databinding.ItemFullRecipeStepRvBinding

class FullRecipeListAdapter (var recipeStepList:MutableList<String>, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<FullRecipeListAdapter.FullRecipeListHolder>() {
    inner class FullRecipeListHolder(private val binding: ItemFullRecipeStepRvBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int){
            val item = recipeStepList[position]

            // 레시피 스탭 설정
            binding.steprecipeItemTvStep.text = "${position + 1}. ${item}"

            binding.root.setOnClickListener {
                itemClickListener.onClick(layoutPosition)
            }
        }
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    fun  interface ItemClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullRecipeListHolder {
        return  FullRecipeListHolder (ItemFullRecipeStepRvBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: FullRecipeListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return recipeStepList.size
    }
}