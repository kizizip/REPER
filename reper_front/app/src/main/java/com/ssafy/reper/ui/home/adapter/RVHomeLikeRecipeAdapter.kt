package com.ssafy.reper.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.reper.R
import com.ssafy.reper.data.local.HomeLikeRecipeModel

class RVHomeLikeRecipeAdapter (val List: MutableList<HomeLikeRecipeModel>) : RecyclerView.Adapter<RVHomeLikeRecipeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVHomeLikeRecipeAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_home_like_recipe_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RVHomeLikeRecipeAdapter.ViewHolder, position: Int) {

        holder.bindItems(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: HomeLikeRecipeModel) {

            val rv_title = itemView.findViewById<TextView>(R.id.rv_home_like_recipe_text)
            val rv_img = itemView.findViewById<ImageView>(R.id.rv_home_like_recipe_img)

            rv_title.text = item.recipeTitle
            rv_img.setImageResource(item.imageUrl)

        }
    }


}