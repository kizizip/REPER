package com.ssafy.reper.ui.boss

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentRecipeManageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.adpater.RecipeAdapter
import com.ssafy.reper.ui.login.LoginActivity

private const val TAG = "RecipeManageFragment_싸피"
class RecipeManageFragment : Fragment() {
    private var _binding: FragmentRecipeManageBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var menuList: MutableList<String>

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
        _binding = FragmentRecipeManageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuList = mutableListOf(
            "아메리카노",
            "카페라떼",
            "카푸치노",
            "프라푸치노",
            "핫초코",
            "카라멜 마끼아또",
            "에스프레소",
            "바닐라 라떼",
            "그린티 라떼",
            "프라프치노"
        )

        binding.recipeFgRV.layoutManager = LinearLayoutManager(requireContext())
        binding.recipeFgRV.adapter =
            RecipeAdapter(menuList, object : RecipeAdapter.ItemClickListener {
                override fun onItemClick(position: Int) {
                    showDialog(menuList[position])
                    Log.d(TAG, "onItemClick: ")
                }
            })

        binding.recipeFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(menuName: String) {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete)

        // 텍스트를 변경하려는 TextView 찾기
        val textView = dialog.findViewById<TextView>(R.id.dialog_delete_bold_tv)

        // 텍스트 변경
        textView.text = "${menuName} 레시피"

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