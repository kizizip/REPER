package com.ssafy.reper.ui.login

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.reper.R
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.databinding.FragmentJoinBinding
import kotlinx.coroutines.launch
import android.graphics.drawable.GradientDrawable
import android.content.Context
import android.content.pm.ActivityInfo
import android.text.Editable
import android.text.TextWatcher
import com.ssafy.reper.data.dto.JoinRequest
import com.ssafy.reper.data.dto.RequestStore
import android.view.animation.AnimationUtils
import com.ssafy.reper.ui.MainActivity


class JoinFragment : Fragment() {

    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!

    private var isEmailError = true
    private var isPasswordError = true
    private var isNameError = true
    private var isPhoneError = true

    private lateinit var loginAcitivty: LoginActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginAcitivty = context as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기에 모든 레이아웃을 숨김
        binding.fragmentJoinTitle.alpha = 0f
        binding.fragmentJoinLinearLayout1.alpha = 0f
        binding.fragmentJoinLinearLayout2.alpha = 0f
        binding.fragmentJoinLinearLayout3.alpha = 0f
        binding.fragmentJoinLinearLayout4.alpha = 0f
        binding.fragmentJoinLinearLayout5.alpha = 0f
        binding.FragmentJoinStoreInfo.alpha = 0f
        binding.FragmentJoinJoinBtnText.alpha = 0f  // 필수 입력 항목 텍스트 추가
        binding.FragmentJoinJoinBtn.alpha = 0f

        // 순차적으로 애니메이션 실행
        startSequentialAnimation()

        // 소셜로그인으로 회원가입시
        val socialEmail = arguments?.getString("email")
        val socialNickname = arguments?.getString("nickname")
        val social = arguments?.getString("social")

        if (socialEmail != null && socialNickname != null) {
            // 이메일과 닉네임 설정
            binding.FragmentJoinEmailInput.apply {
                setText(socialEmail)
                setTextColor(Color.BLACK)  // 텍스트 색상을 검은색으로 설정
            }
            binding.FragmentJoinNameInput.apply {
                setText(socialNickname)
                setTextColor(Color.BLACK)  // 텍스트 색상을 검은색으로 설정
            }

            // 이메일과 이름 입력 필드를 수정 불가능하게 설정
            binding.FragmentJoinEmailInput.isEnabled = false
            binding.FragmentJoinNameInput.isEnabled = false

            // 이메일 중복 확인 버튼 비활성화
            binding.FragmentJoinEmailCheckButton.isEnabled = false
            binding.FragmentJoinEmailCheckButton.alpha = 0.5f

            // 이메일과 이름 에러 상태 해제
            isEmailError = false
            isNameError = false
        }

        // 이메일 중복 체크
        binding.FragmentJoinEmailCheckButton.setOnClickListener {
            val email = binding.FragmentJoinEmailInput.text.toString()

            if (email.isEmpty()) {
                updateEmailInputState(
                    isError = true,
                    message = "이메일을 입력해주세요."
                )
                isEmailError = true
                return@setOnClickListener
            }


            lifecycleScope.launch {
                try {
                    val isDuplicated = RetrofitUtil.authService.checkEmail(email)
                    if (isDuplicated) {
                        updateEmailInputState(
                            isError = true,
                            message = "이미 존재하는 이메일입니다."
                        )
                        isEmailError = true
                    } else {
                        updateEmailInputState(
                            isError = false,

                            message = "사용가능한 이메일입니다."
                        )
                        isEmailError = false
                    }
                } catch (e: Exception) {
                    updateEmailInputState(
                        isError = true,
                        message = "이메일 중복 체크 실패"
                    )
                    isEmailError = true
                }
            }

        }

        // 비밀번호 
        binding.FragmentJoinPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                // 최소 8자 이상
                //영문 대/소문자, 숫자, 특수문자 포함 여부 체크 로직
                /**
                 *         "Password1!",   // ✅ 유효한 비밀번호
                 *         "pass1!",       // ❌ 8자 미만
                 *         "PASSWORD1!",   // ❌ 소문자 없음
                 *         "password1!",   // ❌ 대문자 없음
                 *         "Password!",    // ❌ 숫자 없음
                 *         "Password1"     // ❌ 특수문자 없음
                 */
                val passwordPattern = Regex("^(?=.*[a-z])(?=.*\\d)[a-z\\d@\$!%*?&]{8,}\$")
                val password = s.toString()

