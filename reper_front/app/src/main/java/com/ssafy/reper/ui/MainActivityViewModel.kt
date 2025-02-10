import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel_정언"
class MainActivityViewModel(application: Application) :  AndroidViewModel(application) {
    private val recipeService = RetrofitUtil.recipeService

    private val _selectedRecipeList =
        MutableLiveData<MutableList<Recipe>>()
    val selectedRecipeList: LiveData<MutableList<Recipe>>
        get() = _selectedRecipeList

    fun getSelectedRecipes(storeId: Int, recipeIdList:MutableList<Int>){
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

    fun setNowISeeStep(idx: Int){
        _nowISeeStep.value = idx
    }

    private val _nowISeeRecipe = MutableLiveData<Int>()
    val nowISeeRecipe : LiveData<Int>
        get() = _nowISeeRecipe

    fun setNowISeeRecipe(idx: Int){
        _nowISeeRecipe.value = idx
    }

}