package com.ssafy.reper.ui.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.reper.R
import com.ssafy.reper.data.dto.LoginRequest
import com.ssafy.reper.data.remote.RetrofitUtil
import com.ssafy.reper.databinding.FragmentLoginBinding
import com.ssafy.reper.ui.MainActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.google.gson.Gson
import com.ssafy.reper.data.dto.ErrorResponse
import android.graphics.drawable.GradientDrawable
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import com.ssafy.reper.data.dto.JoinRequest
import com.ssafy.reper.data.dto.KakaoLoginRequest
import com.ssafy.reper.data.dto.UserInfo
import com.ssafy.reper.data.local.SharedPreferencesUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val TAG = "LoginFragment_레퍼"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Firebase 인증 객체 선언
    private lateinit var auth: FirebaseAuth

    // Google 로그인 클라이언트 선언
    private lateinit var googleSignInClient: GoogleSignInClient

    // Google 로그인 요청 코드 (onActivityResult에서 요청을 구분하기 위함)
    private val RC_SIGN_IN = 9001

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
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase Auth 객체 초기화
        auth = Firebase.auth

        // Google 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Google OAuth 2.0 클라이언트 ID 요청
            .requestEmail() // 사용자 이메일 요청
            .build()

        // Google 로그인 클라이언트 초기화
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // 초기에 모든 레이아웃을 숨김
        binding.fragmentLoginLinearLayout1.alpha = 0f
        binding.fragmentLoginLinearLayout2.alpha = 0f
        binding.fragmentLoginLinearLayout3.alpha = 0f
        binding.fragmentLoginLinearLayout4.alpha = 0f

        // 순차적으로 애니메이션 실행
        startSequentialAnimation()

        var keyHash = Utility.getKeyHash(requireContext())
        Log.i("kjwTest", "keyHash: $keyHash")


        // 로그인 버튼 클릭 시 로그인 처리
        val loginBtn = binding.fragmentLoginLoginBtn
        loginBtn.setOnClickListener {

            // 로그인 요청 호출
            login(
                binding.fragmentLoginEamilInput.text.toString(),
                binding.fragmentLoginPasswordInput.text.toString()
            )

        }

        // ActivityLoginJoinText 클릭 시 새로운 LoginFragment로 이동
        val joinText = binding.ActivityLoginJoinText
        joinText.setOnClickListener {
            // JoinFragment로 이동
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.activityLoginFragmentContainer,
                    JoinFragment()
                ) // container ID와 새로운 fragment를 설정
                .addToBackStack(null) // BackStack에 추가 (뒤로가기 시 이전 Fragment로 돌아갈 수 있음)
                .commit()
        }

        // 소셜 로그인(카카오)
        binding.fragmentLoginKakaoLoginIcon.setOnClickListener {
            // 카카오 로그인 요청 호출
            loginWithKakao()
        }

        // 소셜 로그인(구글)
        binding.fragmentLoginGoogleLoginIcon.setOnClickListener {
            // 구글 로그인 요청 호출
            loginWithGoogle()
        }
    }

    override fun onResume() {
        super.onResume()
        loginAcitivty.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 회전 잠금
    }

    private fun startSequentialAnimation() {
        // 각 레이아웃별로 새로운 애니메이션 인스턴스 생성
        val fadeSlideUp1 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp2 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp3 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)
        val fadeSlideUp4 = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        // 첫 번째 레이아웃 애니메이션
        binding.fragmentLoginLinearLayout1.postDelayed({
            binding.fragmentLoginLinearLayout1.alpha = 1f
            binding.fragmentLoginLinearLayout1.startAnimation(fadeSlideUp1)
        }, 200)

        // 두 번째 레이아웃 애니메이션
        binding.fragmentLoginLinearLayout2.postDelayed({
            binding.fragmentLoginLinearLayout2.alpha = 1f
            binding.fragmentLoginLinearLayout2.startAnimation(fadeSlideUp2)
        }, 500)  // 간격 축소: 700 -> 500

        // 세 번째 레이아웃 애니메이션
        binding.fragmentLoginLinearLayout3.postDelayed({
            binding.fragmentLoginLinearLayout3.alpha = 1f
            binding.fragmentLoginLinearLayout3.startAnimation(fadeSlideUp3)
        }, 800)  // 간격 축소: 1200 -> 800

        // 네 번째 레이아웃 애니메이션
        binding.fragmentLoginLinearLayout4.postDelayed({
            binding.fragmentLoginLinearLayout4.alpha = 1f
            binding.fragmentLoginLinearLayout4.startAnimation(fadeSlideUp4)
        }, 1100)  // 간격 축소: 1700 -> 1100
    }

    //로그인 수행
    private fun loginWithKakao() {
        val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패: $error")
            } else if (token != null) {
                Log.d(TAG, "로그인 accessToken: ${token.accessToken}")
                sendToServer(token.accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡 로그인 실패: $error")
                    UserApiClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = mCallback
                    )
                } else {
                    Log.d(TAG, "로그인 accessToken: ${token?.accessToken}")
                    sendToServer(token!!.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = mCallback)
        }
    }


    //DB에 회원정보가 없다면 회원가입 수행,
    //회원정보가 존재한다면 로그인 수행합니다.
    //회원가입 직후 바로 로그인되는 로직 추가 필요합니다. (혹은 Toast로 가입 완료 띄우든가...)
    private fun sendToServer(accessToken: String) {
        lifecycleScope.launch {
            try {
                val user = getUserInfo()

                val nickname = user.kakaoAccount?.profile?.nickname ?: "닉네임 없음"
                val email = user.kakaoAccount?.email ?: throw Exception("이메일 정보가 필요합니다.")

                Log.d(TAG, "유저 정보 불러오기 완료: ${nickname} ${email}")
                // 이메일 중복 확인
                val isEmailDuplicate = RetrofitUtil.authService.checkEmail(email)

                if (!isEmailDuplicate) {
                    // 중복이 아닐 경우 회원가입 진행

                    // 회원가입창으로 이동
                    val bundle = Bundle().apply {
                        putString("email", email)
                        putString("nickname", nickname)
                        putString("social", "kakao")
                    }
                    val joinFragment = JoinFragment().apply {
                        arguments = bundle
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.activityLoginFragmentContainer,
                            joinFragment
                        )
                        .addToBackStack(null)
                        .commit()

                } else {
                    try {
                        // 중복일 경우 로그인 처리
                        val isKakao = RetrofitUtil.authService.checkKakao(email)
                        val isGoogle = RetrofitUtil.authService.checkGoogle(email)
//                        val isKakao = true // 나중에 삭제 아직 api가 서버에 안올라왔음
//                        val isGoogle = false // 나중에 삭제 아직 api가 서버에 안올라왔음

                        // 카카오로 회원가입을 진행했더라면 카카오 로그인 진행
                        if (isKakao) {
                            val request = KakaoLoginRequest(accessToken)
                            Log.d(TAG, "카카오 로그인 요청: $request")
                            val response = RetrofitUtil.kakaoService.kakaoLogin(request)
                            Log.d(TAG, "로그인 성공: ${response}")

                            // SharedPreferences에 사용자 정보 저장
                            val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())

                            // null 체크 후 사용자 정보 저장
                            val userInfo = UserInfo(
                                userId = response.userId ?: -1,  // null일 경우 -1L을 기본값으로 사용
                                username = response.userName ?: "",  // null일 경우 빈 문자열 사용
                                role = response.role ?: ""  // null일 경우 빈 문자열 사용
                            )
                            sharedPreferencesUtil.addUser(userInfo)

                            // 저장된 정보 확인을 위한 로그
                            val savedUser = sharedPreferencesUtil.getUser()
                            Log.d(
                                TAG,
                                "저장된 사용자 정보 - userId: ${savedUser.userId}, username: ${savedUser.username}, role: ${savedUser.role}"
                            )

                            // 메인 화면으로 이동
                            navigateToMainActivity()

                        } else if (isGoogle) { // 구글로 회원가입을 진행했더라면
                            // 다른 소셜 로그인 으로 회원가입을 한 적이있다고 표시 하고 로그인창으로 돌려보냄
                            val dialog = Dialog(requireContext())
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog.setContentView(R.layout.dialog_social_login)

                            val socialLoginTitle = dialog.findViewById<TextView>(R.id.social_login_tv_title)
                            socialLoginTitle.text = "Google"
                            socialLoginTitle.setTextColor(Color.parseColor("#5398FF"))

                            dialog.show()

                            val socialLoginBtn = dialog.findViewById<ConstraintLayout>(R.id.logout_d_btn_positive)
                            socialLoginBtn.setOnClickListener {
                                dialog.dismiss()
                            }
                        } else {
                            val dialog = Dialog(requireContext())
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog.setContentView(R.layout.dialog_inapp_login)
                            dialog.show()
    
                            val socialLoginBtn = dialog.findViewById<ConstraintLayout>(R.id.logout_d_btn_positive)
                            socialLoginBtn.setOnClickListener {
                                dialog.dismiss()
                            }
                        }


                    } catch (e: HttpException) {
                        val errorBody = e.response()?.errorBody()?.string()
                        Log.e(TAG, "카카오 로그인 실패 - 상태 코드: ${e.code()}")
                        Log.e(TAG, "에러 응답: $errorBody")

                        val errorMessage = when (e.code()) {
                            400 -> "잘못된 요청입니다"
                            401 -> "인증에 실패했습니다"
                            404 -> "서비스를 찾을 수 없습니다"
                            500 -> "서버 내부 오류가 발생했습니다"
                            else -> "카카오 로그인에 실패했습니다"
                        }

                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "카카오 로그인 중 오류 발생: ${e.message}")
                        Toast.makeText(
                            requireContext(),
                            "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "sendToServer: 서버 요청 실패 ${e.message}")
            }
        }
    }


    // 사용자 email, nickname 불러오기
    private suspend fun getUserInfo(): User {
        Log.d(TAG, "getUserInfo: ")
        return suspendCancellableCoroutine { cont ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "sendToServer: 사용자 정보 조회 실패 $error")
                    cont.resumeWithException(error)
                } else {
                    cont.resume(user ?: throw Exception("User data is null"))
                }
            }
        }
    }


    private fun loginWithGoogle() {
        // 기존 로그인 정보 삭제
        googleSignInClient.signOut().addOnCompleteListener {
            // 로그아웃 완료 후 새로운 로그인 시도
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // Google 로그인 결과 가져오기
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // 로그인 성공시 Google 계정에서 ID 토큰 가져오기
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // 로그인 실패 시 오류 메시지를 표시
                Toast.makeText(requireContext(), "구글 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Google 로그인 성공 후 Firebase 인증 처리
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Google  자격 증명 객체 생성
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Firebase Auth 인증 시도
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // 인증 성공 시 Firebase 사용자 객체 가져오기
                    val user = auth.currentUser
                    
                    // updateUI 호출을 lifecycleScope로 감싸기
                    lifecycleScope.launch {
                        updateUI(user)
                    }
                } else {
                    // 인증 실패 시 오류 메시지 표시
                    Toast.makeText(requireContext(), "Firebase 인증 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 로그인 성공 시 UI 업데이트 및 JoinFragment로 이동
    private suspend fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // 이메일 중복 확인
            val isEmailDuplicate = user.email?.let { RetrofitUtil.authService.checkEmail(it) }

            if (!isEmailDuplicate!!) {
                // 중복이 아닐 경우 회원가입 진행
                // 회원가입창으로 이동
                val bundle = Bundle().apply {
                    putString("email", user.email)
                    putString("nickname", user.displayName)
                    putString("social", "google")
                }
                val joinFragment = JoinFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.activityLoginFragmentContainer, joinFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                try {
                    // 중복일 경우 로그인 처리
                    val email = user.email
                    val isKakao = email?.let { RetrofitUtil.authService.checkKakao(it) }
                    val isGoogle = email?.let { RetrofitUtil.authService.checkGoogle(it) }

                    if (isGoogle == true) {
                        // 로그인 처리
                        val userInfo = email?.let { RetrofitUtil.googleService.googleLogin(it) }
                        
                        // SharedPreferences에 저장
                        val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())

                        val userinfo = UserInfo(
                            userId = userInfo?.userId ?: -1,  // null일 경우 -1L을 기본값으로 사용
                            username = userInfo?.userName ?: "",  // null일 경우 빈 문자열 사용
                            role = userInfo?.role ?: ""  // null일 경우 빈 문자열 사용
                        )

                        sharedPreferencesUtil.addUser(userinfo)

                        // 메인 화면으로 이동
                        navigateToMainActivity()

                    } else if (isKakao == true) {
                        // 다른 소셜 로그인 으로 회원가입을 한 적이있다고 표시 하고 로그인창으로 돌려보냄
                        val dialog = Dialog(requireContext())
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(R.layout.dialog_social_login)
                        
                        val socialLoginTitle = dialog.findViewById<TextView>(R.id.social_login_tv_title)
                        socialLoginTitle.text = "Kakao Talk"
                        socialLoginTitle.setTextColor(Color.parseColor("#ADB135"))
                        
                        dialog.show()

                        val socialLoginBtn = dialog.findViewById<ConstraintLayout>(R.id.logout_d_btn_positive)
                        socialLoginBtn.setOnClickListener {
                            dialog.dismiss()
                        }
                    } else {
                        val dialog = Dialog(requireContext())
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(R.layout.dialog_inapp_login)
                        dialog.show()

                        val socialLoginBtn = dialog.findViewById<ConstraintLayout>(R.id.logout_d_btn_positive)
                        socialLoginBtn.setOnClickListener {
                            dialog.dismiss()
                        }
                        
                    }
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "updateUI: HTTP 오류 발생")
                    Log.e(TAG, "updateUI: 상태 코드 = ${e.code()}")
                    Log.e(TAG, "updateUI: 에러 메시지 = ${e.message()}")
                    Log.e(TAG, "updateUI: 에러 바디 = $errorBody")
                    Log.e(TAG, "updateUI: 요청 URL = ${e.response()?.raw()?.request?.url}")
                    
                    withContext(Dispatchers.Main) {
                        when (e.code()) {
                            500 -> Toast.makeText(requireContext(), "서버 내부 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            400 -> Toast.makeText(requireContext(), "잘못된 요청입니다.", Toast.LENGTH_SHORT).show()
                            401 -> Toast.makeText(requireContext(), "인증에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(requireContext(), "오류가 발생했습니다: ${e.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "updateUI: 일반 오류 발생")
                    Log.e(TAG, "updateUI: 오류 종류 = ${e.javaClass.simpleName}")
                    Log.e(TAG, "updateUI: 오류 메시지 = ${e.message}")
                    Log.e(TAG, "updateUI: 발생 위치", e)  // 스택 트레이스를 통해 정확한 오류 발생 위치 확인
                    
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "예기치 않은 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }   
            }

        } else {
            Toast.makeText(requireContext(), "Firebase 인증 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    /**
     * 입력 필드의 시각적 상태를 업데이트하는 함수
     * @param inputField 업데이트할 EditText
     * @param isError 에러 상태 여부
     * @param isEmpty 필드가 비어있는지 여부
     */
    private fun updateInputFieldState(
        inputField: EditText,
        isError: Boolean,
        isEmpty: Boolean = false
    ) {
        // 상태에 따른 색상 결정
        val color = when {
            isError || isEmpty -> "#F26547"  // 에러 상태나 빈 필드일 때 빨간색
            else -> "#C7C7C7"  // 정상 상태일 때 회색
        }
        val borderColor = when {
            isError || isEmpty -> "#F26547"  // 에러 상태나 빈 필드일 때 빨간색
            else -> "#000000"  // 정상 상태일 때 검정색
        }

        // EditText 스타일 적용
        inputField.apply {
            setHintTextColor(Color.parseColor(color))
            background = GradientDrawable().apply {
                setStroke(1.dpToPx(requireContext()), Color.parseColor(borderColor))
                cornerRadius = 12.dpToPx(requireContext()).toFloat()
                setColor(Color.WHITE)
            }
        }
    }

    /**
     * 로그인 프로세스를 시작하는 함수
     */
    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            try {
                handleLoginAttempt(email, password)
            } catch (e: HttpException) {
                handleLoginError(e)
            }
        }
    }

    /**
     * 실제 로그인 시도를 처리하는 함수
     */
    private suspend fun handleLoginAttempt(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        val response = RetrofitUtil.authService.login(loginRequest)

        // SharedPreferences에 저장
        val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())

        // 쿠키 저장
        response.loginIdCookie?.let { cookie ->
            sharedPreferencesUtil.saveUserCookie(cookie)
        }

        // 사용자 정보 저장
        val userInfo = UserInfo(
            userId = response.userId ?: -1,  // null일 경우 -1L을 기본값으로 사용
            username = response.username ?: "",  // null일 경우 빈 문자열 사용
            role = response.role ?: ""  // null일 경우 빈 문자열 사용
        )
        sharedPreferencesUtil.addUser(userInfo)

        // 저장된 사용자 정보 확인을 위한 로그
        val savedUser = sharedPreferencesUtil.getUser()
        Log.d(
            TAG,
            "저장된 사용자 정보 - userId: ${savedUser.userId}, username: ${savedUser.username}, role: ${savedUser.role}"
        )
        Log.d(TAG, "저장된 사용자 쿠키: ${sharedPreferencesUtil.getUserCookie()}")

        // 메인 화면으로 이동
        navigateToMainActivity()
    }

    /**
     * 메인 액티비티로 이동하는 함수
     */
    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    /**
     * 로그인 에러를 처리하는 함수
     */
    private fun handleLoginError(e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

        when (errorResponse.message) {
            "email 혹은 password 누락" -> handleEmptyFieldsError()
            else -> handleInvalidCredentialsError()
        }
    }

    /**
     * 빈 필드 에러를 처리하는 함수
     * 이메일이나 비밀번호가 입력되지 않았을 때 호출
     */
    private fun handleEmptyFieldsError() {
        val emailEmpty = binding.fragmentLoginEamilInput.text.isEmpty()
        val passwordEmpty = binding.fragmentLoginPasswordInput.text.isEmpty()

        // 각 필드의 상태 업데이트
        updateInputFieldState(
            binding.fragmentLoginEamilInput,
            isError = false,
            isEmpty = emailEmpty
        )
        updateInputFieldState(
            binding.fragmentLoginPasswordInput,
            isError = false,
            isEmpty = passwordEmpty
        )

        Toast.makeText(requireContext(), "email/password를 입력해주세요", Toast.LENGTH_SHORT).show()
    }

    /**
     * 잘못된 인증 정보 에러를 처리하는 함수
     * 이메일/비밀번호가 일치하지 않을 때 호출
     */
    private fun handleInvalidCredentialsError() {
        with(binding) {
            // 입력 필드 초기화
            fragmentLoginPasswordInput.setText("")
            fragmentLoginPasswordInput.requestFocus()

            // 에러 상태로 UI 업데이트
            updateInputFieldState(fragmentLoginEamilInput, isError = true)
            updateInputFieldState(fragmentLoginPasswordInput, isError = true)
        }
        Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}