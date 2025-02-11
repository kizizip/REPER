import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeStep
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel_정언"
class MainActivityViewModel(application: Application) :  AndroidViewModel(application) {
    private val recipeService = RetrofitUtil.recipeService

    private val _selectedRecipeList =
        MutableLiveData<MutableList<Recipe>>()
    val selectedRecipeList: LiveData<MutableList<Recipe>>
        get() = _selectedRecipeList

    fun getSelectedRecipes(recipeIdList:MutableList<Int>){
        viewModelScope.launch {
            var list: MutableList<Recipe> = mutableListOf()
            try {
                for(id in recipeIdList){
                    list.add(recipeService.getRecipe(id))
                }
            }
            catch (e:Exception){
                Log.d(TAG, "error: ${e}")
                list = mutableListOf()
            }
            _selectedRecipeList.value = list
            setNowISeeRecipe(0)
            setNowISeeStep(-1)
        }
    }

    private val _order = MutableLiveData<Order>()
    val order : LiveData<Order>
        get() = _order

    fun setOrder(order: Order){
        _order.value = order
    }

    private val _nowISeeStep = MutableLiveData<Int>()
    val nowISeeStep : LiveData<Int>
        get() = _nowISeeStep

    fun setNowISeeStep(stepIdx: Int){
        _nowISeeStep.value = stepIdx
    }

    private val _nowISeeRecipe = MutableLiveData<Int>()
    val nowISeeRecipe : LiveData<Int>
        get() = _nowISeeRecipe

    fun setNowISeeRecipe(recipeIdx: Int){
        _nowISeeRecipe.value = recipeIdx
        setRecipeSteps(recipeIdx)
    }

    private val _recipeSteps = MutableLiveData<MutableList<RecipeStep>>()
    val recipeSteps : LiveData<MutableList<RecipeStep>>
        get() = _recipeSteps

    fun setRecipeSteps(recipeIdx: Int){
         _recipeSteps.value = _selectedRecipeList.value?.get(recipeIdx)?.recipeSteps
    }
}