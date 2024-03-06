package com.example.campusconeect.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.campusconeect.R
import com.example.campusconeect.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val textView = binding.name

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            textView.text = "Welcome, " + userName
        } else {
            textView.text = "You are not a user"
        }


        val signOutButton: Button = findViewById(R.id.btnSignInWithGoogle)
        signOutButton.setOnClickListener {
            signOutAndStartSignInActivity()
        }

        // Update visibility of buttons based on user role
        updateButtonVisibility()
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this) {
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateButtonVisibility() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userRole = document.getString("role")
                        updateUserButtons(userRole)
                    } else {
                        // Document does not exist or role field is missing
                        // Hide all buttons
                        updateUserButtons(null)
                    }
                }
                .addOnFailureListener { exception ->
                    // Failed to get user document
                    // Hide all buttons
                    updateUserButtons(null)
                }
        } ?: run {
            // User is not authenticated
            // Hide all buttons
            updateUserButtons(null)
        }
    }

    private fun updateUserButtons(userRole: String?) {
        val studentButton: Button = findViewById(R.id.studentButton)
        val organizerButton: Button = findViewById(R.id.organizerButton)
        val adminButton: Button = findViewById(R.id.adminButton)

        when (userRole) {
            "student" -> {
                studentButton.visibility = Button.VISIBLE
                organizerButton.visibility = Button.GONE
                adminButton.visibility = Button.GONE
            }

            "organizer" -> {
                studentButton.visibility = Button.VISIBLE
                organizerButton.visibility = Button.VISIBLE
                adminButton.visibility = Button.GONE
            }

            "admin" -> {
                studentButton.visibility = Button.VISIBLE
                organizerButton.visibility = Button.VISIBLE
                adminButton.visibility = Button.VISIBLE
            }

            else -> {
                studentButton.visibility = Button.GONE
                organizerButton.visibility = Button.GONE
                adminButton.visibility = Button.GONE
            }
        }
    }
}

