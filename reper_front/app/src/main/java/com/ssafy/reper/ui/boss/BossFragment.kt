package com.ssafy.reper.ui.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
