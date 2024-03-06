package com.example.campusconeect.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.campusconeect.R
import com.example.campusconeect.databinding.ActivityGetStartedBinding
import com.example.campusconeect.databinding.ActivityMainBinding
import com.example.campusconeect.activities.SignInActivity

class GetStartedActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGetStartedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        val getStartBtn: CardView = findViewById(R.id.cvGetStarted)
        getStartBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}