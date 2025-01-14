package com.example.librarypapb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class DashboardPageActivity : AppCompatActivity() {
    lateinit var profileBtn : Button
    lateinit var addBookBtn : Button
    lateinit var backBtn : Button

    private lateinit var username : TextView

    private var user : FirebaseUser? = null

    val db = FirebaseFirestore.getInstance()
    val booksCollection = db.collection("Book")
    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.dashboard_page)

        user = FirebaseAuth.getInstance().currentUser

        username = findViewById(R.id.usernameText)
        username.text = user!!.displayName

        profileBtn = findViewById(R.id.profileBtn)
        addBookBtn = findViewById(R.id.addBookBtn)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            finish()
        }

        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfilePageActivity::class.java)
            startActivity(intent)
        }

        addBookBtn.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById<RecyclerView>(R.id.bookList)
        recyclerView.layoutManager = LinearLayoutManager(this)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("Name", user!!.displayName.toString())
        username.text = user!!.displayName

        ReloadBook()
    }

    private fun ReloadBook(){
        booksCollection.get().addOnSuccessListener { querySnapshot ->
            val bookData = mutableListOf<Book>()
            for (document in querySnapshot.documents) {
                    val book = document.toObject(Book::class.java)
                    book?.id = document.id // Assign the document ID to the book object
                    bookData.add(book!!)
            }
            recyclerView.adapter = MyAdapter(bookData)
        }.addOnFailureListener { exception ->
            Log.w("DashboardPageActivity", "Error getting documents.", exception)
        }
    }
}


data class Book(
    var id: String? = null,
    var name: String? = null,
    var penulis: String? = null,
    val contains: String? = null
)

class MyAdapter(private val data: List<Book>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookTitleTextView: TextView = itemView.findViewById(R.id.bookTitleTextView)
        val bookAuthorTextView: TextView = itemView.findViewById(R.id.bookAuthorTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBook = data[position]
        holder.bookTitleTextView.text = currentBook.name
        holder.bookAuthorTextView.text = currentBook.penulis

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, BookPageActivity::class.java)
            intent.putExtra("BOOK_ID", currentBook.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}