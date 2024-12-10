package com.example.librarypapb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class ProfilePageActivity : AppCompatActivity() {
    private lateinit var newUsername : EditText
    private lateinit var logoutBtn : Button
    private lateinit var changeNameBtn : Button
    private lateinit var backBtn : Button

    private var user : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile_page)

        user = FirebaseAuth.getInstance().currentUser

        newUsername = findViewById(R.id.newUsernameInputText)
        newUsername.setText(user!!.displayName)

        logoutBtn = findViewById(R.id.logOutBtn)
        changeNameBtn = findViewById(R.id.changeNameBtn)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            finish()
        }

        logoutBtn.setOnClickListener {
            signOut()
        }

        changeNameBtn.setOnClickListener {
            changeUsername()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun changeUsername() {
        val newUsername = newUsername.text.toString()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newUsername)
            .build()

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Profile update successful, finish the activity
                    finish()
                } else {
                    // Handle errors, e.g., show an error message
                }
            }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginPageActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}