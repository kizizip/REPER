package com.ssafy.reper.ui.mypage

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentAllRecipeBinding
import com.ssafy.reper.databinding.FragmentMyPageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.BossFragment
import com.ssafy.reper.ui.login.LoginActivity

class MyPageFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    private var _myPageBinding : FragmentMyPageBinding? = null
    private val myPageBinding get() =_myPageBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _myPageBinding = FragmentMyPageBinding.inflate(inflater, container, false)
        return myPageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.showBottomNavigation()

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // 가게 이름 Spinner 설정
        val spinner = myPageBinding.mypageFmSp
        val userTypes = arrayOf("메가커피 구미 인동점", "이스터에그:이걸 발견하다니!")

        val adapter = ArrayAdapter(requireContext(), R.layout.mypage_spinner_item, userTypes).apply {
            setDropDownViewResource(R.layout.mypage_spinner_item)
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
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        initEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myPageBinding = null
    }

    fun initEvent(){
        myPageBinding.mypageFmBtnBossMenu.setOnClickListener {
            findNavController().navigate(R.id.bossFragment)
        }

        myPageBinding.mypageFmBtnLogout.setOnClickListener {
            val dialog = Dialog(mainActivity)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_logout)
            dialog.findViewById<View>(R.id.logout_d_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<View>(R.id.logout_d_btn_positive).setOnClickListener {
//                ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
//                ApplicationClass.sharedPreferencesUtil.deleteUser()
                startActivity(Intent(mainActivity, LoginActivity::class.java))
                mainActivity.finish()
                dialog.dismiss()
            }
            dialog.show()
        }

        myPageBinding.mypageFmBtnEdit.setOnClickListener {
            findNavController().navigate(R.id.editMyAccountFragment)
        }
    }
}