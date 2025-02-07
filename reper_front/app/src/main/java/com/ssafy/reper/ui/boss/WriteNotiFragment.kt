package com.ssafy.reper.ui.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentNoticeManageBinding
import com.ssafy.reper.databinding.FragmentRecipeManageBinding
import com.ssafy.reper.databinding.FragmentWriteNotiBinding


class WriteNotiFragment : Fragment() {
    private var _binding: FragmentWriteNotiBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteNotiBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notiWriteFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}