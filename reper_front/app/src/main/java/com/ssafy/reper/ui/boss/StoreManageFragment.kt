package com.ssafy.reper.ui.boss

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LayoutAnimationController
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.data.remote.FcmService
import com.ssafy.reper.databinding.FragmentStoreManageBinding
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.MyFirebaseMessagingService
import com.ssafy.reper.ui.boss.adpater.StoreAdapter

private const val TAG = "StoreManageFragment"

class StoreManageFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentStoreManageBinding? = null
    private val binding get() = _binding!!
    private lateinit var storeAdapter: StoreAdapter
    private val bossViewModel: BossViewModel by activityViewModels()
    private val fcmViewModel: FcmViewModel by activityViewModels()
    val sharedPreferencesUtil: SharedPreferencesUtil by lazy {
        SharedPreferencesUtil(requireContext().applicationContext)
    }

    private var storeAddDialog: AlertDialog? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreManageBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.storeFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.storeFgAddTv.setOnClickListener {
            showStoreAddDialog()
        }

        initAdapter()

        bossViewModel.myStoreList.observe(viewLifecycleOwner) { newStoreList ->
            storeAdapter.updateData(newStoreList)
        }

      if(bossViewModel.myStoreList.value == null || bossViewModel.myStoreList.value!!.isEmpty()){
          binding.storeFgRV.visibility =View.GONE
          binding.nothingStore.visibility = View.VISIBLE
      }else{
          binding.storeFgRV.visibility =View.VISIBLE
          binding.nothingStore.visibility = View.GONE
      }

    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }

    private fun showStoreAddDialog() {
        if (!isAdded) return
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_store_add)
        dialog.findViewById<View>(R.id.store_add_content).visibility = View.GONE
        dialog.findViewById<ConstraintLayout>(R.id.store_add_btn_positive).apply {
            isEnabled =false
            alpha = 0.4f
        }





        val editText = dialog.findViewById<EditText>(R.id.storeAddET)
        val addButton = dialog.findViewById<TextView>(R.id.add_btn)

        addButton.setOnClickListener {
            val storeName = editText.text.toString().trim()//가게 이름의 공백 삭제

            if (storeName.isNotEmpty()) {//가게이름의 존재여부 확인
                Log.d(TAG, "showStoreAddDialog: $storeName")
                dialog.findViewById<View>(R.id.store_add_content).visibility = View.VISIBLE
                dialog.findViewById<TextView>(R.id.add_store_name).text = storeName
                dialog.findViewById<ConstraintLayout>(R.id.store_add_btn_positive).apply {
                    isEnabled =true
                    alpha = 1.0f
                }



                dialog.findViewById<View>(R.id.store_add_btn_positive).setOnClickListener {
                    bossViewModel.addStore(storeName, sharedPreferencesUtil.getUser().userId!!.toInt())
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "가게 등록 완료", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "showStoreAddDialog: 가게명이 입력되지 않음")
                Toast.makeText(requireContext(), "가게명을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.findViewById<View>(R.id.store_add_btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.store_add_btn_positive).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun initAdapter() {
        storeAdapter = StoreAdapter(emptyList(), object : StoreAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                storeAdapter.storeList[position].storeName?.let {
                    storeAdapter.storeList[position].storeId?.let { it1 ->
                        showDialog(it, it1)
                    }
                }
            }
        })

        binding.storeFgRV.layoutManager = LinearLayoutManager(requireContext())

        // 애니메이션 설정 추가
        val animationSet = AnimationSet(true).apply {
            val translateAnim = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f
            ).apply {
                duration = 500
            }

            val alphaAnim = AlphaAnimation(0f, 1f).apply {
                duration = 500
            }

            addAnimation(translateAnim)
            addAnimation(alphaAnim)
        }

        binding.storeFgRV.layoutAnimation = LayoutAnimationController(animationSet).apply {
            delay = 0.1f
            order = LayoutAnimationController.ORDER_NORMAL
        }

        binding.storeFgRV.adapter = storeAdapter

        // ViewModel 초기화 및 LiveData 관찰
        bossViewModel.myStoreList.observe(viewLifecycleOwner) { newStoreList ->
            Log.d(TAG, "Updated store list: $newStoreList")
            if (newStoreList.isNullOrEmpty()) {
                binding.storeFgRV.visibility = View.GONE
                binding.nothingStore.visibility = View.VISIBLE
            } else {
                binding.storeFgRV.visibility = View.VISIBLE
                binding.nothingStore.visibility = View.GONE
                storeAdapter.updateData(newStoreList)
                // 데이터가 업데이트될 때마다 애니메이션 재실행
                binding.storeFgRV.scheduleLayoutAnimation()
            }
        }
    }


    private fun showDialog(storeName: String, deleteStoreId: Int) {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete)

        // 텍스트를 변경하려는 TextView 찾기
        val nameTextView = dialog.findViewById<TextView>(R.id.dialog_delete_bold_tv)
        val middleTV = dialog.findViewById<TextView>(R.id.dialog_delete_rle_tv)

        // 텍스트 변경
        nameTextView.text = "${storeName}"
        middleTV.text = "의 정보를"


        dialog.findViewById<View>(R.id.dialog_delete_cancle_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.dialog_delete_delete_btn).setOnClickListener {
            bossViewModel.deleteStore(deleteStoreId, sharedPreferencesUtil.getUser().userId!!.toInt())
            Log.d(TAG, "showDialog: $deleteStoreId")
            dialog.dismiss()
            Toast.makeText(requireContext(), "가게 삭제 완료", Toast.LENGTH_SHORT).show()
            fcmViewModel.deleteStoreToken(deleteStoreId)
            fcmViewModel.deleteStoreToken(deleteStoreId)
        }
        dialog.show()
    }

    override fun onDestroyView() {
        storeAddDialog?.dismiss()  // 프래그먼트 종료 시 다이얼로그 닫기
        storeAddDialog = null
        super.onDestroyView()

    }
}