package com.ssafy.reper.ui.recipe

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentAllRecipeBinding
import com.ssafy.reper.databinding.FragmentStepRecipeBinding
import com.ssafy.reper.ui.MainActivity

private const val TAG = "StepRecipeFragment_정언"
class StepRecipeFragment : Fragment() {

    lateinit var recipeIdList:ArrayList<Int>
    var whereAmICame = -1

    private lateinit var mainActivity: MainActivity

    private var _stepRecipeBinding : FragmentStepRecipeBinding? = null
    private val stepRecipeBinding get() =_stepRecipeBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // 화면 회전 잠금 해제
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
        // OrderFragment에서 bundle로 던진 orderId를 받음
        whereAmICame = arguments?.getInt("whereAmICame") ?: -1 // 1 : AllRecipeFragment
        recipeIdList = arguments?.getIntegerArrayList("recipeIdList") ?: arrayListOf(-1)

        initEvent()
    }
    //캡쳐방지 코드입니다! 메시지 내용은 수정불가능,, 핸드폰내에 저장된 메시지가 뜨는 거라고 하네요
    override fun onResume() {
        super.onResume()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        mainActivity.hideBottomNavigation()
    }
    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    override fun onDestroyView() {
        super.onDestroyView()
        _stepRecipeBinding = null
    }

    fun initEvent(){
        stepRecipeBinding.steprecipeFmBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}