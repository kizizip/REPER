package com.ssafy.reper.ui.recipe.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Ingredient
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.ItemStepRecipeRvSelectedBinding
import com.ssafy.reper.databinding.ItemSteprecipeIngredientsRvBinding

private const val TAG = "StepLandOrderListAdapte_정언"
class StepLandOrderListAdapter(var recipeList:MutableList<Recipe>, var selectedList:MutableList<Recipe>, var nowRecipeId:Int) : RecyclerView.Adapter<StepLandOrderListAdapter.StepLandOrderListHolder>() {
    inner class StepLandOrderListHolder(private val binding: ItemStepRecipeRvSelectedBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int){
            if(position == 0){
                Log.d(TAG, "bindInfo: ${recipeList}")
            }
            val item = recipeList[position]
            binding.steprecipeItemSelectedTvMenu.setText(item.recipeName)

            if(nowRecipeId == item.recipeId){
                binding.steprecipeItemSelectedTvMenu.setTextColor(ContextCompat.getColor(binding.root.context, R.color.darkgreen))
                val typeface = ResourcesCompat.getFont(binding.root.context, R.font.pretendard_bold)
                binding.steprecipeItemSelectedTvMenu.typeface = typeface
            }
            else{
                binding.steprecipeItemSelectedTvMenu.setTextColor(ContextCompat.getColor(binding.root.context, R.color.darkgray))
                val typeface = ResourcesCompat.getFont(binding.root.context, R.font.pretendard_regular)
                binding.steprecipeItemSelectedTvMenu.typeface = typeface
            }

            if(selectedList.contains(item)){
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.morelightgreengray))
            }
            else{
                binding.root.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepLandOrderListHolder {
        return  StepLandOrderListHolder(
            ItemStepRecipeRvSelectedBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: StepLandOrderListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}