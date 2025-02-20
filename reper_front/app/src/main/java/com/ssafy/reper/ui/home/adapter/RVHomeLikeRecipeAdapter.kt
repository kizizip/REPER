package com.ssafy.reper.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.FavoriteRecipe

class RVHomeLikeRecipeAdapter(private val favoriteRecipes: MutableList<FavoriteRecipe>, private val onItemClick: (FavoriteRecipe) -> Unit) : RecyclerView.Adapter<RVHomeLikeRecipeAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(favoriteRecipes[position])
                }
            }
        }

        fun bindItems(item: FavoriteRecipe) {
            val rvTitle = itemView.findViewById<TextView>(R.id.rv_home_like_recipe_text)
            val rvImg = itemView.findViewById<ImageView>(R.id.rv_home_like_recipe_img)

            rvTitle.text = item.recipeName
            rvImg.setImageResource(R.drawable.noimage)


            // 이미지 설정
            if(item.recipeImg != null){
                Glide.with(rvImg)
                    .load(item.recipeImg)
                    .into(rvImg)
            }
            else{
                rvImg.setImageResource(R.drawable.noimage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_home_like_recipe_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(favoriteRecipes[position])
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }
}