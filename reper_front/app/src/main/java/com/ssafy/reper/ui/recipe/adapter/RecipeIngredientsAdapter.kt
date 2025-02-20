package com.ssafy.reper.ui.recipe.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.Ingredient
import com.ssafy.reper.databinding.ItemStepRecipeRvSelectedBinding
import com.ssafy.reper.databinding.ItemSteprecipeIngredientsRvBinding

private const val TAG = "RecipeIngredientsAdapter_정언"
class RecipeIngredientsAdapter(var ingredients:MutableList<Ingredient>, var isPortrait:Boolean) : RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientListHolder>() {
    inner class IngredientListHolder(private val binding: ItemSteprecipeIngredientsRvBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int){
            val item = ingredients[position]

            binding.ingreditentsIName.setText(item.ingredientName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientListHolder {
        return  IngredientListHolder(ItemSteprecipeIngredientsRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: IngredientListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}