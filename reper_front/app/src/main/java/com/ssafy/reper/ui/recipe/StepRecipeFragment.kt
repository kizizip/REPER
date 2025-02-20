package com.ssafy.reper.ui.recipe

import MainActivityViewModel
import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass
import com.ssafy.reper.data.dto.Order
import com.ssafy.reper.data.dto.OrderDetail
import com.ssafy.reper.data.dto.Recipe
import com.ssafy.reper.databinding.FragmentStepRecipeBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.util.ViewModelSingleton
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.content.pm.PackageManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.ui.recipe.adapter.RecipeIngredientsAdapter
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.ui.recipe.adapter.StepLandOrderListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import androidx.lifecycle.whenResumed

private const val TAG = "StepRecipeFragment_정언"
class StepRecipeFragment : Fragment() {
    // 1. AllRecipe에서 넘어오면 세로만 가능하게 한다.
    // 2. OrderRecipe에서 넘어오면 가로 가능

    // 주문 리스트 recyclerView Adapter
    private lateinit var ingredientsAdapter: RecipeIngredientsAdapter
    private lateinit var stepLandOrderListAdapter: StepLandOrderListAdapter

    var nowRecipeIdx = 0
    var nowStepIdx = 0
    var totalSteps = 0
    var totalRecipes =  0
    lateinit var order : Order
    lateinit var orderDetails: MutableList<OrderDetail>
    lateinit var selectedRecipeList : MutableList<Recipe>

    private val mainViewModel: MainActivityViewModel by lazy { ViewModelSingleton.mainActivityViewModel }

    // Bundle 변수
    var whereAmICame = -1
    lateinit var recipeIdList: MutableList<Int>

    // 모션인식을 위한 카메라 제공자, 실행자 초기화
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var gestureRecognizer: GestureRecognizer

    private lateinit var mainActivity: MainActivity

    private var _stepRecipeBinding : FragmentStepRecipeBinding? = null
    private val stepRecipeBinding get() =_stepRecipeBinding!!

    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = false
    private var isFragmentActive = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _stepRecipeBinding = FragmentStepRecipeBinding.inflate(inflater, container, false)
        return stepRecipeBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 내가 어느 Fragment에서 왔는 지 Flag 처리
        whereAmICame = arguments?.getInt("whereAmICame") ?: -1 // 1 : AllRecipeFragment // 2 : OrderRecipeFragment // 3 : FullRecipeFragment // 4 : OrderRecipe Full

