package com.ssafy.reper.ui.recipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.ItemAllRecipeRvBinding

private const val TAG = "AllRecipeListAdapter_정언"
class AllRecipeListAdapter (var recipeList:MutableList<Recipe>, var favoriteRecipeList:MutableList<FavoriteRecipe>, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<AllRecipeListAdapter.AllRecipeListHolder>() {
    inner class AllRecipeListHolder(private val binding: ItemAllRecipeRvBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int){
            val item = recipeList[position]

            // 메뉴명 설정
            binding.allrecipeRvItemTvMenu.text = item.recipeName

            // 이미지 설정
            if(item.recipeImg != null){
                Glide.with(binding.root)
                    .load(item.recipeImg)
                    .into(binding.allrecipeRvItemIvMenu)
            }
            else{
                binding.allrecipeRvItemIvMenu.setImageResource(R.drawable.noimage)
            }

            // 흐르는 글씨를 위해 selected 처리
            binding.allrecipeRvItemTvMenu.isSelected = true

            binding.allrecipeRvItemIvLineheart.visibility = View.VISIBLE
            binding.alllrecipeRvItemIvFullheart.visibility= View.GONE

            for(favorite in favoriteRecipeList){
                if(item.recipeName == favorite.recipeName){
                    binding.allrecipeRvItemIvLineheart.visibility = View.GONE
                    binding.alllrecipeRvItemIvFullheart.visibility= View.VISIBLE
                }
            }

            // 즐겨찾기 버튼 클릭 시 -> 즐겨찾기 제거, 추가
            binding.allrecipeRvItemBtnHeart.setOnClickListener {
                if(binding.allrecipeRvItemIvLineheart.visibility == View.VISIBLE){
                    itemClickListener.onClick(0, item.recipeName, item.recipeId, item.recipeImg)
                    binding.allrecipeRvItemIvLineheart.visibility = View.GONE
                    binding.alllrecipeRvItemIvFullheart.visibility = View.VISIBLE
                }
                else{
                    itemClickListener.onClick(1, item.recipeName, item.recipeId, item.recipeImg)
                    binding.allrecipeRvItemIvLineheart.visibility = View.VISIBLE
                    binding.alllrecipeRvItemIvFullheart.visibility = View.GONE
                }
            }

            // 전체 클릭시 -> recipe 전환
            binding.root.setOnClickListener{
                itemClickListener.onClick(2, item.recipeName, item.recipeId, item.recipeImg)
            }
        }
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    fun  interface ItemClickListener {
        fun onClick(id: Int, recipeName: String, recipeId: Int, recipeImg:Any?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRecipeListHolder {
        return  AllRecipeListHolder (ItemAllRecipeRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AllRecipeListHolder, position: Int) {
        holder.bindInfo(position)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}