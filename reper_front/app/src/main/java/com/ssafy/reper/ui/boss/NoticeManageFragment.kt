package com.ssafy.reper.ui.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentNoticeManageBinding
import com.ssafy.reper.ui.boss.adpater.NotiAdapter
import com.ssafy.reper.ui.MainActivity


class NoticeManageFragment : Fragment() {
    private var _binding: FragmentNoticeManageBinding? = null
    private val binding get() = _binding!!
    private val noticeViewModel: NoticeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeManageBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        noticeViewModel.init(1,1)
        initNotiAdater()
        initSpinner()

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

    fun initSpinner() {
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
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = notis[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
    }

    fun initNotiAdater() {
        binding.notiList.layoutManager = LinearLayoutManager(requireContext())
        val notiAdapter = NotiAdapter( emptyList() , object : NotiAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                findNavController().navigate(R.id.writeNotiFragment)
                Toast.makeText(requireContext(), "공지 $position 클릭됨", Toast.LENGTH_SHORT).show()
            }
        })

        binding.notiList.adapter = notiAdapter

        noticeViewModel.noticeList.observe(viewLifecycleOwner, { newList ->
            notiAdapter.noticeList = newList
            notiAdapter.notifyDataSetChanged()
        })


    }


}
