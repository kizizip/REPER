package com.ssafy.reper.ui.recipe

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.ssafy.reper.data.dto.RecipeStep

class LottieViewModel : ViewModel(){
    val lottieCache = MutableLiveData<MutableMap<String, LottieComposition?>>()

    init {
        lottieCache.value = mutableMapOf()
    }

    fun preloadLottieAnimations(context: Context, recipeSteps: MutableList<RecipeStep>) {
        val cache = lottieCache.value ?: mutableMapOf()

        for (step in recipeSteps) {
            val animationUrl = step.animationUrl
            if (cache.containsKey(animationUrl)) continue

            LottieCompositionFactory.fromUrl(context, animationUrl).addListener { composition ->
                cache[animationUrl] = composition
                lottieCache.postValue(cache)
            }
        }
    }
}