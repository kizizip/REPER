package com.ssafy.reper.ui.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentNoticeManageBinding
import com.ssafy.reper.ui.boss.adpater.NotiAdapter
import com.ssafy.reper.ui.MainActivity


class NoticeManageFragment : Fragment() {
    private var _binding: FragmentNoticeManageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeManageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.notiList.layoutManager = LinearLayoutManager(requireContext())
        binding.notiList.adapter = NotiAdapter()

        val notiSpinner = binding.notiFgSpinner
        val notis = arrayOf("제목", "내용")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.order_spinner_item,
            notis
        ).apply {
            setDropDownViewResource(R.layout.boss_spinner_item)
        }

        notiSpinner.adapter = adapter

        notiSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = notis[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }

        binding.notiFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment가 파괴될 때 BottomNavigationView 다시 보이게 하기
        (activity as MainActivity).showBottomNavigation()

        _binding = null
    }


}
