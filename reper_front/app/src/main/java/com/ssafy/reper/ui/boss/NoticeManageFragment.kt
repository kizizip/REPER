package com.ssafy.reper.ui.boss

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.reper.R
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.FragmentNoticeManageBinding
import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.boss.adpater.NotiAdapter

private const val TAG = "NoticeManageFragment"

class NoticeManageFragment : Fragment() {
    private var _binding: FragmentNoticeManageBinding? = null
    private val binding get() = _binding!!
    private val noticeViewModel: NoticeViewModel by activityViewModels()
    var searchType = "제목"
    private var sharedUserId = 0
    private var sharedStoreId = 0
    private lateinit var notiAdapter: NotiAdapter
    val sharedPreferencesUtil: SharedPreferencesUtil by lazy {
        SharedPreferencesUtil(requireContext().applicationContext)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNavigation()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeManageBinding.inflate(inflater, container, false)
        Log.d("DEBUG", "onCreateView 시작")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedUserId = sharedPreferencesUtil.getUser().userId!!.toInt()
        sharedStoreId = sharedPreferencesUtil.getStoreId()

        if (sharedPreferencesUtil.getUser().role != "OWNER"){
            binding.addBtn.visibility = View.GONE
        }

        if(noticeViewModel.type.equals("")){
            noticeViewModel.getAllNotice(sharedStoreId,sharedUserId)
        }

        initNotiAdater()
        initSpinner()

        binding.notiFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.writeNotiFragment)
        }

        // 엔터키로 검색 실행
        binding.notiFgSearchET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val searchText = binding.notiFgSearchET.text.toString()
                searchNotice(searchText)
                true
            } else {
                false
            }
        }

        // 버튼 클릭으로 검색 실행
        binding.notiFgSearchBtn.setOnClickListener {
            val searchText = binding.notiFgSearchET.text.toString()
            searchNotice(searchText)
        }

        if (noticeViewModel.noticeList.value == null||noticeViewModel.noticeList.value!!.isEmpty()){
            binding.notiList.visibility = View.GONE
            binding.nothingNoticeBoss.visibility = View.VISIBLE
        }else{
            binding.notiList.visibility = View.VISIBLE
            binding.nothingNoticeBoss.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun initSpinner() {
        val notiSpinner = binding.notiFgSpinner
        val category = arrayOf("제목", "내용")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.order_spinner_item,
            category
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
                val selectedItem = category[position]
                searchType = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initNotiAdater() {
        if (noticeViewModel.noticeList.value.isNullOrEmpty()) {
            noticeViewModel.init(sharedStoreId, sharedUserId)
        }

        binding.notiList.layoutManager = LinearLayoutManager(requireContext())
        notiAdapter = NotiAdapter(emptyList(), object : NotiAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                val noticeList = noticeViewModel.noticeList.value // 현재 공지 리스트 가져오기
                if (!noticeList.isNullOrEmpty() && position in noticeList.indices) {
                    noticeViewModel.setClickNotice(noticeList[position])
                    findNavController().navigate(R.id.writeNotiFragment)
                }
            }
        })

        binding.notiList.adapter = notiAdapter

        noticeViewModel.noticeList.observe(viewLifecycleOwner, { newList ->
            // 기존 데이터를 덮어쓰지 않고 새로운 리스트를 어댑터에 설정
            notiAdapter.noticeList = newList
            notiAdapter.notifyDataSetChanged() // 리스트 업데이트
        })
    }

    // 검색 기능
    private fun searchNotice(keyword: String) {
        if (searchType == "제목") {
            noticeViewModel.searchNoticeTitle(sharedStoreId, keyword)
        } else {
            noticeViewModel.searchNoticeContent(sharedStoreId, keyword)
        }
    }

}
