package com.ssafy.reper.ui.boss

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentBossBinding
import com.ssafy.reper.ui.MainActivity
//import com.ssafy.reper.ui.boss.adpater.AccessAdapter

class BossFragment : Fragment() {
    private var _binding: FragmentBossBinding? = null
    private val binding get() = _binding!!

//    private lateinit var accessAdapter: AccessAdapter
//    private lateinit var nonAccessAdapter: AccessAdapter
    private lateinit var mainActivity: MainActivity
    private val bossViewModel: BossViewModel by activityViewModels()
    var userId = 1
    var storeId = 1

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
        initAdapter()
        moveFragment()
        initSpinner()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {

//        accessAdapter = AccessAdapter(accessEmployees, object : AccessAdapter.ItemClickListener {
//            override fun onClick(position: Int) {
//                //여기는 삭제 버튼뿐
//                showDialog(accessEmployees[position].name)
//            }
//        })
//
//        nonAccessAdapter = AccessAdapter(nonAccessEmployees, object : AccessAdapter.ItemClickListener {
//            override fun onClick(position: Int) {
//                    //여기는 수락, 거절 버튼 두개있음
//            }
//        })

//        binding.employeeList.layoutManager = LinearLayoutManager(requireContext())
//        binding.employeeList.adapter = accessAdapter
//
//        binding.accessFalseList.layoutManager = LinearLayoutManager(requireContext())
//        binding.accessFalseList.adapter = nonAccessAdapter
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
        }

        // 데이터 요청
        bossViewModel.getStoreList(userId)

        // 스피너 선택 이벤트 설정
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = spinner.adapter.getItem(position) as String
                // 선택된 항목 처리
                //여기서 공유된 데이터의 현재 가게 Id를 바꿔주어야 다른 화면들도 갱신될것!
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


    private fun showDialog(employeeName: String) {
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
            //레시피 삭제 로직작성
            Toast.makeText(requireContext(), "레시피 등록 완료", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }





}
