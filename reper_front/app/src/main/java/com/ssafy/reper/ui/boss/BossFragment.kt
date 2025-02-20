package com.ssafy.reper.ui.boss

import AccessAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.base.FragmentReceiver
import com.ssafy.reper.data.dto.UserToken
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.FragmentBossBinding
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait


private const val TAG = "BossFragment_안주현"

class BossFragment : Fragment() {
    private var _binding: FragmentBossBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessAdapter: AccessAdapter
    private lateinit var nonAccessAdapter: AccessAdapter
    private lateinit var mainActivity: MainActivity
    val bossViewModel: BossViewModel by activityViewModels()
    private val noticeViewModel: NoticeViewModel by activityViewModels()
    private val fcmViewModel: FcmViewModel by activityViewModels()
    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil


    lateinit var storeName: String


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBossBinding.inflate(inflater, container, false)
        return binding.root


    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigation()

        initAdapter()
        moveFragment()
        initSpinner()
        noticeViewModel.type = ""
        bossViewModel.accessList.observe(viewLifecycleOwner) { accessEmployees ->
            Log.d(TAG, "Access employees size: ${accessEmployees?.size}")
            if (accessEmployees.isNullOrEmpty()) {
                binding.employeeList.visibility = View.GONE
                binding.nothingEmployee.visibility = View.VISIBLE
            } else {
                binding.employeeList.visibility = View.VISIBLE
                binding.nothingEmployee.visibility = View.GONE
                accessAdapter.updateList(accessEmployees)
            }
        }

        bossViewModel.waitingList.observe(viewLifecycleOwner) { waitingEmployees ->
            Log.d(TAG, "Waiting employees size: ${waitingEmployees?.size}")
            if (waitingEmployees.isNullOrEmpty()) {
                binding.accessFalseList.visibility = View.GONE
                binding.nothingRequest.visibility = View.VISIBLE
            } else {
                binding.accessFalseList.visibility = View.VISIBLE
                binding.nothingRequest.visibility = View.GONE
                nonAccessAdapter.updateList(waitingEmployees)
            }
        }

        // 모든 요소들을 처음에는 숨김
        binding.apply {
            bossHeaderImage.translationY = -50f
            bossHeaderImage.alpha = 1f
            
            storeFgBackIcon.alpha = 0f
            bossMenu.alpha = 0f
            
            bossFgStoreSpiner.alpha = 0f
            bossFgStoreAdd.alpha = 0f
            bossFgNoticeList.alpha = 0f
            bossFgRecipeManage.alpha = 0f
            
            access.alpha = 0f
            accessFalseList.alpha = 0f
            nothingRequest.alpha = 0f
            
            storeEmployee.alpha = 0f
            employeeList.alpha = 0f
            nothingEmployee.alpha = 0f
        }

