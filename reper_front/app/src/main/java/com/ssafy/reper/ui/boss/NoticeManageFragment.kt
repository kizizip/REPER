package com.ssafy.reper.ui.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentNoticeManageBinding
import com.ssafy.reper.databinding.FragmentRecipeManageBinding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
