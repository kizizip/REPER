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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.databinding.FragmentJoinBinding
import kotlinx.coroutines.launch
import android.graphics.drawable.GradientDrawable
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.ssafy.reper.data.dto.JoinRequest
import com.ssafy.reper.data.dto.RequestStore


class JoinFragment : Fragment() {

    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!

    private var isEmailError = true
    private var isPasswordError = true
    private var isNameError = true
    private var isPhoneError = true

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
                // 선택된 항목 처리
                if (selectedItem == "사장님") {
                    binding.FragmentJoinStoreInfo.visibility = View.VISIBLE
                } else {
                    binding.FragmentJoinStoreInfo.visibility = View.GONE
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
                if(isEmailError) {
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
                val role = if(binding.FragmentJoinSpinnerUserType.selectedItem.toString() == "사장님") "OWNER" else "STAFF"

                lifecycleScope.launch {
                    try {
                        // JoinRequest 객체 생성
                        val joinRequest = JoinRequest(
                            email = email,
                            password = password,
                            userName = username,
                            // 앞 3글자 뒤와 맨 뒤에서 4번째 글자에 하이픈(-)을 추가
                            phone = StringBuilder(phone).insert(3, "-").insert(phone.length - 3, "-").toString(),
                            role = role,
                        )
                        val response = RetrofitUtil.authService.join(joinRequest)

                        if (binding.FragmentJoinStoreInfoInput.text.toString().isNotEmpty()) {
                            val storeRequest = RequestStore(
                                ownerId = response,
                                storeName = binding.FragmentJoinStoreInfoInput.text.toString()
                            )
                            val storeResponse = RetrofitUtil.bossService.addStore(storeRequest)
                        }

                        Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()

                        // LoginFragment로 이동
                        parentFragmentManager.beginTransaction()
                        .replace(R.id.activityLoginFragmentContainer, LoginFragment())
                        .commit()


                    } catch (e: Exception) {    
                        Toast.makeText(requireContext(), "회원가입 실패 잠시후 재시도해 주세요", Toast.LENGTH_SHORT).show()
                    }
                }   
            }
        }
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

}
