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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.databinding.FragmentBossBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.adpater.AccessAdapter

class BossFragment : Fragment() {
    private var _binding: FragmentBossBinding? = null
    private val binding get() = _binding!!

    private lateinit var accessAdapter: AccessAdapter
    private lateinit var nonAccessAdapter: AccessAdapter
    private lateinit var mainActivity: MainActivity

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

        //나중에 라이브 데이터를 사용할 예정인 데이터 리스트....
        val accessEmployees = mutableListOf(
            Employee(name = "김정언", access = true),
            Employee(name = "심근원", access = true),
            Employee(name = "안주현", access = true)
        )

        val nonAccessEmployees = mutableListOf(
            Employee(name = "박재영", access = false),
            Employee(name = "임지혜", access = false),
            Employee(name = "이서현", access = false)
        )

        accessAdapter = AccessAdapter(accessEmployees, object : AccessAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                //여기는 삭제 버튼뿐
                showDialog(accessEmployees[position].name)
            }
        })

        nonAccessAdapter = AccessAdapter(nonAccessEmployees, object : AccessAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                    //여기는 수락, 거절 버튼 두개있음
            }
        })

        binding.employeeList.layoutManager = LinearLayoutManager(requireContext())
        binding.employeeList.adapter = accessAdapter

        binding.accessFalseList.layoutManager = LinearLayoutManager(requireContext())
        binding.accessFalseList.adapter = nonAccessAdapter
    }

    private fun initSpinner() {
        val spinner = binding.bossFgStoreSpiner
        val userTypes = arrayOf("메가커피 구미 인동점", "메가커피 구미 진평점")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.order_spinner_item,
            userTypes
        ).apply {
            setDropDownViewResource(R.layout.boss_spinner_item)
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
    }


    private fun moveFragment() {
        binding.bossFgNoticeWrite.setOnClickListener {
            findNavController().navigate(R.id.writeNotiFragment)
        }


        binding.bossFgNoticeList.setOnClickListener {
            findNavController().navigate(R.id.noticeManageFragment)

        }

        binding.bossFgRecipeManage.setOnClickListener {
            findNavController().navigate(R.id.recipeManageFragment)

        }

        binding.recipeFgBackIcon.setOnClickListener {
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
            dialog.dismiss()
        }
        dialog.show()
    }


}
