package com.ssafy.reper.ui.recipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentFullRecipeItemBinding

class FullRecipeViewPagerAdapter(
    private val recipeList: List<Recipe>,
    private val whereAmICame: Int,
    private val orderDetails: List<OrderDetail>? = null,
    private val favoriteRecipeList: List<FavoriteRecipe>? = null,
    private val onHeartClick: ((Recipe, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<FullRecipeViewPagerAdapter.FullRecipeViewHolder>() {

    inner class FullRecipeViewHolder(
        private val binding: FragmentFullRecipeItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(recipe: Recipe) {
            binding.apply {
                fullrecipeFmTvCategory.text = recipe.category
                fullrecipeFmTvMenuName.text = recipe.recipeName

                // 재료 표시
                val ingredient = recipe.ingredients.joinToString(", ") { it.ingredientName }
                fullrecipeFmTvIngredients.text = ingredient

                // AllRecipeFragment에서 온 경우
                if (whereAmICame == 1) {
                    fullrecipeFmFlBtnHeart.visibility = View.VISIBLE
                    fullrecipeFmBtngroup.visibility = View.VISIBLE
                    fullrecipeFmTvNote.visibility = View.GONE
                    textView11.visibility = View.GONE

                    // 즐겨찾기 상태 설정
                    val isFavorite = favoriteRecipeList?.any { it.recipeId == recipe.recipeId } ?: false
                    fullrecipeFmIvLineheart.visibility = if (isFavorite) View.GONE else View.VISIBLE
                    fullrecipeFmIvFullheart.visibility = if (isFavorite) View.VISIBLE else View.GONE

                    // 즐겨찾기 버튼 클릭 이벤트
                    fullrecipeFmBtnHeart.setOnClickListener {
                        onHeartClick?.invoke(recipe, !isFavorite)
                    }

                    // ICE/HOT 버튼 설정
                    if (recipe.type == "ICE") {
                        fullrecipeFmBtngroup.check(fullrecipeFmBtnIce.id)
                    } else {
                        fullrecipeFmBtngroup.check(fullrecipeFmBtnHot.id)
                    }
                } 
                // OrderRecipeFragment에서 온 경우
                else if (whereAmICame == 2) {
                    fullrecipeFmFlBtnHeart.visibility = View.GONE
                    fullrecipeFmBtngroup.visibility = View.GONE
                    fullrecipeFmTvNote.visibility = View.VISIBLE
                    textView11.visibility = View.VISIBLE

                    // 주문 요청사항 표시
                    val orderDetail = orderDetails?.find { it.recipeId == recipe.recipeId }
                    fullrecipeFmTvNote.text = orderDetail?.customerRequest ?: ""
                }

                // 레시피 스텝 RecyclerView 설정
                fullrecipeFmRvRecipe.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = FullRecipeListAdapter(recipe.recipeSteps.map { it.instruction }.toMutableList(), {
                        // 클릭하면  stepFragment로 이동
                    })
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullRecipeViewHolder {
        return FullRecipeViewHolder(
            FragmentFullRecipeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FullRecipeViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }

    override fun getItemCount() = recipeList.size
} 