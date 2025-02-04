package com.ssafy.reper.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.reper.R
import com.ssafy.reper.databinding.FragmentLoginBinding
import com.ssafy.reper.ui.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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

        // 로그인 버튼 클릭 시 로그인 처리
        val loginBtn = binding.fragmentLoginLoginBtn
        loginBtn.setOnClickListener {
            // 로그인 버튼 클릭시 메인 화면으로 이동 - (추후 로직 수정 필요)
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        // ActivityLoginJoinText 클릭 시 새로운 LoginFragment로 이동
        val joinText = binding.ActivityLoginJoinText
        joinText.setOnClickListener {
            // JoinFragment로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.activityLoginFragmentContainer, JoinFragment()) // container ID와 새로운 fragment를 설정
                .addToBackStack(null) // BackStack에 추가 (뒤로가기 시 이전 Fragment로 돌아갈 수 있음)
                .commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}