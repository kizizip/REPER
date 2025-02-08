package com.ssafy.reper.ui.boss.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.ItemRecipeBinding

private const val TAG = "RecipeAdapter_싸피"
class RecipeAdapter(var menuList: List<Recipe>, val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // ViewHolder 클래스
    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.iconRecipeDelete.setOnClickListener {
                itemClickListener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    // 아이템 클릭 리스너 인터페이스
    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        // 데이터 바인딩
        holder.binding.recipeItemName.text = menuList[position].recipeName
        holder.binding.recpeAddDateTV.text = menuList[position].type
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    fun updateData(recipeList: List<Recipe>) {
        menuList = recipeList
    }
}