package com.ssafy.reper.ui.mypage

import MyAccessStoreListAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraExtensionSession.StillCaptureLatency
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.ssafy.reper.R
import com.ssafy.reper.data.dto.UserInfo
import com.ssafy.reper.data.local.SharedPreferencesUtil
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.databinding.FragmentEditMyAccountBinding

import com.ssafy.reper.ui.MainActivity
import com.ssafy.reper.ui.login.LoginActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import com.ssafy.reper.data.dto.UpdateUserRequest
import android.util.Log
import com.ssafy.reper.data.dto.Store
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.ssafy.reper.data.dto.StoreResponse
import com.ssafy.reper.ui.FcmViewModel
import com.ssafy.reper.ui.home.StoreViewModel

private const val TAG = "EditMyAccountFragment"

class EditMyAccountFragment : Fragment() {

    // MainActivity 참조를 위한 변수
    private lateinit var mainActivity: MainActivity

    // 사용자가 접근 권한을 가진 매장 목록을 표시하기 위한 RecyclerView 어댑터
    private lateinit var myAccessStoreListAdapter: MyAccessStoreListAdapter

    // 뷰 바인딩 객체 - nullable로 선언
    private var _editMyAccountBinding: FragmentEditMyAccountBinding? = null

    // 뷰 바인딩 객체 접근을 위한 getter (non-null)
    private val editMyAccountBinding get() = _editMyAccountBinding!!

    // userInfo 변수 추가
    private lateinit var userInfo: UserInfo

    // SharedPreferencesUtil 추가
    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private val fcmViewModel: FcmViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by activityViewModels()


    // Fragment가 Activity에 붙을 때 호출되는 생명주기 메서드
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        // SharedPreferencesUtil 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _editMyAccountBinding = FragmentEditMyAccountBinding.inflate(inflater, container, false)
        return editMyAccountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavigation()

        // 초기에 모든 요소 숨김
        editMyAccountBinding.editmyaccountFmBtnBack.alpha = 0f
        editMyAccountBinding.textView9.alpha = 0f

        editMyAccountBinding.textView12.alpha = 0f
        editMyAccountBinding.FragmentJoinPhoneError.alpha = 0f
        editMyAccountBinding.editmyaccountFmEtId.alpha = 0f

        editMyAccountBinding.textView13.alpha = 0f
        editMyAccountBinding.editmyaccountFmEtName.alpha = 0f

        editMyAccountBinding.textView14.alpha = 0f
        editMyAccountBinding.editmyaccountFmEtTel.alpha = 0f

        editMyAccountBinding.editmyaccountFmTvMyaccess.alpha = 0f
        editMyAccountBinding.editmyaccountFmSv.alpha = 0f

        editMyAccountBinding.editmyaccountFmFlTenEdit.alpha = 0f
        editMyAccountBinding.editmyaccountFmTvDeleteaccount.alpha = 0f

        // 순차적 애니메이션 시작
        startSequentialAnimation()

        // 초기 상태: 로딩 화면 표시, 메인 콘텐츠 숨김
        showLoading()
        editMyAccountBinding.editmyaccountFmMainContent.visibility = View.GONE

        // 회원 정보 가져오기
        val savedUser = sharedPreferencesUtil.getUser()

        // 코루틴 스코프에서 사용자 정보 가져오기
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userId = savedUser.userId
                if (userId != null) {
                    val response = RetrofitUtil.authService.getUserInfo(userId)

                    // userInfo 초기화
                    userInfo = UserInfo(
                        userId = userId,
                        username = response.username,
                        role = response.role
                    )

                    // UI 업데이트
                    editMyAccountBinding.editmyaccountFmEtId.setText(response.email)
                    editMyAccountBinding.editmyaccountFmEtName.setText(response.username)
                    editMyAccountBinding.editmyaccountFmEtTel.setText(response.phone)

                    // 수정버튼 클릭시
                    editMyAccountBinding.editmyaccountFmFlTenEdit.setOnClickListener {
                        patchUser(userInfo)
                    }

                    // RecyclerView adapter 처리
                    initAdapter()
                } else {
                    Toast.makeText(context, "유효하지 않은 사용자 ID입니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "사용자 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            } finally {
                // 로딩 완료: 로딩 화면 숨김, 메인 콘텐츠 표시
                hideLoading()
                editMyAccountBinding.editmyaccountFmMainContent.visibility = View.VISIBLE
            }
        }

