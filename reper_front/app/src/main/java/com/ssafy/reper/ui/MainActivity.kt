package com.ssafy.reper.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.reper.R
import com.ssafy.reper.databinding.ActivityMainBinding
import com.ssafy.reper.ui.recipe.FullRecipeFragment
import com.ssafy.reper.ui.home.HomeFragment
import com.ssafy.reper.ui.mypage.MyPageFragment
import com.ssafy.reper.ui.order.OrderFragment
import com.ssafy.reper.ui.recipe.AllRecipeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0    // 뒤로가기 버튼을 누른 시간 저장

    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()

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
                // RecipeFragment로 이동
                R.id.recipe_icon -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.activityMainFragmentContainer, AllRecipeFragment())
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
                // MyPageFragment로 이동
                R.id.mypage_icon -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.activityMainFragmentContainer, MyPageFragment())
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

    // binding의 bottomMenu에 접근하기 위한 public 메서드
    fun getBottomNavigationView(): BottomNavigationView {
        return binding.activityMainBottomMenu
    }


    // backstack에 아무것도없는 상태에서 뒤로가기 버튼을 눌렀을때
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // 현재 BackStack에 있는 Fragment 개수 확인
        if (supportFragmentManager.backStackEntryCount == 0) {
            // 2초 이내에 뒤로가기 버튼을 한 번 더 누르면 앱 종료
            if (System.currentTimeMillis() - backPressedTime < 2000) {
                finish()
                return
            }
            
            // 뒤로가기 버튼을 처음 누를 때
            Toast.makeText(this, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }
}