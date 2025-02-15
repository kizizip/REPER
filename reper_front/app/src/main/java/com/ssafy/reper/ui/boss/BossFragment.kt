package com.ssafy.reper.ui.boss

import AccessAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
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
    val sharedPreferencesUtil: SharedPreferencesUtil by lazy {
        SharedPreferencesUtil(requireContext().applicationContext)
    }


    //sharedPreferencesUtil정보로 바꾸기
    private var sharedUserId = 0
    private var sharedStoreId = 0

    lateinit var storeName: String

    private lateinit var receiver: FragmentReceiver

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receiver 인스턴스 생성
        receiver = FragmentReceiver()
        val filter = IntentFilter("com.ssafy.reper.UPDATE_BOSS_FRAGMENT")

        ContextCompat.registerReceiver(
            requireContext(),
            receiver, // 리시버 객체
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED // 내부 앱에서만 사용
        )

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


        val filter = IntentFilter().apply {
            addAction("com.ssafy.reper.UPDATE_BOSS_FRAGMENT")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(
                receiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            requireActivity().registerReceiver(receiver, filter)
        }

        sharedUserId = sharedPreferencesUtil.getUser().userId!!.toInt()
        sharedStoreId = sharedPreferencesUtil.getStoreId()
        initAdapter()
        moveFragment()
        initSpinner()
        noticeViewModel.type = ""
        bossViewModel.accessList.observe(viewLifecycleOwner) { accessEmployees ->
            accessAdapter.updateList(accessEmployees)
            accessAdapter.notifyDataSetChanged()
            nonAccessAdapter.notifyDataSetChanged()
        }

        // waitingList가 변경되면 nonAccessAdapter의 데이터 갱신
        bossViewModel.waitingList.observe(viewLifecycleOwner) { waitingEmployees ->
            nonAccessAdapter.updateList(waitingEmployees)
            accessAdapter.notifyDataSetChanged()
            nonAccessAdapter.notifyDataSetChanged()
        }

        if ( bossViewModel.accessList.value ==null ||bossViewModel.accessList.value!!.isEmpty()){
            binding.accessFalseList.visibility =View.GONE
            binding.nothingRequest.visibility =View.VISIBLE
        }else{
            binding.accessFalseList.visibility =View.VISIBLE
            binding.nothingRequest.visibility =View.GONE
        }

        if (  bossViewModel.waitingList.value ==null || bossViewModel.waitingList.value!!.isEmpty()){
            binding.employeeList.visibility =View.GONE
            binding.nothingEmployee.visibility =View.VISIBLE
        }else{
            binding.employeeList.visibility =View.VISIBLE
            binding.nothingEmployee.visibility =View.GONE
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initAdapter() {
        bossViewModel.getAllEmployee(sharedStoreId)
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
                    bossViewModel.acceptEmployee(sharedStoreId, userId)

                    if (position in nonAccessAdapter.employeeList.indices) { // ✅ 또 다른 범위 체크
                        fcmViewModel.sendToUserFCM(
                            nonAccessAdapter.employeeList[position].userId,
                            "권한 허가 알림",
                            "${storeName}에서 권한을 허락했습니다.",
                            "MyPageFragment",
                            sharedStoreId
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
                    bossViewModel.acceptEmployee(sharedStoreId, userId)

                    if (position in nonAccessAdapter.employeeList.indices) { // ✅ 또 다른 범위 체크
                        fcmViewModel.sendToUserFCM(
                            nonAccessAdapter.employeeList[position].userId,
                            "권한 허가 알림",
                            "${storeName}에서 권한을 허락했습니다.",
                            "MyPageFragment",
                            sharedStoreId
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
                if (sharedStoreId != 0){
                    val selectedStoreName = storeList.find { it.storeId == sharedStoreId }?.storeName
                    selectedStoreName?.let {
                        val position = storeNames.indexOf(it)
                        spinner.setSelection(position)
                    }
                }else{
                    val selectedStoreName = storeList[0].storeName
                    selectedStoreName?.let {
                        val position = storeNames.indexOf(it)
                        spinner.setSelection(position)
                    }
                }
            }
        }

        // 데이터 요청
        bossViewModel.getStoreList(sharedUserId)

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
            bossViewModel.deleteEmployee(sharedStoreId, userId)
            fcmViewModel.sendToUserFCM(
                userId,
                "권한 삭제알림",
                "${storeName}에서 권한을 삭제했습니다.",
                "MyPageFragment",
                sharedStoreId
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
        try {
            requireContext().unregisterReceiver(receiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver: ${e.message}")
        }
    }


}