        // 순차적으로 애니메이션 실행
        startSequentialAnimations()
    }

    private fun startSequentialAnimations() {
        val duration = 500L // 애니메이션 지속 시간
        val delay = 200L    // 각 그룹 사이의 지연 시간

        // 헤더 이미지 슬라이드 애니메이션
        binding.bossHeaderImage.animate()
            .translationY(0f)
            .setDuration(1000)
            .start()

        // 뒤로가기 아이콘과 타이틀 페이드인
        binding.apply {
            listOf(storeFgBackIcon, bossMenu).forEach { view ->
                view.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setStartDelay(400)
                    .start()
            }
        }

        // 메뉴 그룹 애니메이션
        binding.apply {
            listOf(bossFgStoreSpiner, bossFgStoreAdd, bossFgNoticeList, bossFgRecipeManage).forEach { view ->
                view.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setStartDelay(delay * 3)
                    .start()
            }
        }

        // 접근 권한 그룹 애니메이션
        binding.apply {
            listOf(access, accessFalseList, nothingRequest).forEach { view ->
                view.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setStartDelay(delay * 4)
                    .start()
            }
        }

        // 직원 그룹 애니메이션
        binding.apply {
            listOf(storeEmployee, employeeList, nothingEmployee).forEach { view ->
                view.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setStartDelay(delay * 5)
                    .start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initAdapter() {
        bossViewModel.getAllEmployee(sharedPreferencesUtil.getStoreId())
        // AccessAdapter 초기화
        accessAdapter = AccessAdapter(mutableListOf(), object : AccessAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                if (position in accessAdapter.employeeList.indices) { // ✅ 범위 체크
                    showDialog(
                        accessAdapter.employeeList[position].userName,
                        accessAdapter.employeeList[position].userId
                    )
                } else {
                    Log.e(
                        "BossFragment",
                        "잘못된 인덱스 접근! position=$position, 리스트 크기=${accessAdapter.employeeList.size}"
                    )
                }
            }

            override fun onAcceptClick(position: Int) {
                if (position in nonAccessAdapter.employeeList.indices) { // ✅ 범위 체크
                    val userId = nonAccessAdapter.employeeList[position].userId
                    bossViewModel.acceptEmployee(sharedPreferencesUtil.getStoreId(), userId).let {
                        if (it == "성공") {
                            if (position in nonAccessAdapter.employeeList.indices) { // ✅ 또 다른 범위 체크
                                fcmViewModel.sendToUserFCM(
                                    nonAccessAdapter.employeeList[position].userId,
                                    "권한 허가 알림",
                                    "${storeName}에서 권한을 허락했습니다.",
                                    "MyPageFragment",
                                    sharedPreferencesUtil.getStoreId()
                                )
                            } else {
                                Log.d(TAG, "onAcceptClick: 권한허가 실패")
                            }
                        }
                    }


                } else {
                    Log.e(
                        "BossFragment",
                        "잘못된 인덱스 접근! position=$position, 리스트 크기=${nonAccessAdapter.employeeList.size}"
                    )
                }
            }
        })

        // NonAccessAdapter 초기화
        nonAccessAdapter = AccessAdapter(mutableListOf(), object : AccessAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                if (position in nonAccessAdapter.employeeList.indices) { // ✅ 범위 체크
                    showDialog(
                        nonAccessAdapter.employeeList[position].userName,
                        nonAccessAdapter.employeeList[position].userId
                    )
                } else {
                    Log.e(
                        "BossFragment",
                        "잘못된 인덱스 접근! position=$position, 리스트 크기=${accessAdapter.employeeList.size}"
                    )
                }
            }

            override fun onAcceptClick(position: Int) {
                if (position in nonAccessAdapter.employeeList.indices) { // ✅ 범위 체크
                    val userId = nonAccessAdapter.employeeList[position].userId
                    bossViewModel.acceptEmployee(sharedPreferencesUtil.getStoreId(), userId)

                    if (position in nonAccessAdapter.employeeList.indices) { // ✅ 또 다른 범위 체크
                        fcmViewModel.sendToUserFCM(
                            nonAccessAdapter.employeeList[position].userId,
                            "권한 허가 알림",
                            "${storeName}에서 권한을 허락했습니다.",
                            "MyPageFragment",
                            sharedPreferencesUtil.getStoreId()
                        )
                    }
                } else {
                    Log.e(
                        "BossFragment",
                        "잘못된 인덱스 접근! position=$position, 리스트 크기=${nonAccessAdapter.employeeList.size}"
                    )
                }
            }
        })

        // RecyclerView 설정
        binding.employeeList.layoutManager = LinearLayoutManager(requireContext())
        binding.employeeList.adapter = accessAdapter

        binding.accessFalseList.layoutManager = LinearLayoutManager(requireContext())
        binding.accessFalseList.adapter = nonAccessAdapter

    }


    private fun initSpinner() {
        val spinner = binding.bossFgStoreSpiner

        // 스토어 리스트 관찰 후 업데이트
        bossViewModel.myStoreList.observe(viewLifecycleOwner) { storeList ->
            val storeNames = storeList.map { it.storeName }

            // 가게가 없을 경우 "등록된 가게가 없습니다" 추가
            val items = if (storeNames.isEmpty()) {
                listOf("등록된 가게가 없습니다")
            } else {
                storeNames
            }

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.order_spinner_item,
                items
            ).apply {
                setDropDownViewResource(R.layout.boss_spinner_item)
            }

            spinner.adapter = adapter

            // "등록된 가게가 없습니다" 항목을 선택하도록 설정
            if (storeNames.isEmpty()) {
                spinner.setSelection(0)
            } else {
                if (sharedPreferencesUtil.getStoreId() != 0) {
                    val selectedStoreName =
                        storeList.find { it.storeId == sharedPreferencesUtil.getStoreId() }?.storeName
                    selectedStoreName?.let {
                        val position = storeNames.indexOf(it)
                        spinner.setSelection(position)
                    }
                } else {
                    val selectedStoreName = storeList[0].storeName
                    selectedStoreName?.let {
                        val position = storeNames.indexOf(it)
                        spinner.setSelection(position)
                    }
                }
            }
        }

        // 데이터 요청
        bossViewModel.getStoreList(sharedPreferencesUtil.getUser().userId!!)

        // 스피너 선택 이벤트 설정
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = spinner.adapter.getItem(position) as String

                // "등록된 가게가 없습니다" 항목을 선택했을 때 추가 동작 없이 종료
                if (selectedItem == "등록된 가게가 없습니다") {
                    return
                }

                val selectedStore =
                    bossViewModel.myStoreList.value?.find { it.storeName == selectedItem }
                storeName = selectedStore!!.storeName.toString()
                sharedPreferencesUtil.setStoreId(selectedStore.storeId)
                bossViewModel.getAllEmployee(selectedStore.storeId!!)
                Log.d(TAG, "onItemSelected: ${selectedStore.storeId}")

                CoroutineScope(Dispatchers.Main).launch {
                    val token = withContext(Dispatchers.IO) {
                        mainActivity.getFCMToken()
                    }
                    fcmViewModel.saveToken(
                        UserToken(
                            sharedPreferencesUtil.getStoreId(),
                            token,
                            sharedPreferencesUtil.getUser().userId!!.toInt()
                        )
                    )
                    Log.d("FCMTOKEN", token)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
    }


    private fun moveFragment() {
        binding.bossFgStoreAdd.setOnClickListener {
            findNavController().navigate(R.id.storeManage)

        }


        binding.bossFgNoticeList.setOnClickListener {
            findNavController().navigate(R.id.noticeManageFragment)

        }

        binding.bossFgRecipeManage.setOnClickListener {
            findNavController().navigate(R.id.recipeManageFragment)

        }

        binding.storeFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


    private fun showDialog(employeeName: String, userId: Int) {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete)

        // 텍스트를 변경하려는 TextView 찾기
        val nameTextView = dialog.findViewById<TextView>(R.id.dialog_delete_bold_tv)
        val middleTV = dialog.findViewById<TextView>(R.id.dialog_delete_rle_tv)

        // 텍스트 변경
        nameTextView.text = "${employeeName}"
        middleTV.text = "의 권한을"


        dialog.findViewById<View>(R.id.dialog_delete_cancle_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.dialog_delete_delete_btn).setOnClickListener {
            bossViewModel.deleteEmployee(sharedPreferencesUtil.getStoreId(), userId)
            fcmViewModel.sendToUserFCM(
                userId,
                "권한 삭제알림",
                "${storeName}에서 권한을 삭제했습니다.",
                "MyPageFragment",
                sharedPreferencesUtil.getStoreId()
            ).let {
                if (it.equals("성공")) {
                    fcmViewModel.deleteUserToken(userId)
                }
                Toast.makeText(requireContext(), "권한 삭제 완료", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
