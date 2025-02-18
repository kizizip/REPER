import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.FavoriteRecipe
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.data.dto.RecipeStep
import com.ssafy.reper.data.dto.User
import com.ssafy.reper.data.dto.UserInfo
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.storeService
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.recipeService
import com.ssafy.reper.data.remote.RetrofitUtil.Companion.userService
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val TAG = "MainActivityViewModel_정언"
class MainActivityViewModel(application: Application) :  AndroidViewModel(application) {
        private val _isDataReady = MutableLiveData<Boolean>()
    val isDataReady: LiveData<Boolean> = _isDataReady

    private val _order = MutableLiveData<Order?>(null)
    val order : LiveData<Order?> = _order

    private val _selectedRecipeList = MutableLiveData<MutableList<Recipe>?>(null)
    val selectedRecipeList: LiveData<MutableList<Recipe>?> = _selectedRecipeList

    private val _orderRecipeList = MutableLiveData<MutableList<Recipe>?>(null)
    val orderRecipeList: LiveData<MutableList<Recipe>?> = _orderRecipeList

    private val _nowISeeStep = MutableLiveData<Int?>(null)
    val nowISeeStep: LiveData<Int?> = _nowISeeStep

    private val _nowISeeRecipe = MutableLiveData<Int?>(null)
    val nowISeeRecipe: LiveData<Int?> = _nowISeeRecipe

    private val _recipeSteps = MutableLiveData<MutableList<RecipeStep>?>(null)
    val recipeSteps: LiveData<MutableList<RecipeStep>?> = _recipeSteps

    private val _isEmployee = MutableLiveData<Boolean?>(null)
    val isEmployee: LiveData<Boolean?> = _isEmployee

    private val _favoriteRecipeList =
        MutableLiveData<MutableList<FavoriteRecipe>>(mutableListOf())
    val favoriteRecipeList: LiveData<MutableList<FavoriteRecipe>>
        get() = _favoriteRecipeList

    private val _userInfo = MutableLiveData<User?>(null)
    val userInfo: LiveData<User?> = _userInfo

    private val _nowTab = MutableLiveData<Int>(1)
    val nowTab:LiveData<Int> = _nowTab

    private val _recipeList = MutableLiveData<MutableList<Recipe>>(mutableListOf())
    val recipeList: LiveData<MutableList<Recipe>> = _recipeList

    fun getRecipeList(){
        viewModelScope.launch {
            try{
                val list = recipeService.getAllRecipes(ApplicationClass.sharedPreferencesUtil.getStoreId())
                _recipeList.value = list
            }catch (e:Exception) {
                Log.d(TAG, "getRecipeList: ${e}")
            }
        }
    }

    fun setNowTab(tab : Int){
        _nowTab.value = tab
    }

    fun setOrderRecipeList(recipeList: MutableList<Recipe>){
        _orderRecipeList.value = recipeList
    }

    fun setUserInfo(userId: Int){
        viewModelScope.launch {
            var data: User
            try {
                data = userService.getUserInfo(userId)
                _userInfo.value = data
            }
            catch (e:Exception){
                Log.d(TAG, "getUserInfo: ${e}")
            }
        }
    }

    fun setLikeRecipes(list: MutableList<FavoriteRecipe>){
        viewModelScope.launch {
            _favoriteRecipeList.value = list
        }
    }

    fun getLikeRecipes(storeId:Int, userId:Int){
        viewModelScope.launch {
            try {
                val list = recipeService.getLikeRecipes(storeId, userId)
                _favoriteRecipeList.value = list
            }
            catch (e: HttpException){
                Log.d(TAG, "getLikeRecipes :error: ${e.response()?.errorBody().toString()}")
            }
        }
    }

    fun setSelectedRecipeGoToStepRecipe(recipeList:MutableList<Recipe>, nowISeeStep:Int){
        viewModelScope.launch {
            try {
                _selectedRecipeList.value = recipeList
                Log.d(TAG, "setSelectedRecipes: ${selectedRecipeList.value}")

                _nowISeeRecipe.value = 0
                Log.d(TAG, "_nowIseeRecipe: ${nowISeeRecipe.value}")
                _nowISeeStep.value = nowISeeStep
                Log.d(TAG, "_nowISeeStep: ${nowISeeStep}")
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

    fun setSelectedRecipeGoToStepRecipeOnOrder(recipe:Recipe, thisNowISeeStep:Int){
        viewModelScope.launch {
            try {
                _nowISeeRecipe.value = selectedRecipeList.value!!.indexOf(recipe)
                Log.d(TAG, "_nowIseeRecipe: ${nowISeeRecipe.value}")
                _nowISeeStep.value = thisNowISeeStep
                Log.d(TAG, "_nowISeeStep: ${nowISeeStep}")
                _recipeSteps.value = selectedRecipeList.value!!.get(nowISeeRecipe.value!!).recipeSteps
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
                _isDataReady.value = true
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
                        _isEmployee.value = true
                    }
                }
            }catch (e:Exception){
                Log.e(TAG, "getIsEmployee: ")
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
    }
}