package com.example.librarypapb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterPageActivity : AppCompatActivity() {

    private lateinit var emailReg : EditText
    private lateinit var passwordReg : EditText
    private lateinit var passwordVerificationReg : EditText
    private lateinit var registerBtn : Button
    private lateinit var backBtn : Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register_page)

        emailReg = findViewById(R.id.emailReg)
        passwordReg = findViewById(R.id.passwordReg)
        passwordVerificationReg = findViewById(R.id.passwordVerificationReg)
        registerBtn = findViewById(R.id.button)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            finish()
        }

        auth = Firebase.auth

        registerBtn.setOnClickListener {
            val email = emailReg.text.toString()
            val password = passwordReg.text.toString()
            val passwordVerification = passwordVerificationReg.text.toString()

            // Input validation (e.g., check for empty fields, password match)
            if (email.isEmpty() || password.isEmpty() || password != passwordVerification) {
                // Handle invalid input (e.g., show error message)
                Toast.makeText(baseContext, "Password tidak sesuai", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration success
                        Log.d("RegisterActivity", "createUserWithEmail:success")
                        val user = auth.currentUser
                        // Handle successful registration (e.g., navigate to next screen)

                        Toast.makeText(baseContext, "REGISTER SUCCESS", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, LoginPageActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Registration failed
                        Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "REGISTER FAILED", Toast.LENGTH_SHORT).show()
                        // Handle registration error (e.g., show error message)
                    }
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}