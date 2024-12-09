package com.example.librarypapb

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.text.substringBefore

class LoginPageActivity : AppCompatActivity() {

    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button

    private lateinit var googleBtn : Button
    private lateinit var facebookBtn : Button
    private lateinit var twitterBtn : Button

    private lateinit var auth: FirebaseAuth
    private var user : FirebaseUser? = null

    companion object {
        private const val RC_SIGN_IN = 9001  // Choose any unique integer value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_page)
        Log.d("LOGIN", "starting")

        auth = Firebase.auth

        user = FirebaseAuth.getInstance().currentUser
        checkAccount()

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registerBtn)

        googleBtn = findViewById(R.id.googleBtn)
        facebookBtn = findViewById(R.id.facebookBtn)
        twitterBtn = findViewById(R.id.twitterBtn)

        loginBtn.setOnClickListener {
            loginUser()
        }

        registerBtn.setOnClickListener {
            register()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)

        googleBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun logInSuccess(){
        grantDisplayName(user)

        val intent = Intent(this, DashboardPageActivity::class.java)
        startActivity(intent)
    }

    private fun register(){
        val intent = Intent(this, RegisterPageActivity::class.java)
        startActivity(intent)
    }

    private fun checkAccount() {
        if (user != null) {
            logInSuccess()
        } else {
            // User is not signed in
            // ... display login screen, disable features, etc. ...
        }
    }

    private fun loginUser() {
        Log.d("LOGIN", "try loging in")
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Validate input (e.g., check for empty fields)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LOGIN", "signInWithEmail:success")
                    logInSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun grantDisplayName(user: FirebaseUser?){
        if (user != null && user.displayName != null && user.displayName != "") {
            Log.d("currentUser", "ini displayName " + user.displayName.toString())
            return
        }

        val email = user?.email // Get the user's email address
        val displayName = email?.substringBefore("@") // Extract the part before "@"

        if (user != null) {
            Log.d("currentUser","inside" + displayName.toString())
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName) // Set the extracted part as the display name
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Profile updated successfully
                    // Access the updated displayName here
                    val updatedUser = FirebaseAuth.getInstance().currentUser
                    Log.d("currentUser", "ini user " + updatedUser?.displayName.toString())
                } else {
                    // Handle error
                    // ...
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.w("GOOGLELOGIN", "trying")

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                Log.w("GOOGLELOGIN", "trying 2")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("GOOGLELOGIN", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        Log.w("GOOGLELOGIN", "trying 3")
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                Log.w("GOOGLELOGIN", "trying 4")
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("GOOGLELOGIN", "signInWithCredential:success")
                    val user = auth.currentUser
                    logInSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("GOOGLELOGIN", "signInWithCredential:failure")
                }
            }
    }
}