                // 비밀번호 입력 칸이 빈칸이 아니고, 입력 패턴에 맞지 않는다면 작동
                if (password.isNotEmpty() && !password.matches(passwordPattern)) {
                    // 에러 상태로 변경
                    binding.FragmentJoinPasswordText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinPasswordError.visibility = View.VISIBLE
                    binding.FragmentJoinPasswordInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                    isPasswordError = true
                } else {
                    // 정상 상태로 복구
                    binding.FragmentJoinPasswordText.setTextColor(Color.parseColor("#000000"))
                    binding.FragmentJoinPasswordError.visibility = View.GONE
                    binding.FragmentJoinPasswordInput.apply {
                        setHintTextColor(Color.parseColor("#C7C7C7"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#000000"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                    isPasswordError = false
                }
            }
        })


        // 이름
        binding.FragmentJoinNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()

                // 이름 입력 칸이 빈칸이 아니고, 입력 패턴에 맞지 않는다면 작동
                if (name.isEmpty()) {
                    binding.FragmentJoinNameText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinNameError.visibility = View.VISIBLE
                    binding.FragmentJoinNameInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                    isNameError = true
                } else {
                    binding.FragmentJoinNameText.setTextColor(Color.parseColor("#000000"))
                    binding.FragmentJoinNameError.visibility = View.GONE
                    binding.FragmentJoinNameInput.apply {
                        setHintTextColor(Color.parseColor("#C7C7C7"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#000000"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                    isNameError = false
                }
            }
        })

        // 전화번호
        binding.FragmentJoinPhoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val phoneRegex = Regex("^01[0-9]\\d{3,4}\\d{4}$")
                val phone = s.toString()

                if (phone.isEmpty()) {
                    isPhoneError = true
                    binding.FragmentJoinPhoneText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinPhoneError.visibility = View.VISIBLE
                    binding.FragmentJoinPhoneInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                } else if (!phone.matches(phoneRegex)) {
                    isPhoneError = true
                    binding.FragmentJoinPhoneText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinPhoneError.visibility = View.VISIBLE
                    binding.FragmentJoinPhoneInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                } else {
                    isPhoneError = false
                    binding.FragmentJoinPhoneText.setTextColor(Color.parseColor("#000000"))
                    binding.FragmentJoinPhoneError.visibility = View.GONE
                    binding.FragmentJoinPhoneInput.apply {
                        setHintTextColor(Color.parseColor("#C7C7C7"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#000000"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                }
            }
        })


        // Spinner 설정
        val spinner = binding.FragmentJoinSpinnerUserType
        val userTypes = arrayOf("직원", "사장님")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.join_spinner_item,
            userTypes
        ).apply {
            setDropDownViewResource(R.layout.join_spinner_item)
        }

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = userTypes[position]
                if (selectedItem == "사장님") {
                    showStoreInfoWithAnimation()
                } else {
                    hideStoreInfoWithAnimation()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        }


