package com.ssafy.reper.ui.boss.adpater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.databinding.ItemRecipeBinding

class RecipeAdapter() :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.binding.recipeItemName.text = "아메리카노"
        holder.binding.recpeAddDateTV.text = "2025.01.25"
    }


    override fun getItemCount(): Int {
        return 20
    }
}
