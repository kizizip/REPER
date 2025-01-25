package com.ssafy.reper.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssafy.reper.R
import com.ssafy.reper.databinding.ActivityMainBinding
import com.ssafy.reper.ui.boss.BossFragment
import com.ssafy.reper.ui.boss.NoticeManageFragment
import com.ssafy.reper.ui.boss.RecipeManageFragment
import com.ssafy.reper.ui.boss.WriteNotiFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bossbtn.setOnClickListener {
            val fragment = BossFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fram_fragment, fragment)
                .addToBackStack(null)
                .commit()


            invisableBtn()
        }


        binding.recipebtn.setOnClickListener {
            val fragment = RecipeManageFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fram_fragment, fragment)
                .addToBackStack(null)
                .commit()


            invisableBtn()
        }


        binding.notibtn.setOnClickListener {
            val fragment = NoticeManageFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fram_fragment, fragment)
                .addToBackStack(null)
                .commit()


            invisableBtn()
        }


        binding.notiwritebtn.setOnClickListener {
            val fragment = WriteNotiFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fram_fragment, fragment)
                .addToBackStack(null)
                .commit()


            invisableBtn()
        }





    }
    fun invisableBtn(){
        binding.bossbtn.visibility = View.GONE
        binding.recipebtn.visibility = View.GONE
        binding.notibtn.visibility = View.GONE
        binding.notiwritebtn.visibility = View.GONE

    }
}