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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentEditMyAccountBinding
import com.ssafy.reper.databinding.FragmentMyPageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.login.LoginActivity
import com.ssafy.reper.ui.recipe.FullRecipeListAdapter
import com.ssafy.reper.ui.recipe.StepRecipeFragment

class EditMyAccountFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    // 내 권한 허용된 가게 리스트 recyclerView Adapter
    private lateinit var myAccessStoreListAdapter: MyAccessStoreListAdapter

    private var _editMyAccountBinding : FragmentEditMyAccountBinding? = null
    private val editMyAccountBinding get() =_editMyAccountBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _editMyAccountBinding = FragmentEditMyAccountBinding.inflate(inflater, container, false)
        return editMyAccountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavigation()

        // RecyclerView adapter 처리
        initAdapter()

        initEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _editMyAccountBinding = null
    }

    private fun initEvent() {
        editMyAccountBinding.editmyaccountFmBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        editMyAccountBinding.editmyaccountFmTvDeleteaccount.setOnClickListener {
            val dialog = Dialog(mainActivity)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_delete_account)
            dialog.findViewById<View>(R.id.deleteaccount_d_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<View>(R.id.deleteaccount_d_btn_delete).setOnClickListener {
//                ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
//                ApplicationClass.sharedPreferencesUtil.deleteUser()
                startActivity(Intent(mainActivity, LoginActivity::class.java))
                mainActivity.finish()
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    fun initAdapter() {
        // allrecipe item 클릭 이벤트 리스너
        myAccessStoreListAdapter = MyAccessStoreListAdapter(mutableListOf()) { position ->
            myAccessStoreListAdapter.notifyItemRemoved(position)
        }

        editMyAccountBinding.editmyaccountFmRv.apply {
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            myAccessStoreListAdapter.accessStoreList = mutableListOf("메가커피 구미 인동점", "메가커피 구미 황상점", "메가커피 구미 진평점", "메가커피 구미 원평점", "스타벅스 구미역점")
            addItemDecoration(DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL))
            adapter = myAccessStoreListAdapter
        }
    }
}