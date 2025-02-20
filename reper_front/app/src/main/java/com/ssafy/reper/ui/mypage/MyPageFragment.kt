package com.ssafy.reper.ui.mypage

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.reper.data.dto.OwnerStore
import com.ssafy.reper.data.dto.SearchedStore
import com.ssafy.reper.data.dto.Store
import com.ssafy.reper.data.dto.StoreResponseUser
import com.ssafy.reper.data.dto.UserInfo
import com.ssafy.reper.data.dto.UserToken
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.databinding.FragmentMyPageBinding
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.BossViewModel
import com.ssafy.reper.ui.boss.NoticeViewModel
import com.ssafy.reper.ui.home.StoreViewModel
import com.ssafy.reper.ui.login.LoginActivity
import com.ssafy.reper.ui.mypage.adapter.StoreSearchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MyPageFragment_싸피"

class MyPageFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    private var _myPageBinding: FragmentMyPageBinding? = null
    private val myPageBinding get() = _myPageBinding!!
    private val fcmViewModel: FcmViewModel by activityViewModels()
    private val bossViewModel: BossViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by activityViewModels()


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

        if (user.role == "OWNER") {
            myPageBinding.mypageFmTvYellow.text = "${user.username} 사장"
            myPageBinding.mypageFmBtnBossMenu.text = "사장님 메뉴"

        } else {
            myPageBinding.mypageFmTvYellow.text = "${user.username} 직원"
            myPageBinding.mypageFmBtnBossMenu.text = "권한 요청"
        }


        startSequentialAnimation()
        setupSpinner()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }



    private fun startSequentialAnimation() {
        // 뷰가 유효한지 확인
        val binding = _myPageBinding ?: return

        // 헤더 이미지 애니메이션
        binding.imageView4.apply {
            translationY = -200f
            animate()
                .translationY(0f)
                .setDuration(500)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                    // 바운스 효과
                    animate()
                        .translationY(-30f)
                        .setDuration(100)
                        .withEndAction {
                            if (_myPageBinding == null) return@withEndAction
                            animate()
                                .translationY(0f)
                                .setDuration(100)
                        }
                }
        }

        // 첫 번째 그룹 (상단 프로필 영역)
        val firstGroup = listOf(
            binding.mypageFmTvYellow,
            binding.mypageFmTvName,
            binding.textView6,
            binding.mypageFmBtnLogout
        )

        firstGroup.forEach { view ->
            view.alpha = 0f
            view.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(300)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                }
        }

        // 두 번째 그룹 (매장 정보 영역)
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
                .setDuration(400)
                .setStartDelay(500)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                }
        }

        // 세 번째 그룹 (하단 버튼들)
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
                .setDuration(400)
                .setStartDelay(700)
                .withEndAction {
                    if (_myPageBinding == null) return@withEndAction
                }
        }
    }

    private fun setupSpinner() {
        val spinner = myPageBinding.mypageFmSp

        val observeStoreList: (List<Any>) -> Unit = { originalStoreList ->
            // 먼저 리스트를 storeId 기준으로 정렬
            val storeList = when {
                originalStoreList.isNotEmpty() && originalStoreList[0] is SearchedStore -> {
                    originalStoreList.sortedBy { (it as SearchedStore).storeId }
                }
                originalStoreList.isNotEmpty() && originalStoreList[0] is StoreResponseUser -> {
                    originalStoreList.sortedBy { (it as StoreResponseUser).storeId }
                }
                else -> originalStoreList
            }

            val storeNames: MutableList<String>
            val storeIds: MutableList<Int>

            if (storeList.isNotEmpty()) {
                myPageBinding.mypageFmTvStoreNum.text = storeList.size.toString()
                
                when (val firstItem = storeList[0]) {
                    is SearchedStore -> {
                        storeNames = storeList.map { (it as SearchedStore).storeName ?: "등록된 가게가 없습니다." }.toMutableList()
                        storeIds = storeList.map { (it as SearchedStore).storeId ?: 0}.toMutableList()
                    }
                    is StoreResponseUser -> {
                        storeNames = storeList.map { (it as StoreResponseUser).name}.toMutableList()
                        storeIds = storeList.map { (it as StoreResponseUser).storeId }.toMutableList()
                    }
                    else -> {
                        storeNames = mutableListOf("등록된 가게가 없습니다.")
                        storeIds = mutableListOf(0)
                        sharedPreferencesUtil.setStoreId(0)
                    }
                }

            } else {
                storeNames = mutableListOf("등록된 가게가 없습니다.")
                storeIds = mutableListOf(0)
                sharedPreferencesUtil.setStoreId(0)
                myPageBinding.mypageFmTvStoreNum.text = "0"
            }



            // Adapter 설정
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.home_spinner_item,
                storeNames
            ).apply {
                setDropDownViewResource(R.layout.home_spinner_item)
            }

            spinner.adapter = adapter

            // 저장된 storeId 가져오기
            val savedStoreId = sharedPreferencesUtil.getStoreId()
            val defaultIndex = storeIds.indexOfFirst { it == savedStoreId }

            // 기본 선택 인덱스 설정
            if (defaultIndex != -1) {
                spinner.setSelection(defaultIndex)
                sharedPreferencesUtil.setStoreId(storeIds[defaultIndex])
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // 선택된 storeId 저장
                    val selectedStoreId = storeIds.getOrNull(position)
                    selectedStoreId?.let { sharedPreferencesUtil.setStoreId(it) }
                    CoroutineScope(Dispatchers.Main).launch {
                        val token = withContext(Dispatchers.IO) {
                            (activity as MainActivity).getFCMToken()
                        }
                        fcmViewModel.saveToken(UserToken(sharedPreferencesUtil.getStoreId(), token, sharedPreferencesUtil.getUser().userId!!.toInt()))
                        Log.d("FCMTOKEN", token)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // OWNER인지 아닌지에 따라 다른 뷰모델을 사용
        if (sharedPreferencesUtil.getUser().role == "OWNER") {
            bossViewModel.myStoreList.observe(viewLifecycleOwner, observeStoreList)
        } else {
            storeViewModel.myStoreList.observe(viewLifecycleOwner, observeStoreList)
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
                dialog.findViewById<TextView>(R.id.request_access_d_no_result).visibility =
                    View.VISIBLE

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
                                            val noResultText =
                                                dialog.findViewById<TextView>(R.id.request_access_d_no_result)

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
                                                    dialog.findViewById<ConstraintLayout>(R.id.request_access_d_btn_positive)
                                                        .apply {
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
                                            dialog.findViewById<RecyclerView>(R.id.request_access_d_rv).visibility =
                                                View.GONE
                                            dialog.findViewById<TextView>(R.id.request_access_d_no_result).visibility =
                                                View.VISIBLE
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
                                    storeId!!,
                                    userId!!
                                )
                                withContext(Dispatchers.Main) {  // Main 스레드에서 토스트 메시지 표시
                                    Toast.makeText(
                                        requireContext(),
                                        "권한 요청 완료",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    RetrofitUtil.storeService.getStore(storeId!!).let {
                                        Log.d(TAG, "initEvent: ${it.ownerId}")
                                        fcmViewModel.sendToUserFCM(
                                            it.ownerId,
                                            "권한요청알림",
                                            "${sharedPreferencesUtil.getUser().username}님께서 ${it.storeName}에 권한을 요청하셨습니다.",
                                            "BossFragment",
                                            it.storeId
                                        )
                                    }
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

                fcmViewModel.deleteUserToken(sharedPreferencesUtil.getUser().userId!!.toInt())

                //로그아웃
                val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
                sharedPreferencesUtil.saveUserCookie("")
                sharedPreferencesUtil.addUser(UserInfo("", 0, ""))
                sharedPreferencesUtil.clearUserData()

                Toast.makeText(requireContext(), "로그아웃 완료.", Toast.LENGTH_SHORT).show()
                fcmViewModel.deleteUserToken(sharedPreferencesUtil.getUser().userId!!.toInt())
                startActivity(Intent(mainActivity, LoginActivity::class.java))
                mainActivity.finish()
                dialog.dismiss()

            }
            dialog.show()
        }

        myPageBinding.mypageFmBtnEdit.setOnClickListener {
            findNavController().navigate(R.id.editMyAccountFragment)
        }

        myPageBinding.mypageFmBtnNotice.setOnClickListener {
            findNavController().navigate(R.id.noticeManageFragment)

        }
        myPageBinding.mypageFmBtnRecipe.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.allRecipeFragment)

        }
    }
}