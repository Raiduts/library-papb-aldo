package com.example.librarypapb

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AddBookActivity : AppCompatActivity() {
    private lateinit var titleInput : EditText
    private lateinit var authorInput : EditText
    private lateinit var fileUploadBtn : Button
    private lateinit var addBtn : Button

    private lateinit var backBtn : Button

    lateinit var story : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.add_book)

        titleInput = findViewById(R.id.titleInput)
        authorInput = findViewById(R.id.authorInput)
        fileUploadBtn = findViewById(R.id.fileUploadBtn)
        backBtn = findViewById(R.id.backBtn)

        addBtn = findViewById(R.id.addBtn)

        addBtn.setOnClickListener {
            addBook()
        }

        fileUploadBtn.setOnClickListener {
            openFilePicker()
        }

        backBtn.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun addBook() {
        val db = FirebaseFirestore.getInstance()
        val booksCollection = db.collection("Book")

        val newBookRef = booksCollection.document()

        val book = Book(
            id = newBookRef.id,
            name = titleInput.text.toString(),
            penulis = authorInput.text.toString(),
            contains = story
        )

        newBookRef.set(book)
            .addOnSuccessListener {
                // Book added successfully, you can clear the input fields or show a success message here
                titleInput.text.clear()
                authorInput.text.clear()
                finish()
            }
            .addOnFailureListener { exception ->
                // Handle errors, e.g., show an error message to the user
            }
    }

    fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/text" // Specify the file type
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PICK_TEXT_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_TEXT_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                story = readTextFromUri(uri)
                // Store the content in your desired location
            }
        }
    }

    fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line).append("\n") // Add newline character
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    companion object {
        private const val PICK_TEXT_FILE = 1 // You can choose any integer value
    }
}

