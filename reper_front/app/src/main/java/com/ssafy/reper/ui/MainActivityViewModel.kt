import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeStep
import com.ssafy.reper.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel_정언"
class MainActivityViewModel(application: Application) :  AndroidViewModel(application) {
    private val storeService = RetrofitUtil.storeService

    private val _isDataReady = MutableLiveData<Boolean>()
    val isDataReady: LiveData<Boolean> = _isDataReady

    private val _order = MutableLiveData<Order?>(null)
    val order : LiveData<Order?> = _order

    private val _selectedRecipeList = MutableLiveData<MutableList<Recipe>?>(null)
    val selectedRecipeList: LiveData<MutableList<Recipe>?> = _selectedRecipeList

    private val _nowISeeStep = MutableLiveData<Int?>(null)
    val nowISeeStep: LiveData<Int?> = _nowISeeStep

    private val _nowISeeRecipe = MutableLiveData<Int?>(null)
    val nowISeeRecipe: LiveData<Int?> = _nowISeeRecipe

    private val _recipeSteps = MutableLiveData<MutableList<RecipeStep>?>(null)
    val recipeSteps: LiveData<MutableList<RecipeStep>?> = _recipeSteps

    private val _isEmployee = MutableLiveData<Boolean?>(null)
    val isEmployee: LiveData<Boolean?> = _isEmployee

    fun setSelectedRecipes(recipeList:MutableList<Recipe>){
        viewModelScope.launch {
            try {
                _selectedRecipeList.value = recipeList
                Log.d(TAG, "setSelectedRecipes: ${selectedRecipeList.value}")

                _nowISeeRecipe.value = 0
                Log.d(TAG, "_nowIseeRecipe: ${nowISeeRecipe.value}")
                _nowISeeStep.value = -1
                Log.d(TAG, "_nowISeeStep: ${nowISeeStep.value}")
                _recipeSteps.value = recipeList.get(0).recipeSteps
                Log.d(TAG, "_recipeSteps: ${recipeSteps.value}")
                _isDataReady.postValue(true)
                Log.d(TAG, "_isDataReady: ${isDataReady.value}")
            }
            catch (e:Exception){
                Log.e(TAG, "Error fetching recipes: ${e.message}", e)
                _isDataReady.value = false
            }
        }
    }

    fun setOrder(order: Order){
        _order.value = order
        Log.d(TAG, "setOrder: ${_order.value}")
    }

    fun setNowISeeStep(stepIdx: Int){
        _nowISeeStep.value = stepIdx
        Log.d(TAG, "setNowISeeStep: ${nowISeeStep.value}")
    }

    fun setNowISeeRecipe(recipeIdx: Int){
        _nowISeeRecipe.value = recipeIdx
        Log.d(TAG, "setNowISeeRecipe: ${nowISeeRecipe.value}")
        setRecipeSteps(recipeIdx)
    }

    fun setRecipeSteps(recipeIdx: Int){
         _recipeSteps.value = _selectedRecipeList.value?.get(recipeIdx)?.recipeSteps
        Log.d(TAG, "setRecipeSteps: ${recipeSteps.value}")
    }

    fun getIsEmployee(userId: Int){
        viewModelScope.launch {
            try {
                val list = storeService.getStoreListByEmployeeId(userId)
                for(store in list){
                    if(store.storeId == ApplicationClass.sharedPreferencesUtil.getStoreId()){
                        _isEmployee.postValue(true)
                        return@launch
                    }
                }
            }catch (e:Exception){
                Log.e(TAG, "getIsEmployee: ", )
            }
        }
    }

    fun clearData(){
        _selectedRecipeList.value = null
        _nowISeeRecipe.value = null
        _nowISeeStep.value = null
        _recipeSteps.value = null
        _isDataReady.value = false
        _order.value = null
        _isEmployee.value = false
    }
}