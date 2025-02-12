package com.ssafy.reper.ui.boss

import AccessAdapter
import android.app.Dialog
import android.content.Context
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.FragmentBossBinding
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.MainActivity


private const val TAG = "BossFragment_안주현"

class BossFragment : Fragment() {
    private var _binding: FragmentBossBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessAdapter: AccessAdapter
    private lateinit var nonAccessAdapter: AccessAdapter
    private lateinit var mainActivity: MainActivity
    private val bossViewModel: BossViewModel by activityViewModels()
    private val noticeViewModel: NoticeViewModel by activityViewModels()
    private val fcmViewModel: FcmViewModel by activityViewModels()
    val sharedPreferencesUtil: SharedPreferencesUtil by lazy {
        SharedPreferencesUtil(requireContext().applicationContext)
    }

    //sharedPreferencesUtil정보로 바꾸기
    private var sharedUserId = 0
    private var sharedStoreId = 0

    lateinit var storeName: String

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
        _binding = FragmentBossBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigation()
        sharedUserId = sharedPreferencesUtil.getUser().userId!!.toInt()
        sharedStoreId = sharedPreferencesUtil.getStoreId()
        initAdapter()
        moveFragment()
        initSpinner()
        noticeViewModel.type = ""

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
                showDialog(
                    accessAdapter.employeeList[position].userName,
                    accessAdapter.employeeList[position].userId
                )
            }

            override fun onAcceptClick(position: Int) {
                bossViewModel.acceptEmployee(
                    sharedStoreId,
                    accessAdapter.employeeList[position].userId
                )
                fcmViewModel.sendToUserFCM(
                    accessAdapter.employeeList[position].userId,
                    "권한 허가 알림",
                    "${storeName}에서 권한을 허락했습니다.",
                    "MyPageFragment",
                    sharedStoreId
                )

            }
        })

        // NonAccessAdapter 초기화
        nonAccessAdapter = AccessAdapter(mutableListOf(), object : AccessAdapter.ItemClickListener {
            override fun onDeleteClick(position: Int) {
                showDialog(
                    accessAdapter.employeeList[position].userName,
                    nonAccessAdapter.employeeList[position].userId
                )

            }

            override fun onAcceptClick(position: Int) {
                bossViewModel.acceptEmployee(
                    sharedStoreId,
                    nonAccessAdapter.employeeList[position].userId
                )
                fcmViewModel.sendToUserFCM(
                    accessAdapter.employeeList[position].userId,
                    "권한 허가 알림",
                    "${storeName}에서 권한을 허락했습니다.",
                    "MyPageFragment",
                    sharedStoreId
                )

            }
        })

        // RecyclerView 설정
        binding.employeeList.layoutManager = LinearLayoutManager(requireContext())
        binding.employeeList.adapter = accessAdapter

        binding.accessFalseList.layoutManager = LinearLayoutManager(requireContext())
        binding.accessFalseList.adapter = nonAccessAdapter

        // LiveData 관찰
        observeLiveData()
    }

    private fun observeLiveData() {
        // accessList가 변경되면 accessAdapter의 데이터 갱신
        bossViewModel.accessList.observe(viewLifecycleOwner) { accessEmployees ->
            accessAdapter.updateList(accessEmployees)
        }

        // waitingList가 변경되면 nonAccessAdapter의 데이터 갱신
        bossViewModel.waitingList.observe(viewLifecycleOwner) { waitingEmployees ->
            nonAccessAdapter.updateList(waitingEmployees)
        }
    }


    private fun initSpinner() {
        val spinner = binding.bossFgStoreSpiner

        // 스토어 리스트 관찰 후 업데이트
        bossViewModel.myStoreList.observe(viewLifecycleOwner) { storeList ->
            val storeNames = storeList.map { it.storeName }

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.order_spinner_item,
                storeNames
            ).apply {
                setDropDownViewResource(R.layout.boss_spinner_item)
            }

            spinner.adapter = adapter
            val selectedStoreName = storeList.find { it.storeId == sharedStoreId }?.storeName
            selectedStoreName?.let {
                val position = storeNames.indexOf(it)
                spinner.setSelection(position)
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
                val selectedStore =
                    bossViewModel.myStoreList.value?.find { it.storeName == selectedItem }
                storeName = selectedStore!!.storeName.toString()
                sharedPreferencesUtil.setStoreId(selectedStore.storeId)
                bossViewModel.getAllEmployee(selectedStore.storeId!!)
                Log.d(TAG, "onItemSelected: ${selectedStore.storeId}")
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
                if (it.equals("성공")){
                    fcmViewModel.deleteUserToken(userId)
                }
                Toast.makeText(requireContext(), "권한 삭제 완료", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }
        dialog.show()
    }

}
