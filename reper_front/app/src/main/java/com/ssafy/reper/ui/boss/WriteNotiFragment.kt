package com.ssafy.reper.ui.boss

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.Notice
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.databinding.FragmentWriteNotiBinding
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.MainActivity
import kotlinx.coroutines.launch

private const val TAG = "WriteNotiFragment"
class WriteNotiFragment : Fragment() {
    private var _binding: FragmentWriteNotiBinding? = null
    private val binding get() = _binding!!
    private val noticeViewModel: NoticeViewModel by activityViewModels()
    private val fcmViewModel: FcmViewModel by activityViewModels()
    val sharedPreferencesUtil: SharedPreferencesUtil by lazy {
        SharedPreferencesUtil(requireContext().applicationContext)
    }
    var sharedUserId = 0
    var sharedStoreId = 0

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteNotiBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).hideBottomNavigation()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedStoreId = sharedPreferencesUtil.getStoreId()
        sharedUserId = sharedPreferencesUtil.getUser().userId!!.toInt()

        noticeViewModel.type = "noticeDetail"

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
            viewLifecycleOwner.lifecycleScope.launch {
                if (noticeViewModel.clickNotice.value != null) {
                    modifyNotice()
                    parentFragmentManager.popBackStack()
                } else {
                    createNotice()  // 공지 생성이 완료될 때까지 기다림
                    parentFragmentManager.popBackStack()
                    findNavController().navigate(R.id.noticeManageFragment)
                }
            }
        }


        binding.notiWriteFgDeleteTv.setOnClickListener {
            noticeViewModel.deleteNotice(sharedUserId, noticeViewModel.clickNotice.value!!.noticeId,
                 mapOf("userId" to sharedUserId) )

            parentFragmentManager.popBackStack()
            Toast.makeText(requireContext(), "공지 삭제 완료", Toast.LENGTH_SHORT).show()

        }

    }

    //이부분이 사장님과 직원일때 달라져야하는 부분  직원 -> 삭제 버튼, 수정, 저장 아무 것도 안뜨게

    fun detailNotice(notice: Notice) {
        binding.textView.text = "공지 상세"
        binding.notiWriteFgTitleTV.text = notice.title
        binding.notiWriteFgContentTV.text = notice.content
        binding.notiFgDate.text = notice.updatedAt
        binding.notiFgDate.visibility = View.VISIBLE


        binding.notiWriteFgContentET.visibility = View.GONE
        binding.notiWriteFgTitleET.visibility = View.GONE
        binding.notiWriteFgSaveBtn.visibility = View.INVISIBLE

        binding.notiWriteFgTitleTV.visibility = View.VISIBLE
        binding.notiWriteFgContentTV.visibility = View.VISIBLE
        binding.notiWriteFgModifyBtn.visibility = View.VISIBLE


        binding.notiWriteFgDeleteTv.visibility = View.VISIBLE
        if (sharedPreferencesUtil.getUser().role !="OWNER"){
            binding.notiWriteFgModifyBtn.visibility = View.GONE
            binding.notiWriteFgDeleteTv.visibility = View.INVISIBLE
            binding.notiWriteFgSaveBtn.visibility = View.INVISIBLE
        }


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
        binding.notiFgDate.visibility = View.INVISIBLE


        if (noticeViewModel.clickNotice.value != null) {
            binding.textView.text = "공지 수정"
            val notice = noticeViewModel.clickNotice.value
            binding.notiWriteFgTitleET.setText(notice!!.title)
            binding.notiWriteFgContentET.setText(notice.content)
        }
        if (sharedPreferencesUtil.getUser().role !="OWNER"){
            binding.notiWriteFgModifyBtn.visibility = View.GONE
            binding.notiWriteFgDeleteTv.visibility = View.INVISIBLE
            binding.notiWriteFgSaveBtn.visibility = View.INVISIBLE
        }

    }


    suspend fun createNotice() {
        val title = binding.notiWriteFgTitleET.text.toString()
        val content = binding.notiWriteFgContentET.text.toString()
        val targetFragment = "WriteNoticeFragment"
        val requestId = noticeViewModel.createNotice(sharedStoreId, sharedUserId, title, content)?.noticeId
        requestId?.let { id ->  // requestId가 null이 아닐 때만 실행
            fcmViewModel.sendToStoreFCM(sharedStoreId, "새로운 공지가 등록되었습니다.", title, targetFragment, id)
            Toast.makeText(requireContext(), "공지 작성 완료", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "createNotice: ${id}")
        } ?: run {
            Toast.makeText(requireContext(), "공지 작성 실패", Toast.LENGTH_SHORT).show()
        }
    }


    fun modifyNotice() {
        val title = binding.notiWriteFgTitleET.text.toString()
        val content = binding.notiWriteFgContentET.text.toString()

        val targetFragment = "WriteNoticeFragment"  // 이동할 프래그먼트 이름
        val requestId = noticeViewModel.clickNotice.value!!.noticeId  // 수정된 공지의 ID,
        Log.d(TAG, "modifyNotice: ${requestId}")

        noticeViewModel.modifyNotice(
            sharedStoreId, sharedUserId,
            noticeViewModel.clickNotice.value!!.noticeId, title, content
        )
        fcmViewModel.sendToStoreFCM(sharedStoreId,"공지가 수정되었습니다.",title, targetFragment, requestId)

        Toast.makeText(requireContext(), "공지 수정 완료", Toast.LENGTH_SHORT).show()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        noticeViewModel.setClickNotice(null)
    }
}