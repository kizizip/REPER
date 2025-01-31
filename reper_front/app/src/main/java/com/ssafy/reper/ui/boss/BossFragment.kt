package com.ssafy.reper.ui.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Employee
import com.ssafy.reper.databinding.FragmentBossBinding

class BossFragment : Fragment() {
    private var _binding: FragmentBossBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBossBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val acessEmpolyees =listOf(
            Employee(name = "김정언", access = true),
            Employee(name = "심근원", access = true),
            Employee(name = "안주현", access = true)
        )

        val nonAcessEmpolyees =listOf(
            Employee(name = "박재영", access = false),
            Employee(name = "임지혜", access = false),
            Employee(name = "이서현", access = false)
        )

        binding.employeeList.layoutManager = LinearLayoutManager(requireContext())
        binding.employeeList.adapter = AccessAdapter(acessEmpolyees)

        binding.accessFalseList.layoutManager = LinearLayoutManager(requireContext())
        binding.accessFalseList.adapter = AccessAdapter(nonAcessEmpolyees)




        // Spinner 설정
        val spinner = binding.bossFgStoreSpiner
        val userTypes = arrayOf("메가커피 구미 인동점", "메가커피 구미 진평점")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.join_spinner_item,
            userTypes
        ).apply {
            setDropDownViewResource(R.layout.join_spinner_item)
        }

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = userTypes[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
