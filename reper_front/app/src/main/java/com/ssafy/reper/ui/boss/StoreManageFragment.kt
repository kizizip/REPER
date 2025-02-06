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
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentBossBinding
import com.ssafy.reper.databinding.FragmentStoreManageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.adpater.StoreAdapter


class StoreManageFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private var _binding: FragmentStoreManageBinding? = null
    private val binding get() = _binding!!

    private lateinit var storeAdapter: StoreAdapter
    private val bossViewModel: BossViewModel by activityViewModels()


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
        _binding = FragmentStoreManageBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.storeFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.storeFgAddTv.setOnClickListener {
            showStoreAddDialog()
        }

        initAdapter()

    }



    private fun showStoreAddDialog() {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_store_add)
        dialog.findViewById<View>(R.id.store_add_content).visibility = View.GONE

        val editText = dialog.findViewById<EditText>(R.id.storeAddET)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editText.text != null){
                    dialog.findViewById<View>(R.id.store_add_content).visibility = View.VISIBLE
                    dialog.findViewById<TextView>(R.id.add_store_name).text = editText.text.toString()

                    dialog.findViewById<View>(R.id.store_add_btn_cancel).setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.findViewById<View>(R.id.store_add_btn_positive).setOnClickListener {
                        bossViewModel.addStore(editText.text.toString(),1)
                        dialog.dismiss()
                    }
                }
                true
            } else {
                dialog.findViewById<View>(R.id.store_add_btn_cancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.store_add_btn_positive).setOnClickListener {
                    dialog.dismiss()
                }
                false

            }
        }


        dialog.findViewById<View>(R.id.store_add_btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.store_add_btn_positive).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun initAdapter() {
        var storeList = bossViewModel.myStoreList.value!!
        storeAdapter = StoreAdapter(storeList
            , object : StoreAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                showDialog(storeList[position].storeName)
            }
        })


        binding.storeFgRV.layoutManager = LinearLayoutManager(requireContext())
        binding.storeFgRV.adapter = storeAdapter

    }


    private fun showDialog(storeName: String) {
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete)

        // 텍스트를 변경하려는 TextView 찾기
        val nameTextView = dialog.findViewById<TextView>(R.id.dialog_delete_bold_tv)
        val middleTV = dialog.findViewById<TextView>(R.id.dialog_delete_rle_tv)

        // 텍스트 변경
        nameTextView.text = "${storeName}"
        middleTV.text = "의 정보를"


        dialog.findViewById<View>(R.id.dialog_delete_cancle_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.dialog_delete_delete_btn).setOnClickListener {
            bossViewModel.deleteStore(1,1)
            dialog.dismiss()
        }
        dialog.show()
    }

}