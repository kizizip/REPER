package com.ssafy.reper.ui.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentAllRecipeBinding

class AllRecipeFragment : Fragment() {

    private var _allRecipeBinding : FragmentAllRecipeBinding? = null
    private val allRecipeBinding get() =_allRecipeBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _allRecipeBinding = FragmentAllRecipeBinding.inflate(inflater, container, false)
        return allRecipeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 카테고리 Spinner 설정
        val categorySpinner = allRecipeBinding.allrecipeFmSp
        val category = arrayOf("coffee", "non-coffee")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.order_spinner_item,
            category
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = category[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _allRecipeBinding = null
    }
}