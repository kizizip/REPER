package com.ssafy.reper.ui.mypage

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.OwnerStore
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.dto.UserInfo
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.databinding.FragmentMyPageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.login.LoginActivity
import com.ssafy.reper.ui.mypage.adapter.StoreSearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
        val user = sharedPreferencesUtil.getUser()
        var ownerStoreList: List<OwnerStore> = mutableListOf()  // 사장 가게 조회
        var employeeStoreList: List<Store> = mutableListOf()    // 직원 가게 조회
        
        // 사장 or 직원 표시
        if (user.role == "OWNER") {
            myPageBinding.mypageFmTvYellow.text = "${user.username} 사장"
            myPageBinding.mypageFmBtnBossMenu.text = "사장님 메뉴"

            // 사장 가게 정보 조회
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    ownerStoreList = RetrofitUtil.storeService.getStoreListByOwnerId(user.userId.toString())
                    withContext(Dispatchers.Main) {
                        myPageBinding.mypageFmTvStoreNum.text = "${ownerStoreList.size}"
                        setupSpinner(ownerStoreList, user.role)  // 파라미터 전달
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "매장 정보 조회 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }



        } else {
            myPageBinding.mypageFmTvYellow.text = "${user.username} 직원"
            myPageBinding.mypageFmBtnBossMenu.text = "권한 요청"
            myPageBinding.textView7.text = "근무매장 수 : "

            // 직원 가게 정보 조회
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    employeeStoreList = RetrofitUtil.storeService.getStoreListByEmployeeId(user.userId.toString())
                    withContext(Dispatchers.Main) {
                        myPageBinding.mypageFmTvStoreNum.text = "${employeeStoreList.size}"
                        setupSpinner(employeeStoreList, user.role.toString())  // 파라미터 전달
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "매장 정보 조회 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        startSequentialAnimation()
        initEvent()
    }

    private fun startSequentialAnimation() {
        // 뷰가 유효한지 확인
        val binding = _myPageBinding ?: return

        // 헤더 이미지 애니메이션
        binding.imageView4.apply {
            translationY = -200f
            animate()
                .translationY(0f)
                .setDuration(1000)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                    // 바운스 효과
                    animate()
                        .translationY(-30f)
                        .setDuration(150)
                        .withEndAction {
                            if (_myPageBinding == null) return@withEndAction
                            animate()
                                .translationY(0f)
                                .setDuration(150)
                        }
                }
        }

        // 첫 번째 그룹 (상단 프로필 영역) - 투명도로 페이드인
        val firstGroup = listOf(
            binding.mypageFmBtnBell,
            binding.mypageFmTvYellow,
            binding.mypageFmTvName,
            binding.textView6,
            binding.mypageFmBtnLogout
        )

        firstGroup.forEach { view ->
            view.alpha = 0f
            view.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(500)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                }
        }

        // 두 번째 그룹 (매장 정보 영역) - 왼쪽에서 슬라이드
        val secondGroup = listOf(
            binding.textView7,
            binding.mypageFmTvStoreNum,
            binding.mypageFmBtnBossMenu,
            binding.mypageFmSp
        )

        secondGroup.forEach { view ->
            view.translationX = -200f
            view.alpha = 0f
            view.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(1000)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                }
        }

        // 세 번째 그룹 (하단 버튼들) - 아래에서 위로 슬라이드
        val thirdGroup = listOf(
            binding.mypageFmBtnNotice,
            binding.mypageFmBtnRecipe,
            binding.mypageFmBtnEdit
        )

        thirdGroup.forEach { view ->
            view.translationY = 200f
            view.alpha = 0f
            view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(1500)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                }
        }
    }

    private fun setupSpinner(storeList: List<Any>, role: String) {
        val binding = _myPageBinding ?: return
        
        // 가게 이름 Spinner 설정
        val spinner = binding.mypageFmSp
        
        val storeNames = if (role == "OWNER") {
            (storeList as List<OwnerStore>).map { it.storeName }
        } else {
            (storeList as List<Store>).map { it.name }
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.mypage_spinner_item, storeNames).apply {
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
                val selectedItem = storeNames[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
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

            if (userInfo.role == "OWNER") {
                findNavController().navigate(R.id.bossFragment)
            } else {
                val dialog = Dialog(mainActivity)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.dialog_request_access)

                // 초기 상태 설정
                dialog.findViewById<RecyclerView>(R.id.request_access_d_rv).visibility = View.GONE
                dialog.findViewById<TextView>(R.id.request_access_d_no_result).visibility = View.VISIBLE

                dialog.findViewById<EditText>(R.id.request_access_d_et)
                    .addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val searchText = s.toString()
                                    if (searchText.isNotEmpty()) {
                                        // 엘라스틱 서치
                                        val storeList = RetrofitUtil.storeService.searchAllStores(searchText)
                                        withContext(Dispatchers.Main) {
                                            val recyclerView = dialog.findViewById<RecyclerView>(R.id.request_access_d_rv)
                                            val noResultText = dialog.findViewById<TextView>(R.id.request_access_d_no_result)
                                            
                                            if (storeList.isEmpty()) {
                                                // 검색 결과가 없을 때
                                                recyclerView.visibility = View.GONE
                                                noResultText.visibility = View.VISIBLE
                                            } else {
                                                // 검색 결과가 있을 때
                                                recyclerView.visibility = View.VISIBLE
                                                noResultText.visibility = View.GONE
                                                
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
                                    } else {
                                        // 검색어가 비어있을 때
                                        withContext(Dispatchers.Main) {
                                            dialog.findViewById<RecyclerView>(R.id.request_access_d_rv).visibility = View.GONE
                                            dialog.findViewById<TextView>(R.id.request_access_d_no_result).visibility = View.VISIBLE
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
                                withContext(Dispatchers.Main) {  // Main 스레드에서 토스트 메시지 표시
                                    Toast.makeText(
                                        requireContext(),
                                        "권한 요청 완료",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

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
                sharedPreferencesUtil.clearUserData()

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