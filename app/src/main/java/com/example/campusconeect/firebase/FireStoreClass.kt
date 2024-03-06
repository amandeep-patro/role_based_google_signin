package com.example.campusconeect.firebase
//
//import android.widget.Toast
//import com.example.campusconeect.contants.Constants
//import com.example.campusconeect.models.User
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.campusconeect.DocumentSnapshot
//import com.google.firebase.campusconeect.FirebaseFirestore
//import com.google.firebase.campusconeect.SetOptions
//import java.text.SimpleDateFormat
//import java.util.*
//
//class FireStoreClass {
//
//    private val mFireStore = FirebaseFirestore.getInstance()
//
//    fun registerUser(userInfo: User)
//    {
//        mFireStore.collection(Constants.USERS)
//            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
//    }
//
//    fun getCurrentUserId(): String
//    {
//        val currentUser= FirebaseAuth.getInstance().currentUser
//        var currentUserId = ""
//        if (currentUser!=null)
//            currentUserId = currentUser.uid
//        return currentUserId
//    }
//
//
//
//}