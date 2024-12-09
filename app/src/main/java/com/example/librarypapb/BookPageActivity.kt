package com.example.librarypapb

import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class BookPageActivity : AppCompatActivity() {
    lateinit var title: TextView
    lateinit var author: TextView
    lateinit var contains: TextView
    val db = FirebaseFirestore.getInstance()
    val booksCollection = db.collection("Book")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.book_page)

        title = findViewById(R.id.Title)
        author = findViewById(R.id.textView3)
        contains = findViewById(R.id.bookContains)

        val bookId = intent.getStringExtra("BOOK_ID")

        if (bookId != null) {
            val db = FirebaseFirestore.getInstance()
            val booksCollection = db.collection("Book")
            val bookDocRef = booksCollection.document(bookId)

            bookDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val book = documentSnapshot.toObject(Book::class.java)
                    title.text = book?.name
                    author.text = book?.penulis
                    contains.text = book?.contains?.replace("\\n", "\n\n")
                    // ... (fill other TextViews as needed)
                } else {
                    // Handle case where document doesn't exist
                }
            }.addOnFailureListener { exception ->
                // Handle errors
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}