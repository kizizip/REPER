package com.ssafy.reper.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.reper.R
import com.ssafy.reper.databinding.ActivityMainBinding
import com.ssafy.reper.ui.recipe.FullRecipeFragment

class MainActivity : AppCompatActivity() {
    private val mainBinding : ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 초기 화면은 홈으로
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_flayout, FullRecipeFragment())
            .commit()
    }
}