        // 전역변수 관리
        mainViewModel.nowISeeRecipe.observe(viewLifecycleOwner){
            if (it != null) {
                nowRecipeIdx = it
            }
        }
        mainViewModel.nowISeeStep.observe(viewLifecycleOwner){
            if (it != null) {
                nowStepIdx = it
            }
        }
        mainViewModel.recipeSteps.observe(viewLifecycleOwner){
            if (it != null) {
                totalSteps = it.count()
            }
        }
        mainViewModel.isDataReady.observe(viewLifecycleOwner){
            if(it){
                selectedRecipeList = mainViewModel.selectedRecipeList.value!!
                totalRecipes = selectedRecipeList.count()

                // 공통 이벤트 처리
                initEvent()

                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) { // 가로모드 처리
                    eventLand()
                } else { // 세로 모드 처리
                    eventPortrait()
                }
            }
        }

        if(whereAmICame == 1 || whereAmICame == 3 ){ // ALlRecipeFragment에서 옴
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
        }
        else if(whereAmICame == 2 || whereAmICame == 4) { // OrderRecipeFragment에서 옴
            mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
            order = mainViewModel.order.value!!
            orderDetails = order.orderDetails
        }

        // 카메라 권한 체크 시작
        checkCameraPermission()
    }
    //캡쳐방지 코드입니다! 메시지 내용은 수정불가능,, 핸드폰내에 저장된 메시지가 뜨는 거라고 하네요
    override fun onResume() {
        super.onResume()
        isFragmentActive = true
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        mainActivity.hideBottomNavigation()
        // 음성 인식 재시작
        if (!isListening && isFragmentActive) {
            startListening()
        }
    }
    override fun onPause() {
        super.onPause()
        isFragmentActive = false
        try {
            // 일시 정지 시에도 카메라 리소스 해제
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        } catch (e: Exception) {
            Log.e(TAG, "Error unbinding camera use cases", e)
        }
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        // 음성 인식 일시 중지
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            isListening = false
        }
    }
    override fun onStop() {
        super.onStop()
        isFragmentActive = false
        try {
            // Fragment가 화면에서 사라질 때도 카메라 리소스 해제
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        } catch (e: Exception) {
            Log.e(TAG, "Error unbinding camera use cases", e)
        }
        // 음성 인식 중지
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            isListening = false
        }
    }
    override fun onDestroyView() {
        try {
            // 카메라 실행 중지
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            
            // 실행자 종료
            if (!cameraExecutor.isShutdown) {
                cameraExecutor.shutdown()
            }
            
            // MediaPipe 리소스 해제
            if (::gestureRecognizer.isInitialized) {
                gestureRecognizer.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up resources", e)
        }
        
        super.onDestroyView()
        _stepRecipeBinding = null
        
        // 음성 인식기 해제
        if (::speechRecognizer.isInitialized) {
            isListening = false
            speechRecognizer.destroy()
        }
    }

    fun initEvent(){
        // 돌아가기 버튼
        stepRecipeBinding.steprecipeFmBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 다음버튼이 눌릴 때.
        stepRecipeBinding.steprecipeFmBtnRight.setOnClickListener {
            nextEvent()
        }

        // 이전 버튼이 눌릴 때.
        stepRecipeBinding.steprecipeFmBtnLeft.setOnClickListener {
            prevEvent()
        }

        // 이전, 다음 버튼 Visible 처리 (화면이 회전되어 재구성될 가능성..)
        if(nowRecipeIdx == 0 && nowStepIdx == -1){
            stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.GONE
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE
        }
        else if(nowRecipeIdx >= totalRecipes - 1 && nowStepIdx >= totalSteps - 1){
            stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.GONE
        }
        else{
            stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE
        }

        // 추가사항 처리
        if(whereAmICame == 1 || whereAmICame == 3){
            stepRecipeBinding.constraintLayout2.visibility = View.GONE // 추가사항 안보이게
        }else{
            stepRecipeBinding.constraintLayout2.visibility = View.VISIBLE // 추가사항 보이게
            stepRecipeBinding.steprecipeFmTvCustom.setText(orderDetails[nowRecipeIdx].customerRequest)
        }
    }
    // 세로 화면일 때 이벤트 처리
    fun eventPortrait(){
        stepRecipeBinding.steprecipeFmTvUser?.setText("이용자 : ${mainViewModel.userInfo.value!!.email}")
        stepRecipeBinding.steprecipeFmTvMenuName?.text = "${selectedRecipeList.get(nowRecipeIdx).recipeName} ${selectedRecipeList.get(nowRecipeIdx).type}"

        stepRecipeBinding.steprecipeFmTvMenuName?.text =
            "${selectedRecipeList.get(nowRecipeIdx).recipeName} ${selectedRecipeList.get(nowRecipeIdx).type}"

        if(nowStepIdx == -1){ // 재료를 보여줘야해!
            showIngredient(nowRecipeIdx, true)
        }
        else{ // 레시피를 보여줘야해!
            showOneStepRecipe(nowStepIdx)
        }
    }
    // 가로 화면일 때 이벤트 처리
    fun eventLand(){
        stepRecipeBinding.steprecipeFmLandTvUser?.setText("이용자 : ${mainViewModel.userInfo.value!!.email}")

        if(whereAmICame == 1 || whereAmICame == 3){
            stepRecipeBinding.constraintLayout4?.visibility = View.GONE // 인덱스 안보이게
        }
        else if(whereAmICame == 2 || whereAmICame == 4){
            stepRecipeBinding.constraintLayout4?.visibility = View.VISIBLE // 인덱스 보이게
            stepRecipeBinding.steprecipeFmTvIndex.let{ it->
                var quantities = 0
                for(item in orderDetails){
                    quantities += item.quantity
                }
                if(order.takeout){
                    it?.setText("포장 ${quantities}개")
                    stepRecipeBinding.imageView?.setImageResource(R.drawable.steprecipe_land_red_index)
                }
                else{
                    it?.setText("매장 ${quantities}개")
                    stepRecipeBinding.imageView?.setImageResource(R.drawable.steprecipe_land_green_index)
                }
            }
        }

        if(nowStepIdx == -1){ // 재료를 보여줘야해!
            showIngredient(nowRecipeIdx, false)
        }
        else{ // 레시피를 보여줘야해!
            showOneStepRecipe(nowStepIdx)
        }
    }
    // 다음 클릭시
    // 1. 내가 지금 재료라서 스탭을 보여줘야 할 때
    // 2. 내가 지금 스텝인데, 다음 스텝이 있을 때
    // 3. 내가 지금 스텝인데, 마지막 스텝이라 다음 레시피의 재료를 보여줘야할 때
    // 4. 내가 지금 스텝인데, 마지막 레시피의 마지막 스탭일때
    fun nextEvent() {
        val orientation = resources.configuration.orientation
        stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.VISIBLE

        mainViewModel.setNowISeeStep(nowStepIdx + 1)
//        Log.d(TAG, "다음을 눌렀습니다. ${nowRecipeIdx}/${totalRecipes}, ${nowStepIdx}/${totalSteps}")

        // 마지막 레시피의 마지막 스텝인 경우 → 버튼 비활성화
        if(nowRecipeIdx >= totalRecipes - 1 && nowStepIdx >= totalSteps - 1){
//            Log.d(TAG, "마지막 레시피의 마지막 스텝 도달 ${nowRecipeIdx}/${totalRecipes}, ${nowStepIdx}/${totalSteps}")
            stepRecipeBinding.steprecipeFmBtnRight.visibility = View.GONE
            showOneStepRecipe(nowStepIdx)
            return
        }

        when {
            // 다음 스텝이 존재하는 경우 → 다음 스텝으로 이동
            nowStepIdx == -1 || nowStepIdx < totalSteps -> {
                showOneStepRecipe(nowStepIdx)
            }

            nowStepIdx >= totalSteps && nowRecipeIdx < totalRecipes - 1-> {
//                Log.d(TAG,"다음 레시피 보여주기! ${nowRecipeIdx}/${totalRecipes}, ${nowStepIdx}/${totalSteps}")
                mainViewModel.setNowISeeRecipe(nowRecipeIdx + 1)
                mainViewModel.setRecipeSteps(nowRecipeIdx)
                mainViewModel.setNowISeeStep(-1)
                if (orientation == Configuration.ORIENTATION_LANDSCAPE){ // 가로야?
                    showIngredient(nowRecipeIdx, false)
                }
                else{
                    showIngredient(nowRecipeIdx, true)
                }
            }
        }
    }
    // 이전 클릭시
    // 1. 내가 지금 재료라서 이전 레시피의 마지막 스탭을 보여줘야 할 때
    // 2. 내가 지금 스텝인데, 이전 스텝이 있을 때
    // 3. 내가 지금 스텝인데, 첫번쨰 스탭이라 현재 레시피의 재료를 보여줘야할 때
    // 4. 내가 재료인데, 첫번쨰 레시피의 재료일때
    fun prevEvent(){
        val orientation = resources.configuration.orientation

        stepRecipeBinding.steprecipeFmBtnRight.visibility = View.VISIBLE

        mainViewModel.setNowISeeStep(nowStepIdx - 1)
//        Log.d(TAG,"이전을 눌렀습니다. ${mainViewModel.nowISeeRecipe.value} / ${mainViewModel.nowISeeStep.value}")
        when {
            nowStepIdx < 0 && nowRecipeIdx <= 0 ->{
                stepRecipeBinding.steprecipeFmBtnLeft.visibility = View.GONE
                if (orientation == Configuration.ORIENTATION_LANDSCAPE){ // 가로야?
                    showIngredient(nowRecipeIdx, false)
                }
                else{
                    showIngredient(nowRecipeIdx, true)
                }
            }

            // 이전 스텝이 존재하는 경우 → 이전 스텝으로 이동
            nowStepIdx >= 0-> {
                showOneStepRecipe(nowStepIdx)
            }

            nowStepIdx == -1 -> {
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE){ // 가로야?
                    showIngredient(nowRecipeIdx, false)
                }
                else{
                    showIngredient(nowRecipeIdx, true)
                }
            }

            nowStepIdx < 0 && nowRecipeIdx > 0-> {
                mainViewModel.setNowISeeRecipe(nowRecipeIdx - 1)
                mainViewModel.setRecipeSteps(nowRecipeIdx)
                mainViewModel.setNowISeeStep(totalSteps - 1)
                showOneStepRecipe(nowStepIdx)
            }

            else ->{
                Log.d(TAG, "prevEvent: 엥 이게 무슨 조건이지? $nowStepIdx / $nowRecipeIdx")
            }
        }
    }
    // 재료 보이게
    fun showIngredient(recipeIdx:Int, isPortrait:Boolean){
//        Log.d(TAG, "showIngredient: ")
        stepRecipeBinding.lottieAnimationView.visibility = View.GONE
        stepRecipeBinding.steprecipeFmTvStep?.visibility = View.GONE
        stepRecipeBinding.steprecipeFmTvMenuName?.setText("${selectedRecipeList.get(recipeIdx).recipeName} ${selectedRecipeList.get(recipeIdx).type}")
        stepRecipeBinding.steprecipeFmLandTvRecipe?.setText("${selectedRecipeList.get(recipeIdx).recipeName} ${selectedRecipeList.get(recipeIdx).type}")

        if(whereAmICame == 2 || whereAmICame == 4){
            stepRecipeBinding.steprecipeFmTvCustom.setText("${orderDetails.get(recipeIdx).customerRequest}")
        }

        stepRecipeBinding.steprecipeFmRvIngredients.visibility = View.VISIBLE
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) { // 가로모드 처리
            initStepLandOrderListAdapter()
        }

        initIngredientsAdapter(recipeIdx, isPortrait)
    }
    // 레시피 보이게
    fun showOneStepRecipe(stepIdx:Int){
        val recipeSteps = mainViewModel.recipeSteps.value!!

        stepRecipeBinding.steprecipeFmTvMenuName?.setText("${selectedRecipeList.get(nowRecipeIdx).recipeName} ${selectedRecipeList.get(nowRecipeIdx).type}")

        stepRecipeBinding.steprecipeFmRvIngredients.visibility = View.GONE
        if(whereAmICame == 2 || whereAmICame == 4){
            stepRecipeBinding.steprecipeFmTvCustom.setText("${orderDetails.get(nowRecipeIdx).customerRequest}")
        }

        // 로티
        stepRecipeBinding.lottieAnimationView.visibility = View.VISIBLE
        if(recipeSteps.get(stepIdx)?.animationUrl == null){
            stepRecipeBinding.lottieAnimationView.setAnimationFromUrl("https://cdn.lottielab.com/l/87fJhuooafErS0.json")
        }else{
            stepRecipeBinding.lottieAnimationView.setAnimationFromUrl(recipeSteps.get(stepIdx)?.animationUrl)
        }
        // 레시피
        stepRecipeBinding.steprecipeFmTvStep?.visibility = View.VISIBLE
        stepRecipeBinding.steprecipeFmTvStep?.setText(recipeSteps?.get(stepIdx)?.instruction)
        stepRecipeBinding.steprecipeFmLandTvRecipe?.setText(recipeSteps?.get(stepIdx)?.instruction)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) { // 가로모드 처리
            initStepLandOrderListAdapter()
        }
    }
    // 재료 어뎁터 설정
    fun initIngredientsAdapter(recipeIdx:Int, isPortrait: Boolean) {
        val ingredients = mainViewModel.selectedRecipeList.value?.get(recipeIdx)?.ingredients!!

        ingredientsAdapter = RecipeIngredientsAdapter(ingredients, isPortrait)
        stepRecipeBinding.steprecipeFmRvIngredients.adapter = ingredientsAdapter
    }
    // 가로모드일 때 선택된 메뉴 리스트 어뎁터
    fun initStepLandOrderListAdapter(){
        stepLandOrderListAdapter = StepLandOrderListAdapter(mainViewModel.orderRecipeList.value!!, selectedRecipeList, selectedRecipeList.get(nowRecipeIdx).recipeId)
        stepRecipeBinding.steprecipeFmLandRvWhy?.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            adapter = stepLandOrderListAdapter
        }
    }

    // 화면 회전 감지
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val bundle = arguments
        findNavController().popBackStack(R.id.stepRecipeFragment, true)

        initEvent()
        // 가로모드 UI 업데이트
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            eventLand()
            findNavController().navigate(R.id.stepRecipeFragment, bundle)
        } else { // 세로모드 UI 업데이트
            eventPortrait()
            findNavController().navigate(R.id.stepRecipeFragment, bundle)
        }
    }

    // 모션인식을 위한 코드

    // 제스처 인식기 설정
    private fun setupGestureRecognizer() {
        Log.d(TAG, "setupGestureRecognizer: ")

        // 제스처 인식 모델 로딩
        val options = GestureRecognizer.GestureRecognizerOptions.builder()
            .setBaseOptions(
                BaseOptions.builder()
                    .setModelAssetPath("gesture_recognizer.task") // 모델 경로 설정
                    .build()
            )
            .build()

        gestureRecognizer = GestureRecognizer.createFromOptions(requireContext(), options)
    }

    // 카메라 설정
    private fun setupCamera() {
        Log.d(TAG, "setupCamera: ")

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // 이미지 분석기 설정
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer(gestureRecognizer))
                }

            try {
                // 모든 카메라 리소스를 해제하고 새로 바인딩
                cameraProvider.unbindAll()
                // preview 제거하고 imageAnalyzer만 사용
                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer)
            } catch (e: Exception) {
                Log.e(TAG, "Error binding camera", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // 이미지 분석 클래스
    private inner class ImageAnalyzer(
        private val recognizer: GestureRecognizer
    ) : ImageAnalysis.Analyzer {
        private var gestureStartTime: Long = 0
        private var currentGesture: String? = null
        private val GESTURE_DURATION_THRESHOLD = 500 // 0.5초

        override fun analyze(imageProxy: ImageProxy) {
            // 이미지 회전 처리를 위한 Matrix 생성
            val matrix = android.graphics.Matrix().apply {
                // 카메라에서 받은 이미지를 회전
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                // 전면 카메라이므로 이미지 좌우 반전
                postScale(
                    -1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat()
                )
            }

            // ImageProxy를 Bitmap으로 변환
            val bitmap = imageProxy.toBitmap()

            // 회전된 비트맵 생성
            val rotatedBitmap = android.graphics.Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

            // 회전된 비트맵으로 MPImage 생성
            val mpImage = BitmapImageBuilder(rotatedBitmap).build()

            // 제스처 인식
            val result: GestureRecognizerResult = recognizer.recognize(mpImage)

            if (result.gestures().isNotEmpty()) {
                val gesture = result.gestures().first().firstOrNull()

                if (gesture != null) {
                    val gestureName = gesture.categoryName()
                    Log.d(TAG, "analyze: $gestureName")

                    // 새로운 제스처 감지시
                    if (currentGesture != gestureName) {
                        currentGesture = gestureName
                        gestureStartTime = System.currentTimeMillis()
                    } else {
                        // 같은 제스처 지속시 -> 지속시간 체크
                        val gestureDuration = System.currentTimeMillis() - gestureStartTime
                        if (gestureDuration >= GESTURE_DURATION_THRESHOLD) {
                            // 최소시간 이상 지속된 경우에만 이동
                            when (gestureName) {
                                "Thumb_Up" -> {
                                    requireActivity().runOnUiThread {
                                        if (isAdded) {
                                            if (!(nowRecipeIdx >= totalRecipes - 1 && nowStepIdx >= totalSteps - 1)) {
                                                nextEvent()
                                                // 제스처 초기화
                                                currentGesture = null
                                                gestureStartTime = 0
                                            }
                                        }
                                    }
                                }
                                "Closed_Fist" -> {
                                    if (isAdded) {
                                        requireActivity().runOnUiThread {
                                            if (!(nowRecipeIdx == 0 && nowStepIdx == -1)) {
                                                prevEvent()
                                                // 제스처 초기화
                                                currentGesture = null
                                                gestureStartTime = 0
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    Log.d(TAG, "알 수 없는 제스처")
                                }
                            }
                        }
                    }
                }
            } else {
                // 제스처가 감지되지 않으면 초기화
                currentGesture = null
                gestureStartTime = 0
            }

            imageProxy.close()
            bitmap.recycle()
            rotatedBitmap.recycle()
        }
    }

    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                Log.d(TAG, "onReadyForSpeech: 음성 인식 준비됨")
            }

            override fun onResults(results: Bundle?) {
                if (!isFragmentActive) return  // Fragment가 비활성 상태면 처리하지 않음
                
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.get(0)?.let { result ->
                    Log.d(TAG, "음성 인식 결과: $result")
                    when {
                        result.contains("다음") -> {
                            if (!(nowRecipeIdx >= totalRecipes - 1 && nowStepIdx >= totalSteps - 1)) {
                                nextEvent()
                            }
                        }
                        result.contains("이전") -> {
                            if (!(nowRecipeIdx == 0 && nowStepIdx == -1)) {
                                prevEvent()
                            }
                        }
                    }
                }
                
                isListening = false
                // Fragment가 활성 상태일 때만 재시작
                if (isFragmentActive) {
                    startListening()
                }
            }

            override fun onError(error: Int) {
                if (!isFragmentActive) return  // Fragment가 비활성 상태면 처리하지 않음
                
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                    SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 없음"
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
                    SpeechRecognizer.ERROR_NO_MATCH -> "음성 인식 실패"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기 사용 중"
                    SpeechRecognizer.ERROR_SERVER -> "서버 에러"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 없음"
                    else -> "알 수 없는 에러"
                }
                Log.e(TAG, "Speech recognition error: $message")
                isListening = false
                
                // Fragment가 활성 상태일 때만 재시작
                if (isFragmentActive) {
                    startListening()
                }
            }

            // 다른 필수 메서드들
            override fun onBeginningOfSpeech() {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    // 음성 인식 시작 함수 수정
    private fun startListening() {
        if (!isListening && isFragmentActive) {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().packageName)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

                    // 음성 감도 설정
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 300L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                }
                
                speechRecognizer.startListening(intent)
                isListening = true
                Log.d(TAG, "음성 인식 시작")
            } catch (e: Exception) {
                Log.e(TAG, "음성 인식 시작 실패: ${e.message}")
                isListening = false
            }
        }
    }

    // 카메라 권한 확인 함수
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 카메라 권한이 있는 경우
            Log.d(TAG, "checkCameraPermission: 카메라 권한 있음")
            cameraExecutor = Executors.newSingleThreadExecutor()
            setupGestureRecognizer()
            setupCamera()
            // 카메라 권한 확인 후 마이크 권한 확인
            checkMicrophonePermission()
        } else {
            // 카메라 권한이 없는 경우 권한 요청
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    // 마이크 권한 확인 함수
    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 마이크 권한이 있는 경우
            Log.d(TAG, "checkMicrophonePermission: 마이크 권한 있음")
            initSpeechRecognizer()
            startListening()
        } else {
            // 마이크 권한이 없는 경우 권한 요청
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MICROPHONE_PERMISSION_REQUEST_CODE
            )
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 카메라 권한이 승인된 경우
                    Log.d(TAG, "onRequestPermissionsResult: 카메라 권한 승인됨")
                    cameraExecutor = Executors.newSingleThreadExecutor()
                    setupGestureRecognizer()
                    setupCamera()
                    // 카메라 권한 승인 후 마이크 권한 확인
                    checkMicrophonePermission()
                } else {
                    // 카메라 권한이 거부된 경우
                    Toast.makeText(
                        requireContext(),
                        "제스처 인식을 위해 카메라 권한이 필요합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // 카메라 권한이 거부되어도 마이크 권한 확인
                    checkMicrophonePermission()
                }
            }
            MICROPHONE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 마이크 권한이 승인된 경우
                    Log.d(TAG, "onRequestPermissionsResult: 마이크 권한 승인됨")
                    initSpeechRecognizer()
                    startListening()
                } else {
                    // 마이크 권한이 거부된 경우
                    Toast.makeText(
                        requireContext(),
                        "음성 인식을 위해 마이크 권한이 필요합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val MICROPHONE_PERMISSION_REQUEST_CODE = 1002
    }
}