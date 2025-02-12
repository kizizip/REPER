package com.ssafy.reper.ui.recipe

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Ingredient
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.local.OrderRecipeModel
import com.ssafy.reper.databinding.ItemSteprecipeIngredientsRvBinding
import com.ssafy.reper.databinding.OrderSpinnerItemBinding
import com.ssafy.reper.databinding.RvHomeOrderItemBinding
import com.ssafy.reper.databinding.RvOrderRecipeItemBinding
import com.ssafy.reper.ui.order.adapter.OrderAdatper
import com.ssafy.smartstore_jetpack.util.CommonUtils.makeComma
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.log

private const val TAG = "RecipeIngredientsAdapte_정언"
class RecipeIngredientsAdapter(var ingredients:MutableList<Ingredient>) : RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientListHolder>() {
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