        // Join 버튼 클릭시
        binding.FragmentJoinJoinBtn.setOnClickListener {
            // 회원가입 로직
            if (isEmailError || isPasswordError || isNameError || isPhoneError) {
                Toast.makeText(requireContext(), "입력하신 내용을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                if (isEmailError) {
                    binding.FragmentJoinEmailText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinEmailInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                }
                if (isPasswordError) {
                    binding.FragmentJoinPasswordText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinPasswordInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                }
                if (isNameError) {
                    binding.FragmentJoinNameText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinNameInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                }
                if (isPhoneError) {
                    binding.FragmentJoinPhoneText.setTextColor(Color.parseColor("#F26547"))
                    binding.FragmentJoinPhoneInput.apply {
                        setHintTextColor(Color.parseColor("#F26547"))
                        background = GradientDrawable().apply {
                            setStroke(1.dpToPx(context), Color.parseColor("#F26547"))
                            cornerRadius = 12.dpToPx(context).toFloat()
                            setColor(Color.WHITE)
                        }
                    }
                }

                return@setOnClickListener
            } else {
                // 회원가입 로직
                val username = binding.FragmentJoinNameInput.text.toString()
                val email = binding.FragmentJoinEmailInput.text.toString()
                val password = binding.FragmentJoinPasswordInput.text.toString()
                val phone = binding.FragmentJoinPhoneInput.text.toString()
                val role =
                    if (binding.FragmentJoinSpinnerUserType.selectedItem.toString() == "사장님") "OWNER" else "STAFF"

                lifecycleScope.launch {
                    try {
                        // JoinRequest 객체 생성
                        val joinRequest = JoinRequest(
                            email = email,
                            password = password,
                            userName = username,
                            // 앞 3글자 뒤와 맨 뒤에서 4번째 글자에 하이픈(-)을 추가
                            phone = StringBuilder(phone).insert(3, "-")
                                .insert(phone.length - 3, "-").toString(),
                            role = role,
                        )
                        val response = RetrofitUtil.authService.join(joinRequest)

                        if (binding.FragmentJoinStoreInfoInput.text.toString().isNotEmpty()) {
                            val storeRequest = RequestStore(
                                ownerId = response,
                                storeName = binding.FragmentJoinStoreInfoInput.text.toString()
                            )
                            val storeResponse = RetrofitUtil.storeService.addStore(storeRequest)
                        }

                        if (social == "kakao") {
                            RetrofitUtil.authService.updateKakaoBoolean(response)
                        } else if (social == "google") {
                            RetrofitUtil.authService.updateGoogleBoolean(response)
                        }

                        Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()

                        // LoginFragment로 이동
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.activityLoginFragmentContainer, LoginFragment())
                            .commit()


                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "회원가입 실패 잠시후 재시도해 주세요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loginAcitivty.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }

    private fun updateEmailInputState(isError: Boolean, message: String) {

        val color = if (isError) "#F26547" else "#000000"
        val hintColor = if (isError) "#F26547" else "#C7C7C7"

        binding.FragmentJoinEmailText.setTextColor(Color.parseColor(color))
        binding.FragmentJoinEmailInput.apply {
            if (isError) {
                setText("")  // 에러 상태일 때 입력 필드를 비움
            }
            hint = message
            setHintTextColor(Color.parseColor(hintColor))
            background = GradientDrawable().apply {
                setStroke(1.dpToPx(context), Color.parseColor(color))
                cornerRadius = 12.dpToPx(context).toFloat()
                setColor(Color.WHITE)
            }
        }

        if (!isError) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    // Fragment 클래스 내에 확장 함수 추가
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun startSequentialAnimation() {
        // 각 레이아웃별로 새로운 애니메이션 인스턴스 생성
        val fadeSlideUpTitle = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp1 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp2 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp3 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp4 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp5 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUpText = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUpBtn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        // 타이틀 애니메이션
        binding.fragmentJoinTitle.postDelayed({
            binding.fragmentJoinTitle.alpha = 1f
            binding.fragmentJoinTitle.startAnimation(fadeSlideUpTitle)
        }, 200)

        // 첫 번째 레이아웃 애니메이션
        binding.fragmentJoinLinearLayout1.postDelayed({
            binding.fragmentJoinLinearLayout1.alpha = 1f
            binding.fragmentJoinLinearLayout1.startAnimation(fadeSlideUp1)
        }, 500)

        // 두 번째 레이아웃 애니메이션
        binding.fragmentJoinLinearLayout2.postDelayed({
            binding.fragmentJoinLinearLayout2.alpha = 1f
            binding.fragmentJoinLinearLayout2.startAnimation(fadeSlideUp2)
        }, 800)

        // 세 번째 레이아웃 애니메이션
        binding.fragmentJoinLinearLayout3.postDelayed({
            binding.fragmentJoinLinearLayout3.alpha = 1f
            binding.fragmentJoinLinearLayout3.startAnimation(fadeSlideUp3)
        }, 1100)

        // 네 번째 레이아웃 애니메이션
        binding.fragmentJoinLinearLayout4.postDelayed({
            binding.fragmentJoinLinearLayout4.alpha = 1f
            binding.fragmentJoinLinearLayout4.startAnimation(fadeSlideUp4)
        }, 1400)

        // 다섯 번째 레이아웃 애니메이션
        binding.fragmentJoinLinearLayout5.postDelayed({
            binding.fragmentJoinLinearLayout5.alpha = 1f
            binding.fragmentJoinLinearLayout5.startAnimation(fadeSlideUp5)
        }, 1700)

        // 필수 입력 항목 텍스트 애니메이션
        binding.FragmentJoinJoinBtnText.postDelayed({
            binding.FragmentJoinJoinBtnText.alpha = 1f
            binding.FragmentJoinJoinBtnText.startAnimation(fadeSlideUpText)
        }, 2000)

        // Join 버튼 애니메이션
        binding.FragmentJoinJoinBtn.postDelayed({
            binding.FragmentJoinJoinBtn.alpha = 1f
            binding.FragmentJoinJoinBtn.startAnimation(fadeSlideUpBtn)
        }, 2300)
    }

    // 스토어 정보 레이아웃을 보여주는 애니메이션
    private fun showStoreInfoWithAnimation() {
        binding.FragmentJoinStoreInfo.visibility = View.VISIBLE
        binding.FragmentJoinStoreInfo.alpha = 0f

        val fadeSlideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        binding.FragmentJoinStoreInfo.startAnimation(fadeSlideUp)
        binding.FragmentJoinStoreInfo.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    // 스토어 정보 레이아웃을 숨기는 애니메이션
    private fun hideStoreInfoWithAnimation() {
        val fadeSlideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        binding.FragmentJoinStoreInfo.startAnimation(fadeSlideDown)

        binding.FragmentJoinStoreInfo.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                binding.FragmentJoinStoreInfo.visibility = View.GONE
            }
            .start()
    }

}
