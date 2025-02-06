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
import com.ssafy.reper.R
import com.ssafy.reper.ui.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StoreManageFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

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
        return inflater.inflate(R.layout.fragment_store_manage, container, false)
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
                        //가게 추가 로직
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


}