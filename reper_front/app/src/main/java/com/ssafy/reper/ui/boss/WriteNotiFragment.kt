package com.ssafy.reper.ui.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Notice
import com.ssafy.reper.data.dto.NoticeRequest
import com.ssafy.reper.databinding.FragmentNoticeManageBinding
import com.ssafy.reper.databinding.FragmentRecipeManageBinding
import com.ssafy.reper.databinding.FragmentWriteNotiBinding
import com.ssafy.reper.ui.MainActivity


class WriteNotiFragment : Fragment() {
    private var _binding: FragmentWriteNotiBinding? = null
    private val binding get() = _binding!!
    private val noticeViewModel: NoticeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteNotiBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).hideBottomNavigation()

        //고객정보 사장인지 지원인지 따라 분기처리 필요 우선은 사장이라 생각하고 만듭니당
        if (noticeViewModel.clickNotice.value != null) {
            var notice = noticeViewModel.clickNotice.value
            detailNotice(notice!!)

        } else {
            writeNotice()
        }

        binding.notiWriteFgBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.notiWriteFgModifyBtn.setOnClickListener {
            writeNotice()
        }

        binding.notiWriteFgSaveBtn.setOnClickListener {
            if (noticeViewModel.clickNotice.value != null) {
                modifyNotice()
                parentFragmentManager.popBackStack()
            } else {
                createNotice()
                parentFragmentManager.popBackStack()
                findNavController().navigate(R.id.noticeManageFragment)
            }

        }

        binding.notiWriteFgDeleteTv.setOnClickListener {
            noticeViewModel.deleteNotice(1, noticeViewModel.clickNotice.value!!.noticeId,
                 mapOf("userId" to 1) )

            parentFragmentManager.popBackStack()

        }
    }

    //이부분이 사장님과 직원일때 달라져야하는 부분  직원 -> 삭제 버튼, 수정, 저장 아무 것도 안뜨게

    fun detailNotice(notice: Notice) {
        binding.textView.text = "공지 상세"
        binding.notiWriteFgTitleTV.text = notice.title
        binding.notiWriteFgContentTV.text = notice.content

        binding.notiWriteFgContentET.visibility = View.GONE
        binding.notiWriteFgTitleET.visibility = View.GONE
        binding.notiWriteFgSaveBtn.visibility = View.GONE

        binding.notiWriteFgTitleTV.visibility = View.VISIBLE
        binding.notiWriteFgContentTV.visibility = View.VISIBLE
        binding.notiWriteFgModifyBtn.visibility = View.VISIBLE


        binding.notiWriteFgDeleteTv.visibility = View.VISIBLE

    }

    fun writeNotice() {
        binding.textView.text = "공지 쓰기"
        binding.notiWriteFgContentET.visibility = View.VISIBLE
        binding.notiWriteFgTitleET.visibility = View.VISIBLE
        binding.notiWriteFgSaveBtn.visibility = View.VISIBLE

        binding.notiWriteFgTitleTV.visibility = View.GONE
        binding.notiWriteFgContentTV.visibility = View.GONE
        binding.notiWriteFgModifyBtn.visibility = View.GONE
        binding.notiWriteFgDeleteTv.visibility = View.GONE
        binding.notiWriteFgDeleteTv.visibility = View.GONE


        if (noticeViewModel.clickNotice.value != null) {
            binding.textView.text = "공지 수정"
            val notice = noticeViewModel.clickNotice.value
            binding.notiWriteFgTitleET.setText(notice!!.title)
            binding.notiWriteFgContentET.setText(notice.content)
        }

    }


    fun createNotice() {
        val title = binding.notiWriteFgTitleET.text.toString()
        val content = binding.notiWriteFgContentET.text.toString()
        noticeViewModel.createNotice(1, 1, title, content)
    }

    fun modifyNotice() {
        val title = binding.notiWriteFgTitleET.text.toString()
        val content = binding.notiWriteFgContentET.text.toString()
        noticeViewModel.modifyNotice(
            1, 1,
            noticeViewModel.clickNotice.value!!.noticeId, title, content
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        noticeViewModel.setClickNotice(null)
    }
}