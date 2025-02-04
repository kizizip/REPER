package com.ssafy.reper.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.reper.R
import android.widget.ArrayAdapter
import android.widget.AdapterView
import com.ssafy.reper.databinding.FragmentJoinBinding


class JoinFragment : Fragment() {

    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spinner 설정
        val spinner = binding.FragmentJoinSpinnerUserType
        val userTypes = arrayOf("사장님", "직원")
        
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

    
}