        initEvent()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }

    // 로딩 화면 표시
    private fun showLoading() {
        editMyAccountBinding.editmyaccountFmLoadingLayout.visibility = View.VISIBLE
    }

    // 로딩 화면 숨김
    private fun hideLoading() {
        editMyAccountBinding.editmyaccountFmLoadingLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _editMyAccountBinding = null
    }

    // 회원 정보 수정
    private fun patchUser(userInfo: UserInfo) {
        // dialog 표시
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_patch_userinfo)

        // 취소 버튼 클릭 시 다이얼로그 닫기
        dialog.findViewById<View>(R.id.logout_d_btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        // 수정 버튼 클릭 시 회원 정보 수정 처리
        dialog.findViewById<View>(R.id.logout_d_btn_positive).setOnClickListener {
            // 코루틴 스코프에서 회원 정보 수정 요청 실행
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // SharedPreferences 업데이트
                    val user = sharedPreferencesUtil.addUser(
                        UserInfo(
                            username = editMyAccountBinding.editmyaccountFmEtName.text.toString(),
                            role = userInfo.role,
                            userId = userInfo.userId
                        )
                    )

                    // 회원 정보 수정 요청
                    val phone = editMyAccountBinding.editmyaccountFmEtTel.text.toString()

                    // 하이픈이 없는 경우에만 추가
                    val formattedPhone = if (!phone.contains("-")) {
                        StringBuilder(phone)
                            .insert(3, "-")
                            .insert(phone.length - 3, "-")
                            .toString()
                    } else {
                        phone
                    }

                    RetrofitUtil.authService.updateUser(
                        userInfo.userId,
                        UpdateUserRequest(
                            userName = editMyAccountBinding.editmyaccountFmEtName.text.toString(),
                            phone = formattedPhone
                        )
                    )

                    // 수정 완료 후 토스트 메시지 표시
                    Toast.makeText(requireContext(), "회원 정보 수정 완료", Toast.LENGTH_SHORT).show()

                    // 수정 완료 후 이전 화면으로 돌아감
                    parentFragmentManager.popBackStack()
                    dialog.dismiss()
                } catch (e: Exception) {
                    // 에러 처리
                    Toast.makeText(requireContext(), "회원 정보 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        dialog.show()
    }

    // 화면의 이벤트 처리를 초기화하는 메서드
    private fun initEvent() {
        // 뒤로가기 버튼 클릭 시 이전 화면으로 돌아감
        editMyAccountBinding.editmyaccountFmBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 회원 탈퇴 텍스트 클릭 시 삭제 확인 다이얼로그 표시
        editMyAccountBinding.editmyaccountFmTvDeleteaccount.setOnClickListener {
            val dialog = Dialog(mainActivity)
            // 다이얼로그 배경을 투명하게 설정
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_delete_account)

            // 취소 버튼 클릭 시 다이얼로그 닫기
            dialog.findViewById<View>(R.id.deleteaccount_d_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            // 삭제 버튼 클릭 시 로그아웃 처리 및 로그인 화면으로 이동
            dialog.findViewById<View>(R.id.deleteaccount_d_btn_delete).setOnClickListener {
                // CoroutineScope를 사용하여 suspend 함수 호출
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        // 회원 탈퇴 요청
                        val userId = sharedPreferencesUtil.getUser().userId
                        if (userId != null) {
                            RetrofitUtil.authService.deleteUser(userId)
                        }

                        // 로그아웃 처리
                        // 사용자 쿠키 및 정보 삭제
                        sharedPreferencesUtil.clearUserData()

                        Toast.makeText(context, "회원 탈퇴 완료", Toast.LENGTH_SHORT).show()

                        dialog.dismiss()
                        startActivity(Intent(mainActivity, LoginActivity::class.java))
                        mainActivity.finish()
                    } catch (e: Exception) {
                        // 에러 처리
                        Toast.makeText(context, "회원 탈퇴 처리 중 오류가 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog.show()
        }
    }

    // RecyclerView 어댑터 초기화 및 설정

    fun initAdapter() {
        myAccessStoreListAdapter = MyAccessStoreListAdapter(emptyList()) { store, _ ->
            lifecycleScope.launch {
                try {
                    val storeInfo = storeViewModel.getStore(store.storeId)
                    if (storeInfo != null) {
                        showDailog(storeInfo)
                    } else {
                        Toast.makeText(requireContext(), "가게 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting store info: ${e.message}")
                    Toast.makeText(requireContext(), "가게 정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        editMyAccountBinding.editmyaccountFmRv.apply {
            layoutManager = LinearLayoutManager(requireContext()) // RecyclerView 레이아웃 설정
            adapter = myAccessStoreListAdapter

        }

        storeViewModel.myStoreList.observe(viewLifecycleOwner) { storeList ->
            myAccessStoreListAdapter.updateData(storeList ?: emptyList()) // 리스트 갱신
        }
    }

    fun showDailog(store: StoreResponse){
        val dialog = Dialog(mainActivity)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_delete)

        // 텍스트를 변경하려는 TextView 찾기
        val nameTextView = dialog.findViewById<TextView>(R.id.dialog_delete_bold_tv)
        val middleTV = dialog.findViewById<TextView>(R.id.dialog_delete_rle_tv)

        // 텍스트 변경
        nameTextView.text = "${store.storeName}"
        middleTV.text = "을"


        dialog.findViewById<View>(R.id.dialog_delete_cancle_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.dialog_delete_delete_btn).setOnClickListener {
            storeViewModel.deleteStore(store.storeId, sharedPreferencesUtil.getUser().userId!!.toInt())
            fcmViewModel.sendToUserFCM(
                store.ownerId,
                "권한 삭제 알림",
                "${sharedPreferencesUtil.getUser().username}님께서 ${store.storeName}의 권한을 삭제했습니다.",
                "BossFragment",
                store.storeId
            ).let {
                if (it.equals("성공")) {

                }
                Toast.makeText(requireContext(), "가게 삭제 완료", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun startSequentialAnimation() {
        // 각 애니메이션 실행 전에 바인딩 객체가 유효한지 확인
        val binding = _editMyAccountBinding ?: return

        // 첫 번째 그룹 (뒤로가기 버튼, 타이틀)
        binding.editmyaccountFmBtnBack.postDelayed({
            if (_editMyAccountBinding == null) return@postDelayed
            binding.editmyaccountFmBtnBack.alpha = 1f
            binding.editmyaccountFmBtnBack.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
            binding.textView9.alpha = 1f
            binding.textView9.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
        }, 200)

        // 두 번째 그룹 (아이디 관련)
        binding.textView12.postDelayed({
            if (_editMyAccountBinding == null) return@postDelayed
            binding.textView12.alpha = 1f
            binding.textView12.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
            binding.FragmentJoinPhoneError.alpha = 1f
            binding.FragmentJoinPhoneError.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
            binding.editmyaccountFmEtId.alpha = 1f
            binding.editmyaccountFmEtId.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
        }, 500)

        // 세 번째 그룹 (이름 관련)
        binding.textView13.postDelayed({
            if (_editMyAccountBinding == null) return@postDelayed
            binding.textView13.alpha = 1f
            binding.textView13.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
            binding.editmyaccountFmEtName.alpha = 1f
            binding.editmyaccountFmEtName.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
        }, 800)

        // 네 번째 그룹 (전화번호 관련)
        binding.textView14.postDelayed({
            if (_editMyAccountBinding == null) return@postDelayed
            binding.textView14.alpha = 1f
            binding.textView14.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
            binding.editmyaccountFmEtTel.alpha = 1f
            binding.editmyaccountFmEtTel.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
        }, 1100)

        // 다섯 번째 그룹 (내 권한 관련)
        binding.editmyaccountFmTvMyaccess.postDelayed({
            if (_editMyAccountBinding == null) return@postDelayed
            binding.editmyaccountFmTvMyaccess.alpha = 1f
            binding.editmyaccountFmTvMyaccess.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
            binding.editmyaccountFmSv.alpha = 1f
            binding.editmyaccountFmSv.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
        }, 1400)

        // 여섯 번째 그룹 (수정 버튼, 회원탈퇴)
        binding.editmyaccountFmFlTenEdit.postDelayed({
            if (_editMyAccountBinding == null) return@postDelayed
            binding.editmyaccountFmFlTenEdit.alpha = 1f
            binding.editmyaccountFmFlTenEdit.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
            binding.editmyaccountFmTvDeleteaccount.alpha = 1f
            binding.editmyaccountFmTvDeleteaccount.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fade_slide_up
                )
            )
        }, 1700)
    }
}
