package com.example.campusconeect.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campusconeect.R
import com.example.campusconeect.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private var binding: ActivitySignInBinding? = null
    private val RC_SIGN_IN = 123
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Define allowed email IDs for organizers
    private val allowedOrganizerEmails = listOf("amandeep0904@gmail.com", "sunraysystems@gmail.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding?.btnSignInWithGoogle?.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email

            if (email != null && (email.endsWith("@srmist.edu.in") || email in allowedOrganizerEmails || email == "amandeep.patro04@gmail.com")) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                showProgressDialog("Signing In...")
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            if (user != null) {
                                val role = when {
                                    email.endsWith("@srmist.edu.in") -> "student"
                                    email in allowedOrganizerEmails -> "organizer"
                                    email == "amandeep.patro04@gmail.com" -> "admin"
                                    else -> "default" // or any other default role
                                }
                                createUserInFirestore(user.uid, email, role)
                            } else {
                                hideProgressDialog()
                                showToast("Sign-in failed. Please try again.")
                            }
                        } else {
                            hideProgressDialog()
                            showToast("Sign-in error: ${task.exception?.message}")
                        }
                    }
            } else {
                showToast("You are not authorized to sign in with this email.")
                // Reset UI to allow user to try again
                googleSignInClient.signOut()
                googleSignInClient.revokeAccess()
            }
        } catch (e: ApiException) {
            hideProgressDialog()
            showToast("Sign-in error: ${e.message}")
        }
    }


    private fun createUserInFirestore(uid: String, email: String, role: String) {
        val userInfo = hashMapOf(
            "email" to email,
            "role" to role
        )

        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(userInfo)
            .addOnSuccessListener {
                hideProgressDialog()
                navigateToMainActivity()
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                showToast("Error creating user: ${exception.message}")
            }
    }

    private var progressDialog: ProgressDialog? = null

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(message)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
