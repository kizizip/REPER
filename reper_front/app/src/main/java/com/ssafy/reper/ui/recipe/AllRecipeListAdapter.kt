package com.ssafy.reper.ui.recipe

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.databinding.ItemAllRecipeRvBinding

private const val TAG = "AllRecipeListAdapter_정언"
class AllRecipeListAdapter (var recipeList:MutableList<String>, val itemClickListener:ItemClickListener) : RecyclerView.Adapter<AllRecipeListAdapter.AllRecipeListHolder>() {
    inner class AllRecipeListHolder(private val binding: ItemAllRecipeRvBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int){
            val item = recipeList[position]

            // 메뉴명 설정
            binding.allrecipeRvItemTvMenu.text = item

            // 흐르는 글씨를 위해 selected 처리
            binding.allrecipeRvItemTvMenu.isSelected = true

            // 즐겨찾기 버튼 클릭 시 -> 즐겨찾기 제거, 추가
            binding.allrecipeRvItemBtnHeart.setOnClickListener {
                itemClickListener.onClick(0, layoutPosition)
                if(binding.allrecipeRvItemIvLineheart.visibility == View.VISIBLE){
                    binding.allrecipeRvItemIvLineheart.visibility = View.GONE
                    binding.alllrecipeRvItemIvFullheart.visibility = View.VISIBLE
                }
                else{
                    binding.allrecipeRvItemIvLineheart.visibility = View.VISIBLE
                    binding.alllrecipeRvItemIvFullheart.visibility = View.GONE
                }
            }

            // 전체 클릭시 -> recipe 전환
            binding.root.setOnClickListener{
                itemClickListener.onClick(1, layoutPosition)
            }
        }
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    fun  interface ItemClickListener {
        fun onClick(id: Int, position: Int)
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