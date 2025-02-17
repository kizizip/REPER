package com.ssafy.reper.ui.recipe.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kakao.sdk.common.KakaoSdk.type
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentFullRecipeItemBinding

private const val TAG = "FullRecipeViewPagerAdap_정언"
class FullRecipeViewPagerAdapter(var recipeList: MutableList<Recipe>,var whereAmICame: Int, var customList: MutableList<String>, var favoriteRecipeList: List<FavoriteRecipe>?, var recipeCount :Int, var email: String, var itemClickListener: ItemClickListener) : RecyclerView.Adapter<FullRecipeViewPagerAdapter.FullRecipeViewHolder>() {
    private lateinit var fullRecipeListAdapter: FullRecipeListAdapter
    inner class FullRecipeViewHolder(private val binding: FragmentFullRecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(position: Int) {
            binding.apply {
                val item = recipeList[position]

                fullrecipeFmTvCategory.setText(item.category)
                fullrecipeFmTvMenuName.setText(item.recipeName)
                fullrecipeFmTvUser.setText("이용자 : ${email}")

                if(item.recipeImg != null){
                    Glide.with(binding.root)
                        .load(item.recipeImg)
                        .into(fullrecipeFmIv)
                }else{
                    fullrecipeFmIv.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
                }

                // 재료 표시
                val ingredient = item.ingredients.joinToString(", ") { it.ingredientName }
                fullrecipeFmTvIngredients.setText(ingredient)

                // allrecipe item 클릭 이벤트 리스너
                fullRecipeListAdapter = FullRecipeListAdapter(mutableListOf()) { nowISeeStep ->
                    itemClickListener.onRecipeStepClick(item, nowISeeStep)
                }

                fullrecipeFmRvRecipe.apply {
                    layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                    var stepList:MutableList<String> = mutableListOf()
                    stepList.clear()
                    for(step in item.recipeSteps){
                        stepList.add(step.instruction)
                    }
                    fullRecipeListAdapter.recipeStepList = stepList
                    adapter = fullRecipeListAdapter
                }

                // AllRecipeFragment에서 온 경우
                if (whereAmICame == 1) {
                    fullrecipeFmFlBtnHeart.visibility = View.VISIBLE
                    fullrecipeFmBtngroup.visibility = View.VISIBLE
                    fullrecipeFmBtnIce.visibility = View.VISIBLE
                    fullrecipeFmBtnHot.visibility = View.VISIBLE
                    fullrecipeFmTvNote.visibility = View.GONE
                    textView11.visibility = View.GONE

                    // 즐겨찾기 상태 설정
                    val isFavorite = favoriteRecipeList?.any { it.recipeName == item.recipeName } ?: false
                    fullrecipeFmIvLineheart.visibility = if (isFavorite) View.GONE else View.VISIBLE
                    fullrecipeFmIvFullheart.visibility = if (isFavorite) View.VISIBLE else View.GONE

                    // 즐겨찾기 버튼 클릭 이벤트
                    fullrecipeFmBtnHeart.setOnClickListener {
                        val isFavorite = favoriteRecipeList?.any { it.recipeName == item.recipeName } ?: false
                        fullrecipeFmIvLineheart.visibility = if (isFavorite) View.GONE else View.VISIBLE
                        fullrecipeFmIvFullheart.visibility = if (isFavorite) View.VISIBLE else View.GONE
                        itemClickListener.onHeartClick(item.recipeName, isFavorite)
                    }

                    // ICE/HOT 버튼 설정
                    if(recipeCount > 1){
                        // hot, ice 클릭 시
                        fullrecipeFmBtnHot.setOnClickListener {
                            itemClickListener.onHotIceClick(item.recipeName, "HOT")
                        }
                        fullrecipeFmBtnIce.setOnClickListener {
                            itemClickListener.onHotIceClick(item.recipeName, "ICE")
                        }
                    }
                    else{
                        if(item.type.equals("ICE")){
                            fullrecipeFmBtnHot.visibility = View.GONE
                            fullrecipeFmBtnIce.visibility = View.VISIBLE
                        }
                        else if(item.type.equals("HOT")){
                            fullrecipeFmBtnHot.visibility = View.VISIBLE
                            fullrecipeFmBtnIce.visibility = View.GONE
                        }
                    }

                    if (item.type == "ICE") {
                        fullrecipeFmBtngroup.check(fullrecipeFmBtnIce.id)
                    } else {
                        fullrecipeFmBtngroup.check(fullrecipeFmBtnHot.id)
                    }

                    if(fullrecipeFmBtngroup.checkedButtonId == fullrecipeFmBtnIce.id){
                        fullrecipeFmBtnIce.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
                        fullrecipeFmBtnIce.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                        fullrecipeFmBtnHot.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                        fullrecipeFmBtnHot.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                    }

                    if(fullrecipeFmBtngroup.checkedButtonId == fullrecipeFmBtnHot.id){
                        fullrecipeFmBtnIce.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                        fullrecipeFmBtnIce.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                        fullrecipeFmBtnHot.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
                        fullrecipeFmBtnHot.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    }
                }
                // OrderRecipeFragment에서 온 경우
                else if (whereAmICame == 2) {
                    fullrecipeFmFlBtnHeart.visibility = View.GONE
                    fullrecipeFmBtngroup.visibility = View.GONE
                    fullrecipeFmBtnIce.visibility = View.GONE
                    fullrecipeFmBtnHot.visibility = View.GONE
                    fullrecipeFmTvNote.visibility = View.VISIBLE
                    textView11.visibility = View.VISIBLE

                    if (customList.isNotEmpty()) {
                        val custom = customList[position]
                        fullrecipeFmTvNote.setText(custom)
                    }
                }
            }
        }
    }


    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onHeartClick(recipeName: String, isFavorite:Boolean)
        fun onHotIceClick(recipeName: String, type:String)
        fun onRecipeStepClick(recipe :Recipe, nowISeeStep:Int)
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
        holder.bindInfo(position)
    }

    override fun getItemCount() = recipeList.size
} 