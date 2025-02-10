package com.ssafy.reper.ui.mypage

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.UserInfo
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.databinding.FragmentAllRecipeBinding
import com.ssafy.reper.databinding.FragmentMyPageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.BossFragment
import com.ssafy.reper.ui.login.LoginActivity
import com.ssafy.reper.ui.mypage.adapter.StoreSearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.DividerItemDecoration

class MyPageFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    private var _myPageBinding: FragmentMyPageBinding? = null
    private val myPageBinding get() = _myPageBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _myPageBinding = FragmentMyPageBinding.inflate(inflater, container, false)
        return myPageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.showBottomNavigation()

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // 가게 이름 Spinner 설정
        val spinner = myPageBinding.mypageFmSp
        val userTypes = arrayOf("메가커피 구미 인동점", "이스터에그:이걸 발견하다니!")

        val adapter =
            ArrayAdapter(requireContext(), R.layout.mypage_spinner_item, userTypes).apply {
                setDropDownViewResource(R.layout.mypage_spinner_item)
            }

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = userTypes[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        initEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myPageBinding = null
    }

    fun initEvent() {
        myPageBinding.mypageFmBtnBossMenu.setOnClickListener {
            val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
            val userInfo = sharedPreferencesUtil.getUser()
            var selectedStoreId: Int? = null

            if (userInfo.role == "BOSS") {
                findNavController().navigate(R.id.bossFragment)
            } else {
                val dialog = Dialog(mainActivity)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.dialog_request_access)
                dialog.findViewById<EditText>(R.id.request_access_d_et)
                    .addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val searchText = s.toString()
                                    if (searchText.isNotEmpty()) {
                                        // 엘라스틱 서치
                                        val storeList =
                                            RetrofitUtil.storeService.searchAllStores(searchText)
                                        withContext(Dispatchers.Main) {
                                            val recyclerView =
                                                dialog.findViewById<RecyclerView>(R.id.request_access_d_rv)
                                            recyclerView.layoutManager =
                                                LinearLayoutManager(requireContext())
                                            recyclerView.addItemDecoration(
                                                DividerItemDecoration(
                                                    requireContext(),
                                                    DividerItemDecoration.VERTICAL
                                                )
                                            )

                                            // 어댑터 설정 및 클릭 리스너 추가
                                            val adapter = StoreSearchAdapter(storeList)
                                            adapter.setOnItemClickListener { store ->
                                                // 선택된 가게 이름을 TextView에 설정
                                                dialog.findViewById<TextView>(R.id.request_access_d_tv_store_name).text =
                                                    store.storeName
                                                selectedStoreId = store.storeId

                                                // 확인 메시지를 보여주는 레이아웃 표시
                                                dialog.findViewById<ConstraintLayout>(R.id.request_access_d_cl_tv).visibility =
                                                    View.VISIBLE
                                                
                                                // 확인 버튼 활성화
                                                dialog.findViewById<ConstraintLayout>(R.id.request_access_d_btn_positive).apply {
                                                    isEnabled = true
                                                    alpha = 1.0f  // 버튼 투명도를 원래대로 설정
                                                }
                                            }
                                            recyclerView.adapter = adapter
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            requireContext(),
                                            "검색 중 오류가 발생했습니다.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    })

                // 다이얼로그 초기화 시 확인 버튼 비활성화 (dialog.show() 전에 추가)
                dialog.findViewById<ConstraintLayout>(R.id.request_access_d_btn_positive).apply {
                    isEnabled = false
                    alpha = 0.5f  // 버튼이 비활성화되었음을 시각적으로 표시
                }

                // 취소 버튼 클릭 리스너 추가
                dialog.findViewById<ConstraintLayout>(R.id.request_access_d_btn_cancel)
                    .setOnClickListener {
                        dialog.dismiss()
                    }

                // 확인 버튼 클릭 리스너 추가
                dialog.findViewById<ConstraintLayout>(R.id.request_access_d_btn_positive)
                    .setOnClickListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val storeId = selectedStoreId
                                val userId = sharedPreferencesUtil.getUser().userId

                                RetrofitUtil.storeService.approveEmployee(
                                    storeId.toString(),
                                    userId.toString()
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "권한 요청 완료",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "권한 요청 중 오류가 발생했습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        dialog.dismiss()
                    }

                dialog.show()
            }

        }

        myPageBinding.mypageFmBtnLogout.setOnClickListener {
            val dialog = Dialog(mainActivity)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_logout)
            dialog.findViewById<View>(R.id.logout_d_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<View>(R.id.logout_d_btn_positive).setOnClickListener {

                //로그아웃
                val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
                sharedPreferencesUtil.saveUserCookie("")
                sharedPreferencesUtil.addUser(UserInfo("", 0, ""))

                Toast.makeText(requireContext(), "로그아웃 완료.", Toast.LENGTH_SHORT).show()

                startActivity(Intent(mainActivity, LoginActivity::class.java))
                mainActivity.finish()
                dialog.dismiss()

            }
            dialog.show()
        }

        myPageBinding.mypageFmBtnEdit.setOnClickListener {
            findNavController().navigate(R.id.editMyAccountFragment)
        }
    }
}