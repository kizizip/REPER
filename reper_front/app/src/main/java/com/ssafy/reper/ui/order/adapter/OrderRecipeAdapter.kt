package com.ssafy.reper.ui.order.adapter

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
import com.bumptech.glide.Glide
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.local.OrderRecipeModel
import com.ssafy.reper.databinding.OrderSpinnerItemBinding
import com.ssafy.reper.databinding.RvHomeOrderItemBinding
import com.ssafy.reper.databinding.RvOrderRecipeItemBinding
import com.ssafy.smartstore_jetpack.util.CommonUtils.makeComma
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.log

private const val TAG = "OrderRecipeAdapter_정언"
class OrderRecipeAdatper(var orderDetailList: MutableList<OrderDetail>, var recipeList: MutableList<Recipe>, var checkedRecipeList:MutableList<Recipe>, val itemClickListener:ItemClickListener) : RecyclerView.Adapter<OrderRecipeAdatper.OrderDetailListHolder>() {
    inner class OrderDetailListHolder(private val binding: RvOrderRecipeItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int){
            val item = orderDetailList[position]
            val recipe = recipeList[position]
            Log.d(TAG, "bindInfo: ${recipe.recipeId} / ${recipe.recipeName}")

            binding.rvOrderRecipeTitle.text = "${recipe.recipeName} ${recipe.type}"
            binding.rvOrderRecipeQuantity.text = item.quantity.toString()
            binding.rvOrderRecipeMessage.text = item.customerRequest

            if(recipe.recipeImg != null){
                Glide.with(binding.root)
                    .load(recipe.recipeImg)
                    .into(binding.rvOrderRecipeImg)
            }else{
                binding.rvOrderRecipeImg.setImageResource(R.drawable.noimage)
            }

            binding.rvOrderRecipeCheckbox.isChecked = false
            if(checkedRecipeList.isNotEmpty()){
                binding.rvOrderRecipeCheckbox.isChecked = checkedRecipeList.any{ it.recipeId == item.recipeId }
            }

            binding.rvOrderRecipeCheckbox.setOnClickListener {
                itemClickListener.onClick(recipe, binding.rvOrderRecipeCheckbox.isChecked)
            }
        }
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    fun  interface ItemClickListener {
        fun onClick(recipe: Recipe, isChecked:Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailListHolder {
        return  OrderDetailListHolder(RvOrderRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: OrderDetailListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return orderDetailList.size
    }
}