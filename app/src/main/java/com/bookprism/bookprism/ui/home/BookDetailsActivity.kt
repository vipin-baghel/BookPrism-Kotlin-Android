package com.bookprism.bookprism.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bookprism.bookprism.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_book_details.*
import java.util.*

class BookDetailsActivity : AppCompatActivity() {

    var name: String? = null
    var author: String? = null
    var rating: String? = null
    var cost: String? = null
    var comment: String? = null
    var mAuth: FirebaseAuth? = null
    var currentUser: FirebaseUser? = null
    var db: FirebaseFirestore? = null
    var progressBar: ProgressBar? = null
    var imgUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val i = intent
        name = i.getStringExtra("name")
        author = i.getStringExtra("author")
        rating = i.getStringExtra("rating")
        cost = i.getStringExtra("cost")
        comment = i.getStringExtra("comment")
        imgUrl = i.getStringExtra("imgUrl")

        pic.setImageURI(imgUrl)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        db = FirebaseFirestore.getInstance()
        progressBar = findViewById(R.id.progressbar)
        progressBar?.visibility = View.GONE
        val nameTV = findViewById<TextView>(R.id.name_tv)
        val authorTV = findViewById<TextView>(R.id.author_tv)
        val ratingTV = findViewById<TextView>(R.id.rating_tv)
        val costTV = findViewById<TextView>(R.id.cost_tv)
        val commentTv = findViewById<TextView>(R.id.comment_tv)
        pic.setImageURI(imgUrl)
        nameTV.text = name
        authorTV.text = "Author : $author"
        ratingTV.text = "Rating : $rating"
        costTV.text = "Cost : ₹$cost"
        commentTv.text = "Comment : $comment"
        val data: MutableMap<String, Any?> = HashMap()
        data["name"] = name
        data["author"] = author
        data["rating"] = rating
        data["cost"] = cost
        data["imgUrl"] = imgUrl
        data["comment"] = comment
        data["lentBy"] = currentUser!!.displayName

        val lendBtn = findViewById<Button>(R.id.lend_btn)
        lendBtn.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            db!!.collection("lent")
                    .add(data)
                    .addOnSuccessListener {
                        db!!.collection("users")
                                .document(currentUser!!.uid)
                                .collection("lentBooksByMe")
                                .add(data)
                                .addOnSuccessListener {
                                    progressBar?.visibility = View.GONE
                                    Toast.makeText(applicationContext, "Done", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    progressBar?.visibility = View.GONE
                                    Toast.makeText(applicationContext, "Failed with exception : $e", Toast.LENGTH_LONG).show()
                                }
                    }
                    .addOnFailureListener { e ->
                        progressBar?.visibility = View.GONE
                        Toast.makeText(applicationContext, "Failed with exception : $e", Toast.LENGTH_LONG).show()
                    }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}