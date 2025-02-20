package com.ssafy.reper.ui.boss.adpater

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.ItemRecipeBinding

private const val TAG = "RecipeAdapter_싸피"
class RecipeAdapter(var menuList: MutableList<Recipe>, val itemClickListener: ItemClickListener) :
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
        Log.d(TAG, "onBindViewHolder: ${menuList[position].recipeName}")

        // 이미지 설정
        if(menuList[position].recipeImg != null){
            Glide.with(holder.binding.recipeImgIV)
                .load(menuList[position].recipeImg)
                .into(holder.binding.recipeImgIV)
        }
        else{
            holder.binding.recipeImgIV.setImageResource(R.drawable.noimage)
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    fun updateData(recipeList: List<Recipe>) {
        menuList.clear()
        menuList.addAll(recipeList)
        Log.d(TAG, "updateData: $recipeList")
        notifyDataSetChanged()  // 변경된 데이터를 RecyclerView에 반영
    }

}