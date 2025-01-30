package com.ssafy.reper.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.reper.R
import com.ssafy.reper.databinding.ActivityMainBinding
import com.ssafy.reper.ui.home.HomeFragment
import com.ssafy.reper.ui.order.OrderFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 Fragment 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.activityMainFragmentContainer, HomeFragment())
            .commit()

        // bottom_menue의 아이템 선택 리스너 설정
        binding.activityMainBottomMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // HomeFragment로 이동
                R.id.home_icon -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.activityMainFragmentContainer, HomeFragment())
                        .commit()
                    true
                }
                // OrderFragment로 이동
                R.id.order_icon -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.activityMainFragmentContainer, OrderFragment())
                        .commit()
                    true
                }
                else -> false // 그 외의 경우 처리해 주지 않음
            }
        }

    }

    fun hideBottomNavigation() {
        binding.activityMainBottomMenu.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.activityMainBottomMenu.visibility = View.VISIBLE
    }
}